package com.streetascii.clients.mapillary

import com.streetascii.common.Errors.{EmptyInputError, ValidationError}
import com.streetascii.common.Models._

object Models {
  case class MapillaryImageDetails(
      id: MapillaryImageId,
      sequenceId: MapillarySequenceId,
      compassAngle: Double,
      coordinates: Coordinates,
      thumb1024Url: Option[String],
      thumbOriginalUrl: Option[String]
  )

  case class ImagesResponse(data: List[ImageData])
  case class SequenceImagesResponse(data: List[MapillaryImageId])
  case class ImageData(
      id: MapillaryImageId,
      coordinates: Coordinates,
      compassAngle: Double
  )

  case class MapillaryImageId(id: String)
  case class MapillarySequenceId(id: String)

  case class ApiKey private (value: String)
  case object ApiKey {
    def create(v: String): Either[ValidationError, ApiKey] = {
      Either.cond(v.nonEmpty, new ApiKey(v), EmptyInputError)
    }

    def unsafeCreate(v: String): ApiKey = {
      new ApiKey(v)
    }
  }

  sealed trait RequestField {
    def value: String
  }
  object RequestField {
    case object ID extends RequestField {
      def value = "id"
    }
    case object Geometry extends RequestField {
      def value = "geometry"
    }
    case object Sequence extends RequestField {
      def value = "sequence"
    }
    case object CompassAngle extends RequestField {
      def value = "compass_angle"
    }
    case object Thumb1024Url extends RequestField {
      def value = "thumb_1024_url"
    }
    case object ThumbOriginalUrl extends RequestField {
      def value = "thumb_original_url"
    }
  }
}
