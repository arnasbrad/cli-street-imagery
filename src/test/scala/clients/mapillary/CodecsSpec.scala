package clients.mapillary

import io.circe.parser._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import com.streetascii.clients.mapillary.Codecs._
import com.streetascii.clients.mapillary.Models._
import com.streetascii.common.Models.Coordinates

class CodecsSpec extends AnyFunSpec with Matchers {
  describe("Mapillary Codecs") {
    describe("mapillaryImageDetailsDecoder") {
      it("should decode valid MapillaryImageDetails JSON") {
        val json =
          """
          {
            "id": "123456789",
            "sequence": "seq123456",
            "compass_angle": 90.5,
            "geometry": {
              "coordinates": [10.123, 59.456]
            },
            "thumb_1024_url": "https://images.mapillary.com/123456789/thumb-1024.jpg",
            "thumb_original_url": "https://images.mapillary.com/123456789/thumb-original.jpg"
          }
        """

        val result = decode[MapillaryImageDetails](json)
        result.isRight shouldBe true

        val imageDetails =
          result.getOrElse(fail("Failed to decode MapillaryImageDetails"))
        imageDetails.id shouldBe MapillaryImageId("123456789")
        imageDetails.sequenceId shouldBe MapillarySequenceId("seq123456")
        imageDetails.compassAngle shouldBe 90.5
        imageDetails.coordinates shouldBe Coordinates.unsafeCreate(
          59.456,
          10.123
        )
        imageDetails.thumb1024Url shouldBe Some(
          "https://images.mapillary.com/123456789/thumb-1024.jpg"
        )
        imageDetails.thumbOriginalUrl shouldBe Some(
          "https://images.mapillary.com/123456789/thumb-original.jpg"
        )
      }

      it("should handle missing optional fields") {
        val json =
          """
          {
            "id": "123456789",
            "sequence": "seq123456",
            "compass_angle": 90.5,
            "geometry": {
              "coordinates": [10.123, 59.456]
            }
          }
        """

        val result = decode[MapillaryImageDetails](json)
        result.isRight shouldBe true

        val imageDetails =
          result.getOrElse(fail("Failed to decode MapillaryImageDetails"))
        imageDetails.id shouldBe MapillaryImageId("123456789")
        imageDetails.compassAngle shouldBe 90.5
        imageDetails.sequenceId shouldBe MapillarySequenceId("seq123456")
        imageDetails.coordinates shouldBe Coordinates.unsafeCreate(
          59.456,
          10.123
        )
        imageDetails.thumb1024Url shouldBe None
        imageDetails.thumbOriginalUrl shouldBe None
      }

      it("should fail if any required field is missing") {
        val missingId =
          """
          {
            "sequence": "seq123456",
            "compass_angle": 90.5,
            "thumb_1024_url": "https://images.mapillary.com/123456789/thumb-1024.jpg",
            "thumb_original_url": "https://images.mapillary.com/123456789/thumb-original.jpg"
          }
        """

        val missingSequenceId =
          """
          {
            "id": "123456789",
            "compass_angle": 90.5,
            "thumb_1024_url": "https://images.mapillary.com/123456789/thumb-1024.jpg",
            "thumb_original_url": "https://images.mapillary.com/123456789/thumb-original.jpg"
          }
        """

        decode[MapillaryImageDetails](missingId).isLeft shouldBe true
        decode[MapillaryImageDetails](missingSequenceId).isLeft shouldBe true
      }

      it("should ignore extra fields") {
        val jsonWithExtra =
          """
          {
            "id": "123456789",
            "sequence": "seq123456",
            "compass_angle": 90.5,
            "geometry": {
              "coordinates": [10.123, 59.456]
            },
            "thumb_1024_url": "https://images.mapillary.com/123456789/thumb-1024.jpg",
            "thumb_original_url": "https://images.mapillary.com/123456789/thumb-original.jpg",
            "extra_field": "this should be ignored",
            "another_extra": 42
          }
        """

        val result = decode[MapillaryImageDetails](jsonWithExtra)
        result.isRight shouldBe true

        val imageDetails =
          result.getOrElse(fail("Failed to decode MapillaryImageDetails"))
        imageDetails.id shouldBe MapillaryImageId("123456789")
        imageDetails.compassAngle shouldBe 90.5
        imageDetails.sequenceId shouldBe MapillarySequenceId("seq123456")
        imageDetails.coordinates shouldBe Coordinates.unsafeCreate(
          59.456,
          10.123
        )
        imageDetails.thumb1024Url shouldBe Some(
          "https://images.mapillary.com/123456789/thumb-1024.jpg"
        )
        imageDetails.thumbOriginalUrl shouldBe Some(
          "https://images.mapillary.com/123456789/thumb-original.jpg"
        )
      }
    }

    describe("imagesResponseDecoder") {
      it("should decode valid ImagesResponse JSON") {
        val json =
          """
          {
            "data": [
              {
                "id": "123456789",
                "compass_angle": 90.5,
                "geometry": {
                  "coordinates": [10.123, 59.456]
                }
              },
              {
                "id": "987654321",
                "compass_angle": 90.5,
                "geometry": {
                  "coordinates": [11.123, 60.456]
                }
              }
            ]
          }
        """

        val result = decode[ImagesResponse](json)
        println(result)
        result.isRight shouldBe true

        val imagesResponse =
          result.getOrElse(fail("Failed to decode ImagesResponse"))
        imagesResponse.data.length shouldBe 2
        imagesResponse.data.head.id.id shouldBe "123456789"
        imagesResponse.data.head.compassAngle shouldBe 90.5
        imagesResponse.data.head.coordinates.lat shouldBe 59.456
        imagesResponse.data.head.coordinates.lng shouldBe 10.123
        imagesResponse.data(1).id.id shouldBe "987654321"
        imagesResponse.data(1).compassAngle shouldBe 90.5
        imagesResponse.data(1).coordinates.lat shouldBe 60.456
        imagesResponse.data(1).coordinates.lng shouldBe 11.123
      }

      it("should handle empty data array") {
        val json =
          """
          {
            "data": []
          }
        """

        val result = decode[ImagesResponse](json)
        result.isRight shouldBe true

        val imagesResponse =
          result.getOrElse(fail("Failed to decode ImagesResponse"))
        imagesResponse.data.isEmpty shouldBe true
      }

      it("should fail if data field is missing") {
        val missingData =
          """
          {
            "other_field": "value"
          }
        """

        decode[ImagesResponse](missingData).isLeft shouldBe true
      }
    }

    describe("imageDataDecoder") {
      it("should decode valid ImageData JSON") {
        val json =
          """
          {
            "id": "123456789",
            "geometry": {
              "coordinates": [10.123, 59.456]
            },
            "compass_angle": 90.5
          }
        """

        val result = decode[ImageData](json)
        result.isRight shouldBe true

        val imageData = result.getOrElse(fail("Failed to decode ImageData"))
        imageData.id.id shouldBe "123456789"
        imageData.coordinates.lat shouldBe 59.456
        imageData.coordinates.lng shouldBe 10.123
        imageData.compassAngle shouldBe 90.5
      }

      it("should fail if id is missing") {
        val missingId =
          """
          {
            "geometry": {
              "coordinates": [10.123, 59.456]
            }
          }
        """

        decode[ImageData](missingId).isLeft shouldBe true
      }

      it("should fail if geometry or coordinates are missing") {
        val missingGeometry =
          """
          {
            "id": "123456789"
          }
        """

        val missingCoordinates =
          """
          {
            "id": "123456789",
            "geometry": {
              "other_field": "value"
            }
          }
        """

        decode[ImageData](missingGeometry).isLeft shouldBe true
        decode[ImageData](missingCoordinates).isLeft shouldBe true
      }
    }

    describe("mapillaryImageIdDecoder") {
      it("should decode valid MapillaryImageId JSON") {
        val json =
          """
          {
            "id": "123456789"
          }
        """

        val result = decode[MapillaryImageId](json)
        result.isRight shouldBe true

        val imageId =
          result.getOrElse(fail("Failed to decode MapillaryImageId"))
        imageId.id shouldBe "123456789"
      }

      it("should fail if id is missing") {
        val missingId =
          """
          {
            "other_field": "value"
          }
        """

        decode[MapillaryImageId](missingId).isLeft shouldBe true
      }
    }

    describe("sequenceImagesResponseDecoder") {
      it("should decode valid SequenceImagesResponse JSON") {
        val json =
          """
      {
        "data": [
          {"id": "832054311207247"},
          {"id": "3540820439482142"},
          {"id": "1159410088278870"},
          {"id": "204207022016727"}
        ]
      }
      """

        val result = decode[SequenceImagesResponse](json)
        result.isRight shouldBe true

        val response =
          result.getOrElse(fail("Failed to decode SequenceImagesResponse"))
        response.data.length shouldBe 4
        response.data.head.id shouldBe "832054311207247"
        response.data(1).id shouldBe "3540820439482142"
        response.data(2).id shouldBe "1159410088278870"
        response.data(3).id shouldBe "204207022016727"
      }

      it("should handle empty data array") {
        val json =
          """
      {
        "data": []
      }
      """

        val result = decode[SequenceImagesResponse](json)
        result.isRight shouldBe true

        val response =
          result.getOrElse(fail("Failed to decode SequenceImagesResponse"))
        response.data.isEmpty shouldBe true
      }

      it("should fail if data field is missing") {
        val missingData =
          """
      {
        "other_field": "value"
      }
      """

        decode[SequenceImagesResponse](missingData).isLeft shouldBe true
      }

      it("should fail if any id in the array is missing") {
        val missingId =
          """
      {
        "data": [
          {"id": "832054311207247"},
          {"other_field": "value"},
          {"id": "1159410088278870"}
        ]
      }
      """

        decode[SequenceImagesResponse](missingId).isLeft shouldBe true
      }
    }
  }
}
