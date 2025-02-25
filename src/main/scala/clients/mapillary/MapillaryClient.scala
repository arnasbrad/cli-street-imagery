package clients.mapillary

import cats.data.EitherT
import cats.effect.{IO, Resource}
import clients.mapillary.Codecs._
import clients.mapillary.Errors.MapillaryError
import clients.mapillary.Models.MapillaryImageDetails
import org.http4s.Method.GET
import org.http4s.circe.jsonOf
import org.http4s.client.{Client, UnexpectedStatus}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.{EntityDecoder, InvalidMessageBodyFailure, Request, Uri}

trait MapillaryClient {
  def getImage(imageId: String): EitherT[IO, MapillaryError, Array[Byte]]
}

object MapillaryClient {
  def make(): Resource[IO, MapillaryClient] =
    EmberClientBuilder.default[IO].build.map(new MapillaryClientImpl(_))

  private final class MapillaryClientImpl(client: Client[IO])
      extends MapillaryClient {
    private val baseUri = Uri.unsafeFromString("https://graph.mapillary.com")
    private val apiKey = "enter-api-key"

    private def getImageDetails(
        imageId: String
    ): EitherT[IO, MapillaryError, MapillaryImageDetails] = {
      val fields = List(
        "id",
        "thumb_2048_url",
        "thumb_original_url"
      ).mkString(",")

      val imageUri = baseUri
        .addPath(imageId)
        .withQueryParam("fields", fields)
        .withQueryParam("access_token", apiKey)

      val request = Request[IO](
        method = GET,
        uri = imageUri
      )

      implicit val entityDecoder: EntityDecoder[IO, MapillaryImageDetails] =
        jsonOf[IO, MapillaryImageDetails]

      val res = client
        .expect[MapillaryImageDetails](request)
        .attempt
        .map {
          case Right(details) => Right(details)
          case Left(error: InvalidMessageBodyFailure) =>
            Left(
              MapillaryError.JsonDecodingError(
                s"Failed to decode response: ${error.getMessage}"
              )
            )
          case Left(error: UnexpectedStatus) =>
            error.status.code match {
              case 401 | 403 =>
                Left(
                  MapillaryError.AuthenticationError(
                    s"Authentication failed with status ${error.status.code}: ${error.status.reason}"
                  )
                )
              case 429 =>
                Left(
                  MapillaryError.RateLimitError(
                    s"Rate limit exceeded: ${error.status.reason}"
                  )
                )
              case _ =>
                Left(
                  MapillaryError.ApiError(
                    s"API returned unexpected status: ${error.status.code} - ${error.status.reason}"
                  )
                )
            }
          case Left(error) =>
            Left(
              MapillaryError.NetworkError(s"Network error: ${error.getMessage}")
            )
        }
      EitherT(res)
    }

    private def getImageFromUrl(
        url: String
    ): EitherT[IO, MapillaryError, Array[Byte]] = {
      val uri = Uri.unsafeFromString(url)
      val request = Request[IO](
        method = GET,
        uri = uri
      )

      EitherT(
        client
          .expect[Array[Byte]](request)
          .attempt
          .map {
            case Right(imageBytes) => Right(imageBytes)
            case Left(error: UnexpectedStatus) =>
              error.status.code match {
                case 401 | 403 =>
                  Left(
                    MapillaryError.AuthenticationError(
                      s"Authentication failed with status ${error.status.code}: ${error.status.reason}"
                    )
                  )
                case 429 =>
                  Left(
                    MapillaryError.RateLimitError(
                      s"Rate limit exceeded: ${error.status.reason}"
                    )
                  )
                case _ =>
                  Left(
                    MapillaryError.ApiError(
                      s"Image server returned unexpected status: ${error.status.code} - ${error.status.reason}"
                    )
                  )
              }
            case Left(error: java.net.ConnectException) =>
              Left(
                MapillaryError.NetworkError(
                  s"Failed to connect to image server: ${error.getMessage}"
                )
              )
            case Left(error: java.net.SocketTimeoutException) =>
              Left(
                MapillaryError.NetworkError(
                  s"Connection to image server timed out: ${error.getMessage}"
                )
              )
            case Left(error: java.io.IOException) =>
              Left(
                MapillaryError.NetworkError(
                  s"I/O error when downloading image: ${error.getMessage}"
                )
              )
            case Left(error) =>
              Left(
                MapillaryError.UnexpectedError(
                  s"Unexpected error when downloading image: ${error.getMessage}"
                )
              )
          }
      )
    }

    override def getImage(
        imageId: String
    ): EitherT[IO, MapillaryError, Array[Byte]] = {
      for {
        details <- getImageDetails(imageId)

        imageUrl <- details.thumb2048Url match {
          case Some(url) => EitherT.rightT[IO, MapillaryError](url)
          case None =>
            EitherT.leftT[IO, String](
              MapillaryError.NotFoundError(
                s"No thumbnail URL available for image ID: $imageId"
              )
            )
        }

        imageBytes <- getImageFromUrl(imageUrl)
      } yield imageBytes
    }
  }
}
