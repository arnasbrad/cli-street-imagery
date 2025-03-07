package asciiart

import scala.util.Try

object ImageToAsciiTest {
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

  def grayscaleHexToAscii(
      grayscaleValues: List[List[Int]],
      width: Int,
      height: Int,
      charset: Charset
  ): String = {
    // Flatten the nested lists and map each grayscale value to an ASCII character
    val asciiPixels = grayscaleValues.flatten.map { rgbValue =>
      // Extract the grayscale value (since R=G=B in our grayscale RGB, we can use any channel)
      val grayscaleValue = rgbValue & 0xff // Blue channel

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
