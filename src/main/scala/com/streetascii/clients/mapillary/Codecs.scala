package com.streetascii.clients.mapillary

import cats.effect.IO
import com.streetascii.clients.mapillary.Models.{
  ImageData,
  ImagesResponse,
  MapillaryImageDetails,
  MapillaryImageId,
  MapillarySequenceId,
  SequenceImagesResponse
}
import com.streetascii.common.Models.Coordinates
import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, HCursor}
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

object Codecs {
  // Circe decoder for JSON response
  implicit val mapillaryImageDetailsDecoder: Decoder[MapillaryImageDetails] =
    Decoder.instance { c =>
      for {
        id         <- c.get[String]("id").map(MapillaryImageId)
        sequenceId <- c.get[String]("sequence").map(MapillarySequenceId)
        coordsArray <- c
          .downField("geometry")
          .downField("coordinates")
          .as[List[Double]]
        thumb1024Url     <- c.get[Option[String]]("thumb_1024_url")
        thumbOriginalUrl <- c.get[Option[String]]("thumb_original_url")
      } yield MapillaryImageDetails(
        id = id,
        sequenceId = sequenceId,
        coordinates =
          Coordinates.unsafeCreate(coordsArray(1), coordsArray.head),
        thumb1024Url = thumb1024Url,
        thumbOriginalUrl = thumbOriginalUrl
      )
    }
  implicit val mapillaryImageDetailsEntityDecoder
      : EntityDecoder[IO, MapillaryImageDetails] =
    jsonOf[IO, MapillaryImageDetails]

  implicit val sequenceImagesResponseDecoder: Decoder[SequenceImagesResponse] =
    (c: HCursor) => {
      c.downField("data").as[List[MapillaryImageId]].map(SequenceImagesResponse)
    }

  implicit val sequenceImagesResponseEntityDecoder
      : EntityDecoder[IO, SequenceImagesResponse] =
    jsonOf[IO, SequenceImagesResponse]

  implicit val imagesResponseDecoder: Decoder[ImagesResponse] =
    deriveDecoder[ImagesResponse]
  implicit val responseEntityDecoder: EntityDecoder[IO, ImagesResponse] =
    jsonOf[IO, ImagesResponse]

  implicit val imageDataDecoder: Decoder[ImageData] = (c: HCursor) => {
    for {
      id <- c.downField("id").as[String]
      // Navigate to the coordinates array in the geometry object
      coordsArray <- c
        .downField("geometry")
        .downField("coordinates")
        .as[List[Double]]
      // GeoJSON uses [longitude, latitude] order, so we need to swap
    } yield ImageData(
      MapillaryImageId(id),
      Coordinates.unsafeCreate(lat = coordsArray(1), lng = coordsArray.head)
    )
  }

  // Add the necessary decoders
  implicit val mapillaryImageIdDecoder: Decoder[MapillaryImageId] =
    Decoder.instance { c => c.get[String]("id").map(MapillaryImageId) }
}
