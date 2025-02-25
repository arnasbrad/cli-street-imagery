package clients.mapillary

import cats.effect.{IO, Resource}
import clients.mapillary.Codecs._
import clients.mapillary.Errors.NoImageThumbnailFoundException
import clients.mapillary.Models.MapillaryImageDetails
import org.http4s.Method.GET
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.{EntityDecoder, Request, Uri}

trait MapillaryClient {
  def getImage(imageId: String): IO[Array[Byte]]
}

object MapillaryClient {
  def make(): Resource[IO, MapillaryClient] =
    EmberClientBuilder.default[IO].build.map(new MapillaryClientImpl(_))

  private final class MapillaryClientImpl(client: Client[IO])
      extends MapillaryClient {
    private val baseUri = Uri.unsafeFromString("https://graph.mapillary.com")
    private val apiKey = "enter-api-key"

    // Get image details with all available fields
    private def getImageDetails(imageId: String): IO[MapillaryImageDetails] = {
      // Define the fields we want to request
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

      client.expect[MapillaryImageDetails](request)
    }

    // Get image bytes from a URL
    private def getImageFromUrl(url: String): IO[Array[Byte]] = {
      val uri = Uri.unsafeFromString(url)
      val request = Request[IO](
        method = GET,
        uri = uri
      )

      client.expect[Array[Byte]](request)
    }

    // Fetch an image from Mapillary by ID - returns the full byte array
    override def getImage(imageId: String): IO[Array[Byte]] = {
      // First get the image details to get the URL
      getImageDetails(imageId).flatMap { details =>
        details.thumb2048Url match {
          case Some(url) =>
            getImageFromUrl(url)
          case None =>
            IO.raiseError(
              new NoImageThumbnailFoundException(imageId)
            )
        }
      }
    }
  }
}
