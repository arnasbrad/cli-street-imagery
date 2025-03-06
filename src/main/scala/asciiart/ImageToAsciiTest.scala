package asciiart

import scala.util.Try

object ImageToAsciiTest {
  def sampleVertically(lines: List[String], vertical: Int): List[String] = {
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

  def convertToGrayscale(lines: List[String]): List[String] = {
    def hexToRgbValue(hex: String): Either[Throwable, Long] =
      Try(java.lang.Long.parseLong(hex.substring(2), 16)).toEither

    val result = for {
      line <- lines
      hexStr <- Try(line.split(",").toList)
        .getOrElse(List.empty)
        .map(_.trim)
        .filter(_.startsWith("0x"))
    } yield {
      hexToRgbValue(hexStr) match {
        case Right(rgbValue) =>
          val r         = (rgbValue >> 16) & 0xff
          val g         = (rgbValue >> 8) & 0xff
          val b         = rgbValue & 0xff
          val grayscale = (0.299 * r + 0.587 * g + 0.114 * b).toInt
          f"0x${grayscale}%08x"
        case Left(_) =>
          "0x00000000" // Default for unparseable values
      }
    }

    result
  }

  def grayscaleHexToAscii(
      grayscaleHexValues: List[String],
      width: Int,
      height: Int,
      charset: Charset
  ): String = {
    // Map each grayscale hex value to an ASCII character
    val asciiPixels = grayscaleHexValues.map { hexStr =>
      // Extract the grayscale value from the hex string (last two digits)
      val grayscaleValue =
        Try(
          Integer
            .parseInt(
              hexStr.substring(hexStr.length - 2),
              16
            )
        ).getOrElse(0)

      // Map to ASCII character
      val index = ((grayscaleValue * (charset.value.length - 1)) / 255.0).toInt
      // Clamp the index to prevent out of bounds issues
      val safeIndex = math.min(math.max(index, 0), charset.value.length - 1)
      charset.value(safeIndex)
    }

    // Build the ASCII image by converting the 1D array to 2D with proper line breaks
    val asciiImage = (0 until height)
      .map { row =>
        val startIndex = row * width
        val endIndex   = startIndex + width

        // Check if we have enough pixels for this row
        if (startIndex < asciiPixels.length) {
          asciiPixels
            .slice(startIndex, math.min(endIndex, asciiPixels.length))
            .mkString
        } else {
          ""
        }
      }
      .mkString("\n")

    asciiImage
  }
}
