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
import org.http4s.{InvalidMessageBodyFailure, Request, Uri}

trait MapillaryClient {

  /** Retrieves an image from the Mapillary service by its ID.
    *
    * @param imageId The unique identifier of the image to retrieve
    * @return An EitherT containing either a MapillaryError (Left) or the image data as a byte array (Right)
    * @example {{{
    * // Fetch image and render it
    * val imageDataEither = client.getImage("12345678")
    * imageDataEither.value.flatMap {
    *   case Right(imageData) => renderImage(imageData)
    *   case Left(error) => IO.println(s"Failed to retrieve image: $error")
    * }
    * }}}
    */
  def getImage(imageId: String): EitherT[IO, MapillaryError, Array[Byte]]

  /** Searches for image IDs near a specific geographic location.
    *
    * This method queries the Mapillary API to find all images within the specified
    * radius of the given coordinates.
    *
    * @param coordinates The geographic coordinates (latitude/longitude) around which to search
    * @param radiusMeters The search radius in meters from the specified coordinates
    * @return An EitherT containing either a MapillaryError (Left) or a list of image IDs (Right)
    * @example {{{
    * // Find images near Copenhagen
    * val coordinatesEither = Coordinates.create(55.676, 12.568)
    * val radiusEither = Radius.create(50)
    *
    * val result = for {
    *   coordinates <- EitherT.fromEither[IO](coordinatesEither)
    *   radius <- EitherT.fromEither[IO](radiusEither)
    *   imageIds <- client.getImageIdsByLocation(coordinates, radius)
    * } yield imageIds
    * }}}
    */
  def getImageIdsByLocation(
      coordinates: Coordinates,
      radiusMeters: Radius
  ): EitherT[IO, MapillaryError, List[String]]
}

object MapillaryClient {
  def make(): Resource[IO, MapillaryClient] =
    EmberClientBuilder.default[IO].build.map(new MapillaryClientImpl(_))

  private final class MapillaryClientImpl(client: Client[IO])
      extends MapillaryClient {
    private val baseUri = Uri.unsafeFromString("https://graph.mapillary.com")

    // TODO: use oauth instead of http param. And use AppConfig
    private val apiKey = "MLY|9231864626900852|d5262804c0fe9f4ec9033bc7a6432910"

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

    override def getImageIdsByLocation(
        coordinates: Coordinates,
        radiusMeters: Radius
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
        .withQueryParam("access_token", apiKey)
        .withQueryParam("fields", "id")
        .withQueryParam("bbox", bbox)

      println(uri)

      val request = Request[IO](
        method = GET,
        uri = uri
      )

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
