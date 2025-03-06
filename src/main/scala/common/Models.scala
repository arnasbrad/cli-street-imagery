package common

import common.Errors._

object Models {
  // Coordinates with direct validation
  case class Coordinates private (lat: Double, lng: Double)
  case object Coordinates {
    def create(
        lat: Double,
        lng: Double
    ): Either[ValidationError, Coordinates] = {
      validateLatitude(lat).flatMap { validLat =>
        validateLongitude(lng).map { validLng =>
          new Coordinates(validLat, validLng)
        }
      }
    }

    def unsafeCreate(lat: Double, lng: Double): Coordinates =
      new Coordinates(lat, lng)

    private def validateLatitude(lat: Double): Either[ValidationError, Double] =
      if (lat >= -90 && lat <= 90) Right(lat)
      else Left(LatitudeOutOfRangeError(lat))

    private def validateLongitude(
        lng: Double
    ): Either[ValidationError, Double] =
      if (lng >= -180 && lng <= 180) Right(lng)
      else Left(LongitudeOutOfRangeError(lng))
  }

  // Coordinates with direct validation
  case class Radius private (value: Int)
  case object Radius {
    def create(v: Int): Either[ValidationError, Radius] = {
      Either.cond(v >= 0, new Radius(v), NegativeRadiusError(v))
    }

    def unsafeCreate(v: Int): Radius = {
      new Radius(v)
    }
  }
}
