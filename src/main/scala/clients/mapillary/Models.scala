package clients.mapillary

import common.Errors._

object Models {
  case class MapillaryImageDetails(
      id: String,
      thumb2048Url: Option[String] = None,
      thumbOriginalUrl: Option[String] = None
  )

  case class ImagesResponse(data: List[ImageData])
  case class ImageData(id: String)

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
