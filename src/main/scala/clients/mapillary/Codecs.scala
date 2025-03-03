package clients.mapillary

import cats.effect.IO
import clients.mapillary.Models._
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

object Codecs {
  // Circe decoder for JSON response
  implicit val mapillaryImageDetailsDecoder: Decoder[MapillaryImageDetails] =
    Decoder.instance { c =>
      for {
        id <- c.get[String]("id")
        sequenceId <- c.get[String]("sequence")
        thumb2048Url <- c.get[Option[String]]("thumb_2048_url")
        thumbOriginalUrl <- c.get[Option[String]]("thumb_original_url")
      } yield MapillaryImageDetails(
        id = id,
        sequenceId = sequenceId,
        thumb2048Url = thumb2048Url,
        thumbOriginalUrl = thumbOriginalUrl
      )
    }
  implicit val mapillaryImageDetailsEntityDecoder
      : EntityDecoder[IO, MapillaryImageDetails] =
    jsonOf[IO, MapillaryImageDetails]

  implicit val imageDataDecoder: Decoder[ImageData] = deriveDecoder[ImageData]
  implicit val imagesResponseDecoder: Decoder[ImagesResponse] =
    deriveDecoder[ImagesResponse]
  implicit val responseEntityDecoder: EntityDecoder[IO, ImagesResponse] =
    jsonOf[IO, ImagesResponse]

  // Add the necessary decoders
  implicit val mapillaryImageIdDecoder: Decoder[MapillaryImageId] =
    Decoder.instance { c => c.get[String]("id").map(MapillaryImageId) }

  implicit val mapillaryImagesResponseDecoder
      : Decoder[MapillaryImageSequenceIDsResponse] =
    Decoder.instance { c =>
      c.get[List[MapillaryImageId]]("data")
        .map(MapillaryImageSequenceIDsResponse)
    }

  implicit val mapillaryImageSequenceIDsResponseEntityDecoder
      : EntityDecoder[IO, MapillaryImageSequenceIDsResponse] =
    jsonOf[IO, MapillaryImageSequenceIDsResponse]
}
