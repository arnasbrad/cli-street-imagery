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

  /** Retrieves an image by its ID.
    *
    * @param imageId The ID of the image to retrieve
    * @param apiKey The API key for authentication
    * @return The image bytes wrapped in an EitherT
    */
  def getImage(
      imageId: String,
      apiKey: ApiKey
  ): EitherT[IO, MapillaryError, Array[Byte]]

  /** Retrieves image IDs by location within a radius.
    *
    * @param coordinates The center coordinates (latitude, longitude)
    * @param radiusMeters The radius in meters to search
    * @param apiKey The API key for authentication
    * @return A list of image IDs wrapped in an EitherT
    */
  def getImageIdsByLocation(
      coordinates: Coordinates,
      radiusMeters: Radius,
      apiKey: ApiKey
  ): EitherT[IO, MapillaryError, List[String]]
}

object MapillaryClient {

  /** Creates a new MapillaryClient instance.
    *
    * @return A Resource containing the MapillaryClient
    */
  def make(): Resource[IO, MapillaryClient] =
    EmberClientBuilder.default[IO].build.map(new MapillaryClientImpl(_))

  private final class MapillaryClientImpl(client: Client[IO])
      extends MapillaryClient {

    private val baseUri = Uri.unsafeFromString("https://graph.mapillary.com")

    /** Creates an authenticated request with the OAuth header.
      *
      * @param uri The URI for the request
      * @param apiKey The API key for authentication
      * @return A Request with authentication header
      */
    private def authenticatedRequest(uri: Uri, apiKey: ApiKey): Request[IO] = {
      Request[IO](
        method = GET,
        uri = uri
      ).withHeaders(
        Header.Raw(CIString("Authorization"), s"OAuth ${apiKey.value}")
      )
    }

    /** Generic error handler for HTTP requests.
      *
      * @param attempt The attempted HTTP request
      * @return An IO containing either an error or the successful result
      */
    private def handleRequestErrors[A](
        attempt: IO[Either[Throwable, A]]
    ): IO[Either[MapillaryError, A]] = {
      attempt.map {
        case Right(result) => Right(result)
        case Left(error: InvalidMessageBodyFailure) =>
          Left(
            MapillaryError.JsonDecodingError(
              s"Failed to decode response: ${error.getMessage}"
            )
          )
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
    }

    /** Retrieves image details for a specific image ID.
      */
    private def getImageDetails(
        imageId: String,
        apiKey: ApiKey
    ): EitherT[IO, MapillaryError, MapillaryImageDetails] = {
      val fields = List(
        "id",
        "sequence",
        "thumb_2048_url",
        "thumb_original_url"
      ).mkString(",")

      val imageUri = baseUri
        .addPath(imageId)
        .withQueryParam("fields", fields)

      val request = authenticatedRequest(imageUri, apiKey)

      EitherT(
        handleRequestErrors(
          client.expect[MapillaryImageDetails](request).attempt
        )
      )
    }

    /** Retrieves image IDs for a specific sequence.
      */
    private def getImageSequence(
        sequenceId: String,
        apiKey: ApiKey
    ): EitherT[IO, MapillaryError, List[String]] = {
      val imageUri = baseUri
        .addPath("images")
        .withQueryParam("sequence_ids", sequenceId)
        .withQueryParam("fields", "id") // Explicitly request only ID field

      val request = authenticatedRequest(imageUri, apiKey)

      EitherT(
        handleRequestErrors(
          client
            .expect[MapillaryImageSequenceIDsResponse](request)
            .map(response => response.data.map(_.id))
            .attempt
        )
      )
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

      EitherT(
        handleRequestErrors(
          client.expect[Array[Byte]](request).attempt.map {
            case Right(imageBytes) => Right(imageBytes)
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
              Left(error) // Will be handled by the outer handler
          }
        )
      )
    }

    /** Retrieves an image by its ID.
      */
    override def getImage(
        imageId: String,
        apiKey: ApiKey
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
    override def getImageIdsByLocation(
        coordinates: Coordinates,
        radiusMeters: Radius,
        apiKey: ApiKey
    ): EitherT[IO, MapillaryError, List[String]] = {
      // Calculate the bounding box
      val bbox = calculateBoundingBox(coordinates, radiusMeters)

      // Construct the request URI
      val uri = baseUri
        .addPath("images")
        .withQueryParam("fields", "id")
        .withQueryParam("bbox", bbox)

      val request = authenticatedRequest(uri, apiKey)

      EitherT(
        handleRequestErrors(
          client
            .expect[ImagesResponse](request)
            .map(response => response.data.map(_.id))
            .attempt
        )
      )
    }
  }
}
