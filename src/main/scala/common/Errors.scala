package common

object Errors {
  sealed trait ValidationError
  case class LatitudeOutOfRangeError(value: Double) extends ValidationError
  case class LongitudeOutOfRangeError(value: Double) extends ValidationError
  case class NegativeRadiusError(value: Int) extends ValidationError
  case object EmptyInputError extends ValidationError
}
