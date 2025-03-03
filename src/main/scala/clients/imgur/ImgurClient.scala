package clients.imgur

import cats.effect.{IO, Resource}
import clients.imgur.Codecs._
import clients.imgur.Models.ImgurResponse
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.headers.`Content-Type`
import org.http4s.{Headers, MediaType, Method, Request, Uri}

trait ImgurClient {
  def uploadImage(
      imageBytes: Array[Byte],
      title: Option[String] = None,
      description: Option[String] = None
  ): IO[ImgurResponse]
}

object ImgurClient {
  def make(): Resource[IO, ImgurClient] =
    EmberClientBuilder.default[IO].build.map(new ImgurClientImpl(_))

  private class ImgurClientImpl(client: Client[IO]) extends ImgurClient {
    private val baseUri = Uri.unsafeFromString("https://api.imgur.com/3/image")

    def uploadImage(
        imageBytes: Array[Byte],
        title: Option[String] = None,
        description: Option[String] = None
    ): IO[ImgurResponse] = {
      val request = Request[IO](
        method = Method.POST,
        uri = baseUri,
        headers = Headers(
          `Content-Type`(MediaType.image.jpeg)
        )
      ).withEntity(imageBytes)

      // TODO: error handling
      client.expect[ImgurResponse](request)
    }
  }
}
