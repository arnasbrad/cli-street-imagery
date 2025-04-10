package clients.traveltime

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.streetascii.clients.traveltime.Errors.TravelTimeError
import com.streetascii.clients.traveltime.TravelTimeClient
import org.http4s.client.UnexpectedStatus
import org.http4s.{Method, Status, Uri}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TravelTimeClientSpec extends AnyFunSpec with Matchers {
  describe("TravelTimeClient.handleErrors") {
    it("should pass through successful results unchanged") {
      val result = "Success"
      val handled =
        TravelTimeClient.handleErrors(IO.pure(result)).unsafeRunSync()

      handled.isRight shouldBe true
      handled.getOrElse(fail("Expected Right")) shouldBe result
    }

    it("should map InvalidMessageBodyFailure to JsonDecodingError") {
      val error = org.http4s.InvalidMessageBodyFailure("Invalid JSON", None)
      val handled =
        TravelTimeClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val travelTimeError = handled.swap.getOrElse(fail("Expected Left"))
      travelTimeError shouldBe a[TravelTimeError.JsonDecodingError]
      travelTimeError.message should include("Failed to decode response")
    }

    it("should map UnexpectedStatus(401) to AuthenticationError") {
      val error = UnexpectedStatus(
        Status.Unauthorized,
        Method.GET,
        Uri.unsafeFromString(
          "https://api.traveltimeapp.com/v4/geocoding/search"
        )
      )
      val handled =
        TravelTimeClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val travelTimeError = handled.swap.getOrElse(fail("Expected Left"))
      travelTimeError shouldBe a[TravelTimeError.AuthenticationError]
      travelTimeError.message should include("Authentication failed")
    }

    it("should map UnexpectedStatus(403) to AuthenticationError") {
      val error = UnexpectedStatus(
        Status.Forbidden,
        Method.GET,
        Uri.unsafeFromString(
          "https://api.traveltimeapp.com/v4/geocoding/search"
        )
      )
      val handled =
        TravelTimeClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val travelTimeError = handled.swap.getOrElse(fail("Expected Left"))
      travelTimeError shouldBe a[TravelTimeError.AuthenticationError]
      travelTimeError.message should include("Authorization failed")
    }

    it("should map UnexpectedStatus(429) to RateLimitError") {
      val error = UnexpectedStatus(
        Status.TooManyRequests,
        Method.GET,
        Uri.unsafeFromString(
          "https://api.traveltimeapp.com/v4/geocoding/search"
        )
      )
      val handled =
        TravelTimeClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val travelTimeError = handled.swap.getOrElse(fail("Expected Left"))
      travelTimeError shouldBe a[TravelTimeError.RateLimitError]
      travelTimeError.message should include("Rate limit exceeded")
    }

    it("should map UnexpectedStatus(400) to ValidationError") {
      val error = UnexpectedStatus(
        Status.BadRequest,
        Method.GET,
        Uri.unsafeFromString(
          "https://api.traveltimeapp.com/v4/geocoding/search"
        )
      )
      val handled =
        TravelTimeClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val travelTimeError = handled.swap.getOrElse(fail("Expected Left"))
      travelTimeError shouldBe a[TravelTimeError.ValidationError]
      travelTimeError.message should include("Bad request")
    }

    it("should map UnexpectedStatus(413) to PayloadTooLargeError") {
      val error = UnexpectedStatus(
        Status.PayloadTooLarge,
        Method.GET,
        Uri.unsafeFromString(
          "https://api.traveltimeapp.com/v4/geocoding/search"
        )
      )
      val handled =
        TravelTimeClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val travelTimeError = handled.swap.getOrElse(fail("Expected Left"))
      travelTimeError shouldBe a[TravelTimeError.PayloadTooLargeError]
      travelTimeError.message should include("Image size too large")
    }

    it("should map UnexpectedStatus(404) to NotFoundError") {
      val error = UnexpectedStatus(
        Status.NotFound,
        Method.GET,
        Uri.unsafeFromString(
          "https://api.traveltimeapp.com/v4/geocoding/search"
        )
      )
      val handled =
        TravelTimeClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val travelTimeError = handled.swap.getOrElse(fail("Expected Left"))
      travelTimeError shouldBe a[TravelTimeError.NotFoundError]
      travelTimeError.message should include("Resource not found")
    }

    it("should map other UnexpectedStatus to ApiError") {
      val error = UnexpectedStatus(
        Status.InternalServerError,
        Method.GET,
        Uri.unsafeFromString(
          "https://api.traveltimeapp.com/v4/geocoding/search"
        )
      )
      val handled =
        TravelTimeClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val travelTimeError = handled.swap.getOrElse(fail("Expected Left"))
      travelTimeError shouldBe a[TravelTimeError.ApiError]
      travelTimeError.message should include("API returned unexpected status")
    }

    it("should map ConnectException to NetworkError") {
      val error = new java.net.ConnectException("Connection refused")
      val handled =
        TravelTimeClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val travelTimeError = handled.swap.getOrElse(fail("Expected Left"))
      travelTimeError shouldBe a[TravelTimeError.NetworkError]
      travelTimeError.message should include("Failed to connect to server")
    }

    it("should map SocketTimeoutException to NetworkError") {
      val error = new java.net.SocketTimeoutException("Read timed out")
      val handled =
        TravelTimeClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val travelTimeError = handled.swap.getOrElse(fail("Expected Left"))
      travelTimeError shouldBe a[TravelTimeError.NetworkError]
      travelTimeError.message should include("Connection to server timed out")
    }

    it("should map IOException to NetworkError") {
      val error = new java.io.IOException("Failed to read response")
      val handled =
        TravelTimeClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val travelTimeError = handled.swap.getOrElse(fail("Expected Left"))
      travelTimeError shouldBe a[TravelTimeError.NetworkError]
      travelTimeError.message should include("I/O error during request")
    }

    it("should map other exceptions to UnknownError") {
      val error = new RuntimeException("Something unexpected happened")
      val handled =
        TravelTimeClient
          .handleErrors(IO.raiseError[String](error))
          .unsafeRunSync()

      handled.isLeft shouldBe true
      val travelTimeError = handled.swap.getOrElse(fail("Expected Left"))
      travelTimeError shouldBe a[TravelTimeError.UnknownError]
      travelTimeError.message should include("Unexpected error")
    }
  }
}
