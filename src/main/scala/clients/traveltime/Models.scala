package clients.traveltime

import common.Errors.{EmptyInputError, ValidationError}
import common.Models.Coordinates

object Models {
  case class TravelTimeGeocodingResult(
      coordinates: Coordinates,
      name: Option[String],
      city: Option[String],
      country: Option[String]
  )
  case class TravelTimeGeocodingResponse(
      features: List[TravelTimeGeocodingResult]
  )

  case class AppId private (value: String)
  case object AppId {
    def create(v: String): Either[ValidationError, AppId] = {
      Either.cond(v.nonEmpty, new AppId(v), EmptyInputError)
    }

    def unsafeCreate(v: String): AppId = {
      new AppId(v)
    }
  }

  case class ApiKey private (value: String)
  case object ApiKey {
    def create(v: String): Either[ValidationError, ApiKey] = {
      Either.cond(v.nonEmpty, new ApiKey(v), EmptyInputError)
    }

    def unsafeCreate(v: String): ApiKey = {
      new ApiKey(v)
    }
  }
}
