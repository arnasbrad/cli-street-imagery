package asciiart

import asciiart.Models.{HexImage, ImageHeight, ImageWidth}
import cats.effect.{IO, Resource}

import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

object Conversions {
  def sampleHorizontally(
      image: List[List[Int]],
      downsampleFactor: Int
  ): List[List[Int]] = {
    // Ensure downsample factor is at least 1
    val safeFactor = math.max(1, downsampleFactor)

    // For each row (vertical pixel), select columns at regular intervals
    image.map { row =>
      val rowLength    = row.length
      val newRowLength = (rowLength + safeFactor - 1) / safeFactor
      (0 until newRowLength).map { i =>
        row(i * safeFactor)
      }.toList
    }
  }

  def sampleVertically(
      lines: List[List[Int]],
      vertical: Int
  ): List[List[Int]] = {
    // Use max to ensure vertical is at least 1
    val safeVertical = Math.max(1, vertical)

    if (safeVertical == 1) {
      // If vertical is 1, return all lines (no sampling)
      lines
    } else {
      // Keep only indices that are multiples of the vertical parameter
      lines.zipWithIndex
        .filter { case (_, index) => index % safeVertical == 0 }
        .map { case (line, _) =>
          line
        }
    }
  }

  def convertToGrayscale(lines: List[List[Int]]): List[List[Int]] = {
    lines.map { line =>
      line.map { rgbValue =>
        // The values are already parsed integers, no need to handle "0x" prefix
        val r         = (rgbValue >> 16) & 0xff
        val g         = (rgbValue >> 8) & 0xff
        val b         = rgbValue & 0xff
        val grayscale = (0.299 * r + 0.587 * g + 0.114 * b).toInt

        // Return grayscale as an RGB value where R=G=B=grayscale
        (grayscale << 16) | (grayscale << 8) | grayscale
      }
    }
  }

  def convertBytesToHexImage(imageBytes: Array[Byte]): IO[HexImage] = {
    // Load the image
    Resource
      .make(IO(new ByteArrayInputStream(imageBytes)))(s => IO(s.close()))
      .use { stream =>
        IO(ImageIO.read(stream))
      }
      .map { image =>
        val width  = image.getWidth
        val height = image.getHeight

        // Create an array to hold all RGB values
        val rgbArray = new Array[Int](width * height)

        // Read RGB values from the image
        var index = 0
        for (y <- 0 until height) {
          for (x <- 0 until width) {
            val rgb = image.getRGB(x, y)
            // Convert ARGB to RGB (remove alpha channel)
            val rgbValue = rgb & 0x00ffffff
            rgbArray(index) = rgbValue
            index += 1
          }
        }

        HexImage(
          hexStrings = rgbArray.map(rgbToHex),
          width = ImageWidth(width),
          height = ImageHeight(height)
        )
      }
  }

  private def rgbToHex(rgb: Int): String = {
    // Format as 0x00RRGGBB
    f"00$rgb%06X"
  }
}
