package clients.mapillary

import clients.mapillary.Models.MapillaryImageDetails
import io.circe.Decoder

object Codecs {
  // Circe decoder for JSON response
  implicit val decoder: Decoder[MapillaryImageDetails] =
    Decoder.instance { c =>
      for {
        id <- c.get[String]("id")
        thumb2048Url <- c.get[Option[String]]("thumb_2048_url")
        thumbOriginalUrl <- c.get[Option[String]]("thumb_original_url")
      } yield MapillaryImageDetails(
        id = id,
        thumb2048Url = thumb2048Url,
        thumbOriginalUrl = thumbOriginalUrl
      )
    }
}
