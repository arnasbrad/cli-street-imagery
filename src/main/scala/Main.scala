import cats.effect.{IO, IOApp}
import clients.mapillary.MapillaryClient

import java.nio.file.{Files, Paths}

object Main extends IOApp.Simple {
  val run: IO[Unit] = {
    MapillaryClient.make().use { client =>
      // Call getImage which returns EitherT[IO, MapillaryError, Array[Byte]]
      client.getImage("2966993343542765").value.flatMap {
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
}
