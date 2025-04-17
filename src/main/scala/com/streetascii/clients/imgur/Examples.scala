package com.streetascii.clients.imgur

import cats.effect.{IO, IOApp}
import com.streetascii.clients.imgur.Models.ClientId

import java.nio.file.{Files, Paths}

object UploadImage extends IOApp.Simple {
  val clientId: ClientId = ClientId.unsafeCreate("id")

  val run: IO[Unit] = ImgurClient.make(clientId).use { client =>
    val imagePath  = "mapillary_image.jpg"
    val imageBytes = Files.readAllBytes(Paths.get(imagePath))

    for {
      res <- client
        .uploadImage(imageBytes)
        .value
      _ <- res match {
        case Right(a)  => IO.println(a)
        case Left(err) => IO.println(err.message)
      }
    } yield ()
  }
}
