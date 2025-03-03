package clients.imgur

object Errors {
  sealed trait ImgurError extends Throwable {
    def message: String
  }

  object ImgurError {
    case class JsonDecodingError(message: String) extends ImgurError
    case class ApiError(message: String) extends ImgurError
    case class NetworkError(message: String) extends ImgurError
    case class NotFoundError(message: String) extends ImgurError
    case class AuthenticationError(message: String) extends ImgurError
    case class RateLimitError(message: String) extends ImgurError
  }
}
