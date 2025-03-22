package clients.imgur

import io.circe.parser._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import Codecs._

class CodecsSpec extends AnyFunSpec with Matchers {
  describe("Imgur Codecs") {
    describe("imgurDataDecoder") {
      it("should decode valid ImgurData JSON") {
        val json =
          """
          {
            "id": "abcd1234",
            "link": "https://i.imgur.com/abcd1234.jpg",
            "deletehash": "xyz789deletehash"
          }
        """

        val result = decode[Models.ImgurData](json)
        result.isRight shouldBe true

        val imgurData = result.getOrElse(fail("Failed to decode ImgurData"))
        imgurData.id shouldBe "abcd1234"
        imgurData.link shouldBe "https://i.imgur.com/abcd1234.jpg"
        imgurData.deletehash shouldBe "xyz789deletehash"
      }

      it("should fail if any required field is missing") {
        val missingId =
          """
          {
            "link": "https://i.imgur.com/abcd1234.jpg",
            "deletehash": "xyz789deletehash"
          }
        """

        val missingLink =
          """
          {
            "id": "abcd1234",
            "deletehash": "xyz789deletehash"
          }
        """

        val missingDeletehash =
          """
          {
            "id": "abcd1234",
            "link": "https://i.imgur.com/abcd1234.jpg"
          }
        """

        decode[Models.ImgurData](missingId).isLeft shouldBe true
        decode[Models.ImgurData](missingLink).isLeft shouldBe true
        decode[Models.ImgurData](missingDeletehash).isLeft shouldBe true
      }

      it("should ignore extra fields") {
        val jsonWithExtra =
          """
          {
            "id": "abcd1234",
            "link": "https://i.imgur.com/abcd1234.jpg",
            "deletehash": "xyz789deletehash",
            "extra": "this should be ignored",
            "another_extra": 42
          }
        """

        val result = decode[Models.ImgurData](jsonWithExtra)
        result.isRight shouldBe true

        val imgurData = result.getOrElse(fail("Failed to decode ImgurData"))
        imgurData.id shouldBe "abcd1234"
        imgurData.link shouldBe "https://i.imgur.com/abcd1234.jpg"
        imgurData.deletehash shouldBe "xyz789deletehash"
      }
    }

    describe("imgurResponseDecoder") {
      it("should decode valid ImgurResponse JSON") {
        val json =
          """
          {
            "data": {
              "id": "abcd1234",
              "link": "https://i.imgur.com/abcd1234.jpg",
              "deletehash": "xyz789deletehash"
            },
            "success": true,
            "status": 200
          }
        """

        val result = decode[Models.ImgurResponse](json)
        result.isRight shouldBe true

        val imgurResponse =
          result.getOrElse(fail("Failed to decode ImgurResponse"))
        imgurResponse.success shouldBe true
        imgurResponse.status shouldBe 200
        imgurResponse.data.id shouldBe "abcd1234"
        imgurResponse.data.link shouldBe "https://i.imgur.com/abcd1234.jpg"
        imgurResponse.data.deletehash shouldBe "xyz789deletehash"
      }

      it("should fail if any required field is missing") {
        val missingData =
          """
          {
            "success": true,
            "status": 200
          }
        """

        val missingSuccess =
          """
          {
            "data": {
              "id": "abcd1234",
              "link": "https://i.imgur.com/abcd1234.jpg",
              "deletehash": "xyz789deletehash"
            },
            "status": 200
          }
        """

        val missingStatus =
          """
          {
            "data": {
              "id": "abcd1234",
              "link": "https://i.imgur.com/abcd1234.jpg",
              "deletehash": "xyz789deletehash"
            },
            "success": true
          }
        """

        decode[Models.ImgurResponse](missingData).isLeft shouldBe true
        decode[Models.ImgurResponse](missingSuccess).isLeft shouldBe true
        decode[Models.ImgurResponse](missingStatus).isLeft shouldBe true
      }

      it("should fail if the data object is invalid") {
        val invalidData =
          """
          {
            "data": {
              "id": "abcd1234",
              "link": "https://i.imgur.com/abcd1234.jpg"
              // missing deletehash
            },
            "success": true,
            "status": 200
          }
        """

        decode[Models.ImgurResponse](invalidData).isLeft shouldBe true
      }
    }
  }
}
