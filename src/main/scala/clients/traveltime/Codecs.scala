package clients.traveltime

import cats.effect.IO
import clients.traveltime.Models._
import common.Models.Coordinates
import io.circe.Decoder
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

object Codecs {
  // Decoder for Coordinates
  implicit val coordinatesDecoder: Decoder[Coordinates] = Decoder.instance {
    cursor =>
      for {
        coordinates <- cursor.as[List[Double]]
        lng = coordinates.head
        lat = coordinates.last
      } yield Coordinates(lat, lng)
  }

  // Decoder for TravelTimeGeocodingResult
  implicit val travelTimeGeocodingResultDecoder
      : Decoder[TravelTimeGeocodingResult] = Decoder
    .instance { cursor =>
      for {
        coordinates <- cursor
          .downField("geometry")
          .downField("coordinates")
          .as[Coordinates]

        name    <- cursor.downField("properties").get[Option[String]]("name")
        city    <- cursor.downField("properties").get[Option[String]]("city")
        country <- cursor.downField("properties").get[Option[String]]("country")

      } yield TravelTimeGeocodingResult(
        coordinates = coordinates,
        name = name,
        city = city,
        country = country
      )
    }

  implicit val travelTimeGeocodingResponseDecoder
      : Decoder[TravelTimeGeocodingResponse] = Decoder.instance { c =>
    for {
      results <- c
        .downField("features")
        .as[List[TravelTimeGeocodingResult]]
    } yield TravelTimeGeocodingResponse(results)
  }

  // EntityDecoder for List[TravelTimeGeocodingResult]
  implicit val travelTimeGeocodingListEntityDecoder
      : EntityDecoder[IO, TravelTimeGeocodingResponse] =
    jsonOf[IO, TravelTimeGeocodingResponse]
}
