package clients.imgur

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import clients.imgur.Errors.ImgurError
import org.http4s.client.UnexpectedStatus
import org.http4s.{Method, Status, Uri}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.ci._

class ImgurClientSpec extends AnyFunSpec with Matchers {
  describe("ImgurClient.handleErrors") {
    it("should pass through successful results unchanged") {
      val result  = "Success"
      val handled = ImgurClient.handleErrors(IO.pure(result)).unsafeRunSync()

      handled.isRight shouldBe true
      handled.getOrElse(fail("Expected Right")) shouldBe result
    }

    it("should map InvalidMessageBodyFailure to JsonDecodingError") {
      val error = org.http4s.InvalidMessageBodyFailure("Invalid JSON", None)
      val handled =
        ImgurClient.handleErrors(IO.raiseError[String](error)).unsafeRunSync()

      handled.isLeft shouldBe true
      val imgurError = handled.swap.getOrElse(fail("Expected Left"))
      imgurError shouldBe a[ImgurError.JsonDecodingError]
      imgurError.message should include("Failed to decode response")
    }

    it("should map UnexpectedStatus(401) to AuthenticationError") {
      val error = UnexpectedStatus(
        Status.Unauthorized,
        Method.POST,
        Uri.unsafeFromString("https://api.imgur.com/3/image")
      )
      val handled =
        ImgurClient.handleErrors(IO.raiseError[String](error)).unsafeRunSync()

      handled.isLeft shouldBe true
      val imgurError = handled.swap.getOrElse(fail("Expected Left"))
      imgurError shouldBe a[ImgurError.AuthenticationError]
      imgurError.message should include("Authentication failed")
    }

    it("should map UnexpectedStatus(403) to AuthenticationError") {
      val error = UnexpectedStatus(
        Status.Forbidden,
        Method.POST,
        Uri.unsafeFromString("https://api.imgur.com/3/image")
      )
      val handled =
        ImgurClient.handleErrors(IO.raiseError[String](error)).unsafeRunSync()

      handled.isLeft shouldBe true
      val imgurError = handled.swap.getOrElse(fail("Expected Left"))
      imgurError shouldBe a[ImgurError.AuthenticationError]
      imgurError.message should include("Authorization failed")
    }

    it("should map UnexpectedStatus(429) to RateLimitError") {
      val error = UnexpectedStatus(
        Status.TooManyRequests,
        Method.POST,
        Uri.unsafeFromString("https://api.imgur.com/3/image")
      )
      val handled =
        ImgurClient.handleErrors(IO.raiseError[String](error)).unsafeRunSync()

      handled.isLeft shouldBe true
      val imgurError = handled.swap.getOrElse(fail("Expected Left"))
      imgurError shouldBe a[ImgurError.RateLimitError]
      imgurError.message should include("Rate limit exceeded")
    }

    it("should map UnexpectedStatus(400) to ValidationError") {
      val error = UnexpectedStatus(
        Status.BadRequest,
        Method.POST,
        Uri.unsafeFromString("https://api.imgur.com/3/image")
      )
      val handled =
        ImgurClient.handleErrors(IO.raiseError[String](error)).unsafeRunSync()

      handled.isLeft shouldBe true
      val imgurError = handled.swap.getOrElse(fail("Expected Left"))
      imgurError shouldBe a[ImgurError.ValidationError]
      imgurError.message should include("Bad request")
    }

    it("should map UnexpectedStatus(413) to PayloadTooLargeError") {
      val error = UnexpectedStatus(
        Status.PayloadTooLarge,
        Method.POST,
        Uri.unsafeFromString("https://api.imgur.com/3/image")
      )
      val handled =
        ImgurClient.handleErrors(IO.raiseError[String](error)).unsafeRunSync()

      handled.isLeft shouldBe true
      val imgurError = handled.swap.getOrElse(fail("Expected Left"))
      imgurError shouldBe a[ImgurError.PayloadTooLargeError]
      imgurError.message should include("Image size too large")
    }

    it("should map UnexpectedStatus(404) to NotFoundError") {
      val error = UnexpectedStatus(
        Status.NotFound,
        Method.POST,
        Uri.unsafeFromString("https://api.imgur.com/3/image")
      )
      val handled =
        ImgurClient.handleErrors(IO.raiseError[String](error)).unsafeRunSync()

      handled.isLeft shouldBe true
      val imgurError = handled.swap.getOrElse(fail("Expected Left"))
      imgurError shouldBe a[ImgurError.NotFoundError]
      imgurError.message should include("Resource not found")
    }

    it("should map other UnexpectedStatus to ApiError") {
      val error = UnexpectedStatus(
        Status.InternalServerError,
        Method.POST,
        Uri.unsafeFromString("https://api.imgur.com/3/image")
      )
      val handled =
        ImgurClient.handleErrors(IO.raiseError[String](error)).unsafeRunSync()

      handled.isLeft shouldBe true
      val imgurError = handled.swap.getOrElse(fail("Expected Left"))
      imgurError shouldBe a[ImgurError.ApiError]
      imgurError.message should include("API returned unexpected status")
    }

    it("should map ConnectException to NetworkError") {
      val error = new java.net.ConnectException("Connection refused")
      val handled =
        ImgurClient.handleErrors(IO.raiseError[String](error)).unsafeRunSync()

      handled.isLeft shouldBe true
      val imgurError = handled.swap.getOrElse(fail("Expected Left"))
      imgurError shouldBe a[ImgurError.NetworkError]
      imgurError.message should include("Failed to connect to server")
    }

    it("should map SocketTimeoutException to NetworkError") {
      val error = new java.net.SocketTimeoutException("Read timed out")
      val handled =
        ImgurClient.handleErrors(IO.raiseError[String](error)).unsafeRunSync()

      handled.isLeft shouldBe true
      val imgurError = handled.swap.getOrElse(fail("Expected Left"))
      imgurError shouldBe a[ImgurError.NetworkError]
      imgurError.message should include("Connection to server timed out")
    }

    it("should map IOException to NetworkError") {
      val error = new java.io.IOException("Failed to read response")
      val handled =
        ImgurClient.handleErrors(IO.raiseError[String](error)).unsafeRunSync()

      handled.isLeft shouldBe true
      val imgurError = handled.swap.getOrElse(fail("Expected Left"))
      imgurError shouldBe a[ImgurError.NetworkError]
      imgurError.message should include("I/O error during request")
    }

    it("should map other exceptions to UnknownError") {
      val error = new RuntimeException("Something unexpected happened")
      val handled =
        ImgurClient.handleErrors(IO.raiseError[String](error)).unsafeRunSync()

      handled.isLeft shouldBe true
      val imgurError = handled.swap.getOrElse(fail("Expected Left"))
      imgurError shouldBe a[ImgurError.UnknownError]
      imgurError.message should include("Unexpected error")
    }
  }

  describe("ImgurClient.makeRequest") {
    it("should create a request with the correct method, URI, and headers") {
      val testUri    = Uri.unsafeFromString("https://test.example.com/image")
      val imageBytes = "test image".getBytes

      val request = ImgurClient.makeRequest(testUri, imageBytes)

      // Verify request properties
      request.method shouldBe Method.POST
      request.uri shouldBe testUri

      // Verify Content-Type header
      val contentTypeHeader = request.headers.get(ci"Content-Type")
      contentTypeHeader.isDefined shouldBe true
      contentTypeHeader.get.head.value shouldBe "image/jpeg"

      // Verify the entity body (this is a bit trickier since it's wrapped in an EntityBody)
      // We'll just check that it exists
      request.body.compile.toVector.unsafeRunSync().isEmpty shouldBe false
    }
  }
}
