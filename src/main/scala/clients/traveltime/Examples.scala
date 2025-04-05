package clients.traveltime

import cats.effect.{IO, IOApp}
import clients.traveltime.Models._

object GeocodingSearch extends IOApp.Simple {
  private val appId  = AppId.unsafeCreate("b29821a7")
  private val apiKey = ApiKey.unsafeCreate("b708d7215a458ae453789a7b596d51a5")

  val run: IO[Unit] = TravelTimeClient.make(appId, apiKey).use { client =>
    for {
      resp <- client.geocodingSearch("Kauno pilis").value

      _ <- resp match {
        case Right(res) => IO.println(res.features.head.coordinates)
        case Left(e)    => IO.println(e, e.message)
      }
    } yield ()
  }
}
