package common

import java.io.{File, PrintWriter}
import scala.io.Source

object ImageToAsciiTest {
  val filename = "C:\\Users\\ignsu\\OneDrive\\Documents\\GitHub\\cli-street-imagery\\src\\main\\scala\\common/testImage"

  def readFileLines(filename: String): List[String] = {
    val source = Source.fromFile(filename)
    try {
      source.getLines().toList
    } finally {
      source.close()
    }
  }

  def sampleVertically(lines: List[String], vertical: Int): List[String] = {
    if (vertical <= 0) {
      throw new IllegalArgumentException("Vertical sampling parameter must be positive")
    }

    if (vertical == 1) {
      // If vertical is 1, return all lines (no sampling)
      lines
    } else {
      // Keep only indices that are multiples of the vertical parameter
      lines.zipWithIndex
        .filter { case (_, index) => index % vertical == 0 }
        .map(_._1)
    }
  }

  def convertToGrayscale(lines: List[String]): List[String] = {
    lines.flatMap { line =>
      line.split(",").map(_.trim).filter(_.startsWith("0x")).map { hexStr =>
        val rgbValue = java.lang.Long.parseLong(hexStr.substring(2), 16)
        val r = (rgbValue >> 16) & 0xFF
        val g = (rgbValue >> 8) & 0xFF
        val b = rgbValue & 0xFF
        val grayscale = (0.299 * r + 0.587 * g + 0.114 * b).toInt
        f"0x${grayscale}%08x"
      }
    }
  }

  def grayscaleHexToAscii(grayscaleHexValues: List[String], width: Int, height: Int, charset: String): String = {
    // Make sure the charset is from darkest to brightest
    val asciiChars = charset.reverse

    // Map each grayscale hex value to an ASCII character
    val asciiPixels = grayscaleHexValues.map { hexStr =>
      // Extract the grayscale value from the hex string (last two digits)
      val grayscaleValue = Integer.parseInt(hexStr.substring(hexStr.length - 2), 16)

      // Map to ASCII character
      val index = ((grayscaleValue * (asciiChars.length - 1)) / 255.0).toInt
      // Clamp the index to prevent out of bounds issues
      val safeIndex = math.min(math.max(index, 0), asciiChars.length - 1)
      asciiChars(safeIndex)
    }

    // Build the ASCII image by converting the 1D array to 2D with proper line breaks
    val asciiImage = (0 until height)
      .map { y =>
        val startIndex = y * width
        val endIndex = startIndex + width

        // Check if we have enough pixels for this row
        if (startIndex < asciiPixels.length) {
          asciiPixels.slice(startIndex, math.min(endIndex, asciiPixels.length)).mkString
        } else {
          ""
        }
      }
      .mkString("\n")

    asciiImage.toString
  }

  def printAsciiToFile(asciiArt: String): Unit = {
    new PrintWriter(new File("ascii_image.txt")) {
      write(asciiArt);
      close()
    }
    println("\nASCII art has been saved to 'ascii_image.txt'")
  }

  def main(args: Array[String]): Unit = {
    val originalWidth = 463
    val originalHeight = 280
    val verticalSampling = 2

    val sampledHeight = originalHeight / verticalSampling
    val characters = ".:-=+*#%@" // From darkest to brightest (or configure as needed)
    val characters2 = " .'`^\\\",:;Il!i~+_-?][}{1)(|\\\\/*tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$"

    val rgbValues = readFileLines(filename)
    val sampledInput = sampleVertically(rgbValues, verticalSampling)

    val grayscaleValues = convertToGrayscale(sampledInput)

    // Convert grayscale hex values to ASCII art
    val asciiArt = grayscaleHexToAscii(grayscaleValues, originalWidth, sampledHeight, characters)

    printAsciiToFile(asciiArt)
  }
}