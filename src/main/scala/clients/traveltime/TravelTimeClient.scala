package clients.traveltime

import cats.data.EitherT
import cats.effect.{IO, Resource}
import clients.traveltime.Errors._
import clients.traveltime.Codecs._
import clients.traveltime.Models._
import org.http4s.client.{Client, UnexpectedStatus}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.headers.Accept
import org.http4s.{
  Header,
  Headers,
  InvalidMessageBodyFailure,
  MediaType,
  Method,
  Request,
  Status,
  Uri
}
import org.typelevel.ci.CIString

trait TravelTimeClient {
  def geocodingSearch(
      query: String
  ): EitherT[IO, TravelTimeError, TravelTimeGeocodingResponse]
}

object TravelTimeClient {
  def make(appId: AppId, apiKey: ApiKey): Resource[IO, TravelTimeClient] =
    EmberClientBuilder
      .default[IO]
      .build
      .map(new TravelTimeClientImpl(_, appId, apiKey))

  private class TravelTimeClientImpl(
      client: Client[IO],
      appId: AppId,
      apiKey: ApiKey
  ) extends TravelTimeClient {
    private val baseUri =
      Uri.unsafeFromString("https://api.traveltimeapp.com/v4/geocoding/search")

    private def handleErrors[A](
        attempt: IO[A]
    ): IO[Either[TravelTimeError, A]] = {
      attempt.attempt.map {
        case Right(result) => Right(result)

        // JSON decoding errors
        case Left(error: InvalidMessageBodyFailure) =>
          Left(
            TravelTimeError.JsonDecodingError(
              s"Failed to decode response: ${error.getMessage}"
            )
          )

        // HTTP status errors
        case Left(error: UnexpectedStatus) =>
          error.status match {
            case Status.Unauthorized =>
              Left(
                TravelTimeError.AuthenticationError(
                  s"Authentication failed with status ${error.status.code}: ${error.status.reason}"
                )
              )
            case Status.Forbidden =>
              Left(
                TravelTimeError.AuthenticationError(
                  s"Authorization failed with status ${error.status.code}: ${error.status.reason}"
                )
              )
            case Status.TooManyRequests =>
              Left(
                TravelTimeError.RateLimitError(
                  s"Rate limit exceeded: ${error.status.reason}"
                )
              )
            case Status.BadRequest =>
              Left(
                TravelTimeError.ValidationError(
                  s"Bad request: ${error.status.reason}"
                )
              )
            case Status.PayloadTooLarge =>
              Left(
                TravelTimeError.PayloadTooLargeError(
                  s"Image size too large: ${error.status.reason}"
                )
              )
            case Status.NotFound =>
              Left(
                TravelTimeError.NotFoundError(
                  s"Resource not found: ${error.status.reason}"
                )
              )
            case _ =>
              Left(
                TravelTimeError.ApiError(
                  s"API returned unexpected status: ${error.status.code} - ${error.status.reason}"
                )
              )
          }

        // Network-specific errors
        case Left(error: java.net.ConnectException) =>
          Left(
            TravelTimeError.NetworkError(
              s"Failed to connect to server: ${error.getMessage}"
            )
          )
        case Left(error: java.net.SocketTimeoutException) =>
          Left(
            TravelTimeError.NetworkError(
              s"Connection to server timed out: ${error.getMessage}"
            )
          )
        case Left(error: java.io.IOException) =>
          Left(
            TravelTimeError.NetworkError(
              s"I/O error during request: ${error.getMessage}"
            )
          )

        // Fallback for other errors
        case Left(error) =>
          Left(
            TravelTimeError.UnknownError(
              s"Unexpected error: ${error.getMessage}"
            )
          )
      }
    }

    def geocodingSearch(
        query: String
    ): EitherT[IO, TravelTimeError, TravelTimeGeocodingResponse] = {
      // Build the URI with optional parameters
      val uri = baseUri
        .withQueryParam("query", query)

      val request = Request[IO](
        method = Method.GET,
        uri = uri,
        headers = Headers(
          Accept(MediaType.application.json),
          Header.Raw(
            CIString("X-Application-Id"),
            appId.value
          ),
          Header.Raw(
            CIString("X-Api-Key"),
            apiKey.value
          )
        )
      )

      EitherT(
        handleErrors(client.expect[TravelTimeGeocodingResponse](request))
      )
    }
  }
}
