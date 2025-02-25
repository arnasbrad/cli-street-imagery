import cats.effect.{IO, IOApp}
import clients.mapillary.MapillaryClient

import java.nio.file.{Files, Paths}

object Main extends IOApp.Simple {
  val run: IO[Unit] = {
    MapillaryClient.make().use { client =>
      for {
        // Get the image bytes
        imageBytes <- client.getImage("2966993343542765")

        // Print some info about the image
        _ <- IO.println(
          s"Successfully retrieved image. Size: ${imageBytes.length} bytes"
        )

        // Save to a file (optional)
        _ <- IO.blocking {
          val outputPath = Paths.get("mapillary_image.jpg")
          Files.write(outputPath, imageBytes)
        }

        _ <- IO.println(s"Image saved to mapillary_image.jpg")
      } yield ()
    }
  }
}
