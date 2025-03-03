package clients.mapillary

import cats.data.EitherT
import cats.effect.{IO, Resource}
import clients.mapillary.Codecs._
import clients.mapillary.Errors.MapillaryError
import clients.mapillary.Models._
import common.Models._
import org.http4s.Method.GET
import org.http4s.client.{Client, UnexpectedStatus}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.{Header, InvalidMessageBodyFailure, Request, Uri}
import org.typelevel.ci.CIString

trait MapillaryClient {
  def getImage(
      imageId: String,
      apiKey: String
  ): EitherT[IO, MapillaryError, Array[Byte]]

  def getImageIdsByLocation(
      coordinates: Coordinates,
      radiusMeters: Radius,
      apiKey: String
  ): EitherT[IO, MapillaryError, List[String]]
}

object MapillaryClient {
  def make(): Resource[IO, MapillaryClient] =
    EmberClientBuilder.default[IO].build.map(new MapillaryClientImpl(_))

  private final class MapillaryClientImpl(client: Client[IO])
      extends MapillaryClient {
    private val baseUri = Uri.unsafeFromString("https://graph.mapillary.com")

    // TODO: use oauth instead of http param. And use AppConfig
    private def getImageDetails(
        imageId: String,
        apiKey: String
    ): EitherT[IO, MapillaryError, MapillaryImageDetails] = {
      val fields = List(
        "id",
        "thumb_2048_url",
        "thumb_original_url"
      ).mkString(",")

      val imageUri = baseUri
        .addPath(imageId)
        .withQueryParam("fields", fields)

      val request = Request[IO](
        method = GET,
        uri = imageUri
      ).withHeaders(Header.Raw(CIString("Authorization"), s"OAuth $apiKey"))

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
        imageId: String,
        apiKey: String
    ): EitherT[IO, MapillaryError, Array[Byte]] = {
      for {
        details <- getImageDetails(imageId, apiKey)

        imageUrl <- details.thumbOriginalUrl match {
          case Some(url) => EitherT.rightT[IO, MapillaryError](url)
          case None =>
            EitherT.leftT[IO, String](
              MapillaryError.NotFoundError(
                s"No thumbnail URL available for image ID: $imageId"
              )
            )
        }
        _ = println(imageUrl)
        imageBytes <- getImageFromUrl(imageUrl)
      } yield imageBytes
    }

    override def getImageIdsByLocation(
        coordinates: Coordinates,
        radiusMeters: Radius,
        apiKey: String
    ): EitherT[IO, MapillaryError, List[String]] = {
      // Convert meters to degrees (approximate)
      // 1 degree of latitude is approximately 111,320 meters
      // 1 degree of longitude varies with latitude (gets smaller as you move away from equator)
      val latDegrees = radiusMeters.value / 111320.0
      val lngDegrees = radiusMeters.value / (111320.0 * Math.cos(
        Math.toRadians(coordinates.lat)
      ))

      // Calculate the bounding box
      val minLng = coordinates.lng - lngDegrees
      val minLat = coordinates.lat - latDegrees
      val maxLng = coordinates.lng + lngDegrees
      val maxLat = coordinates.lat + latDegrees

      // Format the bounding box string
      val bbox = f"$minLng,$minLat,$maxLng,$maxLat"

      // Construct the request URI
      val uri = baseUri
        .addPath("images")
        .withQueryParam("fields", "id")
        .withQueryParam("bbox", bbox)

      println(uri)

      val request = Request[IO](
        method = GET,
        uri = uri
      ).withHeaders(Header.Raw(CIString("Authorization"), s"OAuth $apiKey"))

      EitherT(
        client
          .expect[ImagesResponse](request)
          .map(response => response.data.map(_.id))
          .attempt
          .map {
            case Right(ids) => Right(ids)
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
                MapillaryError
                  .NetworkError(s"Network error: ${error.getMessage}")
              )
          }
      )
    }
  }
}
