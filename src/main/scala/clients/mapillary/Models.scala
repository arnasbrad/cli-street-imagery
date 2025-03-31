package clients.mapillary

import common.Errors._
import common.Models.Coordinates

object Models {
  case class MapillaryImageDetails(
      id: String,
      sequenceId: String,
      thumb2048Url: Option[String],
      thumbOriginalUrl: Option[String]
  )

  case class ImagesResponse(data: List[ImageData])
  case class ImageData(id: MapillaryImageId, coordinates: Coordinates)

  case class MapillaryImageId(id: String)

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
    case object Thumb1024Url extends RequestField {
      def value = "thumb_1024_url"
    }
    case object ThumbOriginalUrl extends RequestField {
      def value = "thumb_original_url"
    }
  }
}
