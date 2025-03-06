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
import org.http4s.{Header, InvalidMessageBodyFailure, Request, Status, Uri}
import org.typelevel.ci.CIString

/** Client for interacting with the Mapillary API.
  */
trait MapillaryClient {
  def getImage(
      imageId: String,
      fields: List[RequestField] = List(
        RequestField.ID,
        RequestField.Sequence,
        RequestField.Thumb2048Url,
        RequestField.ThumbOriginalUrl
      )
  ): EitherT[IO, MapillaryError, Array[Byte]]

  def getImagesInfoByLocation(
      coordinates: Coordinates,
      radiusMeters: Radius,
      fields: List[RequestField] = List(
        RequestField.ID,
        RequestField.Geometry
      )
  ): EitherT[IO, MapillaryError, ImagesResponse]
}

object MapillaryClient {

  /** Creates a new MapillaryClient instance.
    *
    * @return
    *   A Resource containing the MapillaryClient
    */
  def make(apiKey: ApiKey): Resource[IO, MapillaryClient] =
    EmberClientBuilder.default[IO].build.map(new MapillaryClientImpl(_, apiKey))

  private final class MapillaryClientImpl(client: Client[IO], apiKey: ApiKey)
      extends MapillaryClient {

    private val baseUri = Uri.unsafeFromString("https://graph.mapillary.com")

    /** Creates an authenticated request with the OAuth header.
      *
      * @param uri
      *   The URI for the request
      * @return
      *   A Request with authentication header
      */
    private def authenticatedRequest(uri: Uri): Request[IO] = {
      Request[IO](
        method = GET,
        uri = uri
      ).withHeaders(
        Header.Raw(CIString("Authorization"), s"OAuth ${apiKey.value}")
      )
    }

    /** Enhanced error handler for HTTP requests and IO operations.
      *
      * @param attempt
      *   The attempted IO operation
      * @return
      *   An IO containing either a MapillaryError or the successful result
      */
    private def handleErrors[A](
        attempt: IO[A]
    ): IO[Either[MapillaryError, A]] = {
      attempt.attempt.map {
        case Right(result) => Right(result)

        // JSON decoding errors
        case Left(error: InvalidMessageBodyFailure) =>
          Left(
            MapillaryError.JsonDecodingError(
              s"Failed to decode response: ${error.getMessage}"
            )
          )

        // HTTP status errors
        case Left(error: UnexpectedStatus) =>
          error.status match {
            case Status.Unauthorized | Status.Forbidden =>
              Left(
                MapillaryError.AuthenticationError(
                  s"Authentication failed with status ${error.status.code}: ${error.status.reason}"
                )
              )
            case Status.TooManyRequests =>
              Left(
                MapillaryError.RateLimitError(
                  s"Rate limit exceeded: ${error.status.reason}"
                )
              )
            case Status.NotFound =>
              Left(
                MapillaryError.NotFoundError(
                  s"Resource not found: ${error.status.reason}"
                )
              )
            case Status.BadRequest =>
              Left(
                MapillaryError.ValidationError(
                  s"Bad request: ${error.status.reason}"
                )
              )
            case _ =>
              Left(
                MapillaryError.ApiError(
                  s"API returned unexpected status: ${error.status.code} - ${error.status.reason}"
                )
              )
          }

        // Network-specific errors
        case Left(error: java.net.ConnectException) =>
          Left(
            MapillaryError.NetworkError(
              s"Failed to connect to server: ${error.getMessage}"
            )
          )
        case Left(error: java.net.SocketTimeoutException) =>
          Left(
            MapillaryError.NetworkError(
              s"Connection to server timed out: ${error.getMessage}"
            )
          )
        case Left(error: java.io.IOException) =>
          Left(
            MapillaryError.NetworkError(
              s"I/O error during request: ${error.getMessage}"
            )
          )

        // Fallback for other errors
        case Left(error) =>
          Left(
            MapillaryError.UnknownError(
              s"Unexpected error: ${error.getMessage}"
            )
          )
      }
    }

    /** Retrieves image details for a specific image ID.
      */
    private def getImageDetails(
        imageId: String,
        fields: List[RequestField]
    ): EitherT[IO, MapillaryError, MapillaryImageDetails] = {
      val imageUri = baseUri
        .addPath(imageId)
        .withQueryParam("fields", fields.map(_.value).mkString(","))

      val request = authenticatedRequest(imageUri)

      EitherT(handleErrors(client.expect[MapillaryImageDetails](request)))
    }

    /** Downloads an image from a URL.
      */
    private def getImageFromUrl(
        url: String
    ): EitherT[IO, MapillaryError, Array[Byte]] = {
      val uri = Uri.unsafeFromString(url)
      val request = Request[IO](
        method = GET,
        uri = uri
      )

      EitherT(handleErrors(client.expect[Array[Byte]](request)))
    }

    /** Retrieves an image by its ID.
      */
    override def getImage(
        imageId: String,
        fields: List[RequestField]
    ): EitherT[IO, MapillaryError, Array[Byte]] = {
      for {
        details <- getImageDetails(imageId, fields)

        imageUrl <- details.thumbOriginalUrl match {
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

    /** Calculates a bounding box around coordinates within a radius.
      */
    private def calculateBoundingBox(
        coordinates: Coordinates,
        radiusMeters: Radius
    ): String = {
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
      f"$minLng,$minLat,$maxLng,$maxLat"
    }

    /** Retrieves image IDs by location within a radius.
      */
    override def getImagesInfoByLocation(
        coordinates: Coordinates,
        radiusMeters: Radius,
        fields: List[RequestField]
    ): EitherT[IO, MapillaryError, ImagesResponse] = {
      // Calculate the bounding box
      val bbox = calculateBoundingBox(coordinates, radiusMeters)

      // Construct the request URI
      val uri = baseUri
        .addPath("images")
        .withQueryParam("fields", fields.map(_.value).mkString(","))
        .withQueryParam("bbox", bbox)

      val request = authenticatedRequest(uri)

      EitherT(
        handleErrors(
          client
            .expect[ImagesResponse](request)
        )
      )
    }
  }
}
