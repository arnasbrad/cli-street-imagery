package clients.imgur

import cats.effect.{IO, IOApp}

import java.nio.file.{Files, Paths}

object UploadImage extends IOApp.Simple {
  val run: IO[Unit] = ImgurClient.make().use { client =>
    val imagePath = "mapillary_image.jpg"
    val imageBytes = Files.readAllBytes(Paths.get(imagePath))

    for {
      res <- client
        .uploadImage(imageBytes)
        .value
      _ <- IO.println(res)
    } yield ()
  }
}
