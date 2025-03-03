package common

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
    val asciiImage = new StringBuilder()
    for (y <- 0 until height) {
      val startIndex = y * width
      val endIndex = startIndex + width
      // Check if we have enough pixels for this row
      if (startIndex < asciiPixels.length) {
        val row = asciiPixels.slice(startIndex, math.min(endIndex, asciiPixels.length)).mkString
        asciiImage.append(row).append("\n")
      }
    }

    asciiImage.toString
  }

  def main(args: Array[String]): Unit = {
    val width = 463
    val height = 280
    val characters = ".:-=+*#%@" // From darkest to brightest (or configure as needed)

    val lines = readFileLines(filename)
    val grayscaleValues = convertToGrayscale(lines)

    // Print a sample of grayscale values for verification
    println("Sample of grayscale values:")
    grayscaleValues.take(10).foreach(println)

    // Convert grayscale hex values to ASCII art
    val asciiArt = grayscaleHexToAscii(grayscaleValues, width, height, characters)

    // Print ASCII art
    println("\nASCII Art Image:")
    println(asciiArt)

    // Optionally save the ASCII art to a file
    import java.io.{File, PrintWriter}
    new PrintWriter(new File("ascii_image.txt")) { write(asciiArt); close() }
    println("\nASCII art has been saved to 'ascii_image.txt'")
  }
}