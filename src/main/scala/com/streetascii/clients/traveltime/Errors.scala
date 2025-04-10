package com.streetascii.clients.traveltime

object Errors {
  sealed trait TravelTimeError extends Throwable {
    def message: String
  }

  object TravelTimeError {
    case class JsonDecodingError(message: String)    extends TravelTimeError
    case class ApiError(message: String)             extends TravelTimeError
    case class ValidationError(message: String)      extends TravelTimeError
    case class PayloadTooLargeError(message: String) extends TravelTimeError
    case class NetworkError(message: String)         extends TravelTimeError
    case class UnknownError(message: String)         extends TravelTimeError
    case class NotFoundError(message: String)        extends TravelTimeError
    case class AuthenticationError(message: String)  extends TravelTimeError
    case class RateLimitError(message: String)       extends TravelTimeError
  }
}
