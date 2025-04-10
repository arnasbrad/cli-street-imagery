package com.streetascii.clients.traveltime

import com.streetascii.common.Errors.{EmptyInputError, ValidationError}
import com.streetascii.common.Models.Coordinates

object Models {
  case class TravelTimeGeocodingResult(
      coordinates: Coordinates,
      name: Option[String],
      city: Option[String],
      country: Option[String]
  ) {
    def prettyString(): String = {
      val locationCity    = city.getOrElse("Unknown city")
      val locationCountry = country.getOrElse("Unknown country")
      val coords          = s"${coordinates.lat}, ${coordinates.lng}"

      s"$coords, $locationCity, $locationCountry"
    }
  }
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
