package com.streetascii.clients.mapillary

object Errors {
  sealed trait MapillaryError extends Throwable {
    def message: String
  }

  object MapillaryError {
    case class JsonDecodingError(message: String)   extends MapillaryError
    case class ApiError(message: String)            extends MapillaryError
    case class NetworkError(message: String)        extends MapillaryError
    case class NotFoundError(message: String)       extends MapillaryError
    case class UnknownError(message: String)        extends MapillaryError
    case class ValidationError(message: String)     extends MapillaryError
    case class AuthenticationError(message: String) extends MapillaryError
    case class RateLimitError(message: String)      extends MapillaryError
  }
}
