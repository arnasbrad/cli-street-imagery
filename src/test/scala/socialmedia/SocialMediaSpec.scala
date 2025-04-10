package socialmedia

import com.streetascii.socialmedia.SocialMedia
import org.http4s.Uri
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SocialMediaSpec extends AnyFlatSpec with Matchers {

  "SocialMedia.X" should "generate the correct Twitter/X sharing URI" in {
    // Setup
    val text      = "Check out this cool street view!"
    val imgurLink = "https://imgur.com/abc123"
    val x         = SocialMedia.X(text, imgurLink)

    // Create an expected URI using http4s directly
    val expectedUri = Uri
      .unsafeFromString("https://x.com/intent/post")
      .withQueryParam("text", text)
      .withQueryParam("url", imgurLink)

    // Assertions
    x.uri shouldEqual expectedUri
  }

  it should "correctly handle special characters in text and URL" in {
    // Setup with special characters
    val text      = "Check this out! It's amazing & cool"
    val imgurLink = "https://imgur.com/abc?id=123&type=image"
    val x         = SocialMedia.X(text, imgurLink)

    // Create an expected URI using http4s directly
    val expectedUri = Uri
      .unsafeFromString("https://x.com/intent/post")
      .withQueryParam("text", text)
      .withQueryParam("url", imgurLink)

    // Assertions
    x.uri shouldEqual expectedUri
  }

  "SocialMedia.FaceBook" should "generate the correct Facebook sharing URI" in {
    // Setup
    val imgurLink = "https://imgur.com/abc123"
    val facebook  = SocialMedia.FaceBook(imgurLink)

    // Create an expected URI using http4s directly
    val expectedUri = Uri
      .unsafeFromString("https://www.facebook.com/sharer/sharer.php")
      .withQueryParam("u", imgurLink)

    // Assertions
    facebook.uri shouldEqual expectedUri
  }

  it should "correctly handle special characters in URL" in {
    // Setup with special characters
    val imgurLink = "https://imgur.com/abc?id=123&type=image"
    val facebook  = SocialMedia.FaceBook(imgurLink)

    // Create an expected URI using http4s directly
    val expectedUri = Uri
      .unsafeFromString("https://www.facebook.com/sharer/sharer.php")
      .withQueryParam("u", imgurLink)

    // Assertions
    facebook.uri shouldEqual expectedUri
  }
}
