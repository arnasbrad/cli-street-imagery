package common

object Models {
  sealed trait ValidationError
  case class LatitudeOutOfRange(value: Double) extends ValidationError
  case class LongitudeOutOfRange(value: Double) extends ValidationError
  case class NegativeRadius(value: Int) extends ValidationError

  // Coordinates with direct validation
  class Coordinates private (val lat: Double, val lng: Double)
  object Coordinates {
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
      else Left(LatitudeOutOfRange(lat))

    private def validateLongitude(
        lng: Double
    ): Either[ValidationError, Double] =
      if (lng >= -180 && lng <= 180) Right(lng)
      else Left(LongitudeOutOfRange(lng))
  }

  // Coordinates with direct validation
  class Radius private (val value: Int)
  object Radius {
    def create(v: Int): Either[ValidationError, Radius] = {
      Either.cond(v >= 0, new Radius(v), NegativeRadius(v))
    }

    def unsafeCreate(v: Int): Radius = {
      new Radius(v)
    }
  }
}
