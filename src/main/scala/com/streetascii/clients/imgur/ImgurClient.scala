package com.streetascii.clients.imgur

import cats.data.EitherT
import cats.effect.{IO, Resource}
import com.streetascii.clients.imgur.Codecs._
import com.streetascii.clients.imgur.Errors.ImgurError
import com.streetascii.clients.imgur.Models.{ClientId, ImgurResponse}
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
    * @param imageBytes
    *   The raw image bytes to upload
    * @return
    *   Either an ImgurError or a successful ImgurResponse
    */
  def uploadImage(
      imageBytes: Array[Byte]
  ): EitherT[IO, ImgurError, ImgurResponse]
}

object ImgurClient {
  def make(clientId: ClientId): Resource[IO, ImgurClient] =
    EmberClientBuilder.default[IO].build.map(new ImgurClientImpl(_, clientId))

  def makeRequest(
      uri: Uri,
      imageBytes: Array[Byte],
      clientId: ClientId
  ): Request[IO]#Self = {
    val authHeader = s"Client-ID ${clientId.value}"
    Request[IO](
      method = Method.POST,
      uri = uri,
      headers = Headers(
        `Content-Type`(
          MediaType.image.png
        ), // You might want to use png instead of jpeg for ASCII art
        org.http4s.Header
          .Raw(org.http4s.headers.Authorization.name, authHeader)
      )
    ).withEntity(imageBytes)
  }

  def handleErrors[A](
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

  private class ImgurClientImpl(client: Client[IO], clientId: ClientId)
      extends ImgurClient {
    private val baseUri = Uri.unsafeFromString("https://api.imgur.com/3/image")

    /** Uploads an image to Imgur.
      */
    def uploadImage(
        imageBytes: Array[Byte]
    ): EitherT[IO, ImgurError, ImgurResponse] = {
      // Build the URI with optional parameters
      val request = makeRequest(baseUri, imageBytes, clientId)

      EitherT(handleErrors(client.expect[ImgurResponse](request)))
    }
  }
}
