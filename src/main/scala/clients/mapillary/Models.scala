package clients.mapillary

import common.Errors._

object Models {
  case class MapillaryImageDetails(
      id: String,
      sequenceId: String,
      thumb2048Url: Option[String],
      thumbOriginalUrl: Option[String]
  )

  case class ImagesResponse(data: List[ImageData])
  case class ImageData(id: String)

  case class MapillaryImageSequenceIDsResponse(data: List[MapillaryImageId])
  case class MapillaryImageId(id: String)

  class ApiKey private (val value: String)
  object ApiKey {
    def create(v: String): Either[ValidationError, ApiKey] = {
      Either.cond(v.nonEmpty, new ApiKey(v), EmptyInputError)
    }

    def unsafeCreate(v: String): ApiKey = {
      new ApiKey(v)
    }
  }
}
