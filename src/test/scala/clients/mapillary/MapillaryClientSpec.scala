package clients.mapillary

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import clients.mapillary.Errors.MapillaryError
import org.http4s.client.UnexpectedStatus
import org.http4s.{Method, Status, Uri}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MapillaryClientSpec extends AnyFunSpec with Matchers {
  describe("MapillaryClient.handleErrors") {
    it("should pass through successful results unchanged") {
      val result = "Success"
      val handled =
        MapillaryClient.handleErrors(IO.pure(result)).unsafeRunSync()

      handled.isRight shouldBe true
      handled.getOrElse(fail("Expected Right")) shouldBe result
    }

    it("should map InvalidMessageBodyFailure to JsonDecodingError") {
      val error = org.http4s.InvalidMessageBodyFailure("Invalid JSON", None)
      val handled =
        MapillaryClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val mapillaryError = handled.swap.getOrElse(fail("Expected Left"))
      mapillaryError shouldBe a[MapillaryError.JsonDecodingError]
      mapillaryError.message should include("Failed to decode response")
    }

    it("should map UnexpectedStatus(401) to AuthenticationError") {
      val error = UnexpectedStatus(
        Status.Unauthorized,
        Method.GET,
        Uri.unsafeFromString("https://graph.mapillary.com/images")
      )
      val handled =
        MapillaryClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val mapillaryError = handled.swap.getOrElse(fail("Expected Left"))
      mapillaryError shouldBe a[MapillaryError.AuthenticationError]
      mapillaryError.message should include("Authentication failed")
    }

    it("should map UnexpectedStatus(403) to AuthenticationError") {
      val error = UnexpectedStatus(
        Status.Forbidden,
        Method.GET,
        Uri.unsafeFromString("https://graph.mapillary.com/images")
      )
      val handled =
        MapillaryClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val mapillaryError = handled.swap.getOrElse(fail("Expected Left"))
      mapillaryError shouldBe a[MapillaryError.AuthenticationError]
      mapillaryError.message should include("Authentication failed")
    }

    it("should map UnexpectedStatus(429) to RateLimitError") {
      val error = UnexpectedStatus(
        Status.TooManyRequests,
        Method.GET,
        Uri.unsafeFromString("https://graph.mapillary.com/images")
      )
      val handled =
        MapillaryClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val mapillaryError = handled.swap.getOrElse(fail("Expected Left"))
      mapillaryError shouldBe a[MapillaryError.RateLimitError]
      mapillaryError.message should include("Rate limit exceeded")
    }

    it("should map UnexpectedStatus(400) to ValidationError") {
      val error = UnexpectedStatus(
        Status.BadRequest,
        Method.GET,
        Uri.unsafeFromString("https://graph.mapillary.com/images")
      )
      val handled =
        MapillaryClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val mapillaryError = handled.swap.getOrElse(fail("Expected Left"))
      mapillaryError shouldBe a[MapillaryError.ValidationError]
      mapillaryError.message should include("Bad request")
    }

    it("should map UnexpectedStatus(404) to NotFoundError") {
      val error = UnexpectedStatus(
        Status.NotFound,
        Method.GET,
        Uri.unsafeFromString("https://graph.mapillary.com/images")
      )
      val handled =
        MapillaryClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val mapillaryError = handled.swap.getOrElse(fail("Expected Left"))
      mapillaryError shouldBe a[MapillaryError.NotFoundError]
      mapillaryError.message should include("Resource not found")
    }

    it("should map other UnexpectedStatus to ApiError") {
      val error = UnexpectedStatus(
        Status.InternalServerError,
        Method.GET,
        Uri.unsafeFromString("https://graph.mapillary.com/images")
      )
      val handled =
        MapillaryClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val mapillaryError = handled.swap.getOrElse(fail("Expected Left"))
      mapillaryError shouldBe a[MapillaryError.ApiError]
      mapillaryError.message should include("API returned unexpected status")
    }

    it("should map ConnectException to NetworkError") {
      val error = new java.net.ConnectException("Connection refused")
      val handled =
        MapillaryClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val mapillaryError = handled.swap.getOrElse(fail("Expected Left"))
      mapillaryError shouldBe a[MapillaryError.NetworkError]
      mapillaryError.message should include("Failed to connect to server")
    }

    it("should map SocketTimeoutException to NetworkError") {
      val error = new java.net.SocketTimeoutException("Read timed out")
      val handled =
        MapillaryClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val mapillaryError = handled.swap.getOrElse(fail("Expected Left"))
      mapillaryError shouldBe a[MapillaryError.NetworkError]
      mapillaryError.message should include("Connection to server timed out")
    }

    it("should map IOException to NetworkError") {
      val error = new java.io.IOException("Failed to read response")
      val handled =
        MapillaryClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val mapillaryError = handled.swap.getOrElse(fail("Expected Left"))
      mapillaryError shouldBe a[MapillaryError.NetworkError]
      mapillaryError.message should include("I/O error during request")
    }

    it("should map other exceptions to UnknownError") {
      val error = new RuntimeException("Something unexpected happened")
      val handled =
        MapillaryClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val mapillaryError = handled.swap.getOrElse(fail("Expected Left"))
      mapillaryError shouldBe a[MapillaryError.UnknownError]
      mapillaryError.message should include("Unexpected error")
    }
  }
}
