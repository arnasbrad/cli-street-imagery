package com.streetascii.clients.traveltime

import cats.effect.{IO, IOApp}
import com.streetascii.clients.traveltime.Models.{ApiKey, AppId}

object GeocodingSearch extends IOApp.Simple {
  private val appId  = AppId.unsafeCreate("yourid")
  private val apiKey = ApiKey.unsafeCreate("yourkey")

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
