package clients.imgur

import cats.data.EitherT
import cats.effect.{IO, Resource}
import clients.imgur.Codecs._
import clients.imgur.Errors.ImgurError
import clients.imgur.Models.ImgurResponse
import org.http4s.client.{Client, UnexpectedStatus}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.headers.`Content-Type`
import org.http4s.{
  Headers,
  InvalidMessageBodyFailure,
  MediaType,
  Method,
  Request,
  Status,
  Uri
}

trait ImgurClient {

  /** Uploads an image to Imgur.
    *
    * @param imageBytes The raw image bytes to upload
    * @param title Optional title for the image
    * @param description Optional description for the image
    * @return Either an ImgurError or a successful ImgurResponse
    */
  def uploadImage(
      imageBytes: Array[Byte],
      title: Option[String] = None,
      description: Option[String] = None
  ): EitherT[IO, ImgurError, ImgurResponse]
}

object ImgurClient {
  def make(): Resource[IO, ImgurClient] =
    EmberClientBuilder.default[IO].build.map(new ImgurClientImpl(_))

  private class ImgurClientImpl(client: Client[IO]) extends ImgurClient {
    private val baseUri = Uri.unsafeFromString("https://api.imgur.com/3/image")

    /** Enhanced error handler for HTTP requests and IO operations.
      *
      * @param attempt The attempted IO operation
      * @return An IO containing either an ImgurError or the successful result
      */
    private def handleErrors[A](
        attempt: IO[A]
    ): IO[Either[ImgurError, A]] = {
      attempt.attempt.map {
        case Right(result) => Right(result)

        // JSON decoding errors
        case Left(error: InvalidMessageBodyFailure) =>
          Left(
            ImgurError.JsonDecodingError(
              s"Failed to decode response: ${error.getMessage}"
            )
          )

        // HTTP status errors
        case Left(error: UnexpectedStatus) =>
          error.status match {
            case Status.Unauthorized =>
              Left(
                ImgurError.AuthenticationError(
                  s"Authentication failed with status ${error.status.code}: ${error.status.reason}"
                )
              )
            case Status.Forbidden =>
              Left(
                ImgurError.AuthenticationError(
                  s"Authorization failed with status ${error.status.code}: ${error.status.reason}"
                )
              )
            case Status.TooManyRequests =>
              Left(
                ImgurError.RateLimitError(
                  s"Rate limit exceeded: ${error.status.reason}"
                )
              )
            case Status.BadRequest =>
              Left(
                ImgurError.ValidationError(
                  s"Bad request: ${error.status.reason}"
                )
              )
            case Status.PayloadTooLarge =>
              Left(
                ImgurError.PayloadTooLargeError(
                  s"Image size too large: ${error.status.reason}"
                )
              )
            case Status.NotFound =>
              Left(
                ImgurError.NotFoundError(
                  s"Resource not found: ${error.status.reason}"
                )
              )
            case _ =>
              Left(
                ImgurError.ApiError(
                  s"API returned unexpected status: ${error.status.code} - ${error.status.reason}"
                )
              )
          }

        // Network-specific errors
        case Left(error: java.net.ConnectException) =>
          Left(
            ImgurError.NetworkError(
              s"Failed to connect to server: ${error.getMessage}"
            )
          )
        case Left(error: java.net.SocketTimeoutException) =>
          Left(
            ImgurError.NetworkError(
              s"Connection to server timed out: ${error.getMessage}"
            )
          )
        case Left(error: java.io.IOException) =>
          Left(
            ImgurError.NetworkError(
              s"I/O error during request: ${error.getMessage}"
            )
          )

        // Fallback for other errors
        case Left(error) =>
          Left(
            ImgurError.UnknownError(s"Unexpected error: ${error.getMessage}")
          )
      }
    }

    /** Uploads an image to Imgur.
      */
    def uploadImage(
        imageBytes: Array[Byte],
        title: Option[String] = None,
        description: Option[String] = None
    ): EitherT[IO, ImgurError, ImgurResponse] = {
      // Build the URI with optional parameters
      val uri = (title, description) match {
        case (Some(t), Some(d)) =>
          baseUri
            .withQueryParam("title", t)
            .withQueryParam("description", d)
        case (Some(t), None) =>
          baseUri.withQueryParam("title", t)
        case (None, Some(d)) =>
          baseUri.withQueryParam("description", d)
        case (None, None) =>
          baseUri
      }

      val request = Request[IO](
        method = Method.POST,
        uri = uri,
        headers = Headers(
          `Content-Type`(MediaType.image.jpeg)
        )
      ).withEntity(imageBytes)

      EitherT(handleErrors(client.expect[ImgurResponse](request)))
    }
  }
}
