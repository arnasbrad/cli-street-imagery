package clients.mapillary

import cats.effect.{IO, IOApp}
import common.Models.{Coordinates, Radius}

import java.nio.file.{Files, Paths}

object GetImage extends IOApp.Simple {
  // Will download img to root dir
  val apiKey = "Enter your api key here for testing"

  val run: IO[Unit] = MapillaryClient.make().use { client =>
    client.getImage("2966993343542765", apiKey).value.flatMap {
      // Handle the Either result
      case Right(imageBytes) =>
        // Success path - do what you were doing before
        for {
          _ <- IO.println(
            s"Successfully retrieved image. Size: ${imageBytes.length} bytes"
          )

          _ <- IO.blocking {
            val outputPath = Paths.get("mapillary_image.jpg")
            Files.write(outputPath, imageBytes)
          }

          _ <- IO.println(s"Image saved to mapillary_image.jpg")
        } yield ()

      case Left(error) =>
        IO.println(error)
    }
  }
}

object GetImageIdsByLocation extends IOApp.Simple {
  // Will download img to root dir
  val apiKey = "Enter your api key here for testing"

  val run: IO[Unit] =
    MapillaryClient.make().use { client =>
      client
        .getImageIdsByLocation(
          Coordinates.unsafeCreate(55.597, 12.967),
          Radius.unsafeCreate(50),
          "token"
        )
        .value
        .flatMap {
          case Right(res) => IO.println(res)
          case Left(e)    => IO.println(e)
        }
    }
}
