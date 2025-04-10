package clients.traveltime

import io.circe.parser._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import com.streetascii.clients.traveltime.Codecs._
import com.streetascii.clients.traveltime.Models._
import com.streetascii.common.Models.Coordinates

class CodecsSpec extends AnyFunSpec with Matchers {
  describe("TravelTime Codecs") {
    describe("coordinatesDecoder") {
      it("should decode valid coordinates array") {
        val json = """[12.345, 45.678]"""

        val result = decode[Coordinates](json)
        result.isRight shouldBe true

        val coordinates = result.getOrElse(fail("Failed to decode Coordinates"))
        coordinates.lng shouldBe 12.345
        coordinates.lat shouldBe 45.678
      }
    }

    describe("travelTimeGeocodingResultDecoder") {
      it("should decode valid TravelTimeGeocodingResult JSON") {
        val json =
          """
          {
            "geometry": {
              "coordinates": [12.345, 45.678]
            },
            "properties": {
              "name": "Example Location",
              "city": "Example City",
              "country": "Example Country"
            }
          }
        """

        val result = decode[TravelTimeGeocodingResult](json)
        result.isRight shouldBe true

        val geocodingResult =
          result.getOrElse(fail("Failed to decode TravelTimeGeocodingResult"))
        geocodingResult.coordinates.lng shouldBe 12.345
        geocodingResult.coordinates.lat shouldBe 45.678
        geocodingResult.name shouldBe Some("Example Location")
        geocodingResult.city shouldBe Some("Example City")
        geocodingResult.country shouldBe Some("Example Country")
      }

      it("should handle missing optional fields") {
        val json =
          """
          {
            "geometry": {
              "coordinates": [12.345, 45.678]
            },
            "properties": {}
          }
        """

        val result = decode[TravelTimeGeocodingResult](json)
        result.isRight shouldBe true

        val geocodingResult =
          result.getOrElse(fail("Failed to decode TravelTimeGeocodingResult"))
        geocodingResult.coordinates.lng shouldBe 12.345
        geocodingResult.coordinates.lat shouldBe 45.678
        geocodingResult.name shouldBe None
        geocodingResult.city shouldBe None
        geocodingResult.country shouldBe None
      }

      it("should fail if geometry or coordinates are missing") {
        val missingGeometry =
          """
          {
            "properties": {
              "name": "Example Location",
              "city": "Example City",
              "country": "Example Country"
            }
          }
        """

        val missingCoordinates =
          """
          {
            "geometry": {},
            "properties": {
              "name": "Example Location",
              "city": "Example City",
              "country": "Example Country"
            }
          }
        """

        decode[TravelTimeGeocodingResult](missingGeometry).isLeft shouldBe true
        decode[TravelTimeGeocodingResult](
          missingCoordinates
        ).isLeft shouldBe true
      }

      it("should not fail if properties field is missing") {
        val missingProperties =
          """
          {
            "geometry": {
              "coordinates": [12.345, 45.678]
            }
          }
        """

        decode[TravelTimeGeocodingResult](
          missingProperties
        ).isLeft shouldBe false
      }
    }

    describe("travelTimeGeocodingResponseDecoder") {
      it("should decode valid TravelTimeGeocodingResponse JSON") {
        val json =
          """
          {
            "features": [
              {
                "geometry": {
                  "coordinates": [12.345, 45.678]
                },
                "properties": {
                  "name": "Location 1",
                  "city": "City 1",
                  "country": "Country 1"
                }
              },
              {
                "geometry": {
                  "coordinates": [23.456, 56.789]
                },
                "properties": {
                  "name": "Location 2",
                  "city": "City 2",
                  "country": "Country 2"
                }
              }
            ]
          }
        """

        val result = decode[TravelTimeGeocodingResponse](json)
        result.isRight shouldBe true

        val response =
          result.getOrElse(fail("Failed to decode TravelTimeGeocodingResponse"))
        response.features.length shouldBe 2

        // Verify first result
        response.features.head.coordinates.lng shouldBe 12.345
        response.features.head.coordinates.lat shouldBe 45.678
        response.features.head.name shouldBe Some("Location 1")
        response.features.head.city shouldBe Some("City 1")
        response.features.head.country shouldBe Some("Country 1")

        // Verify second result
        response.features(1).coordinates.lng shouldBe 23.456
        response.features(1).coordinates.lat shouldBe 56.789
        response.features(1).name shouldBe Some("Location 2")
        response.features(1).city shouldBe Some("City 2")
        response.features(1).country shouldBe Some("Country 2")
      }

      it("should handle empty features array") {
        val json =
          """
          {
            "features": []
          }
        """

        val result = decode[TravelTimeGeocodingResponse](json)
        result.isRight shouldBe true

        val response =
          result.getOrElse(fail("Failed to decode TravelTimeGeocodingResponse"))
        response.features.isEmpty shouldBe true
      }

      it("should fail if features field is missing") {
        val missingFeatures =
          """
          {
            "other_field": "value"
          }
        """

        decode[TravelTimeGeocodingResponse](
          missingFeatures
        ).isLeft shouldBe true
      }
    }
  }
}
