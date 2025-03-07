package clients.traveltime

import cats.effect.{IO, IOApp}
import clients.traveltime.Models._

object GeocodingSearch extends IOApp.Simple {
  private val appId  = AppId.unsafeCreate("your-id")
  private val apiKey = ApiKey.unsafeCreate("your-key")

  val run: IO[Unit] = TravelTimeClient.make(appId, apiKey).use { client =>
    for {
      resp <- client.geocodingSearch("Parliament square").value

      _ <- resp match {
        case Right(res) => IO.println(res)
        case Left(e)    => IO.println(e, e.message)
      }
    } yield ()
  }
}
