package asciiart

import asciiart.ImageToAsciiTest.{
  convertToGrayscale,
  grayscaleHexToAscii,
  grayscaleHexToAsciiWithEdges,
  sampleHorizontally,
  sampleVertically
}

import java.io.{File, PrintWriter}
import java.nio.file.{Path, Paths}
import scala.io.Source

object Examples {
  private def readFile(path: Path): List[List[Int]] = {
    val source = Source.fromFile(path.toFile)
    try {
      source
        .getLines()
        .map { line =>
          // Split by comma and trim whitespace
          line
            .split(",")
            .map(_.trim)
            .filter(_.nonEmpty) // Filter out empty strings
            .map { hex =>
              if (hex.startsWith("0x")) {
                // Remove the "0x" prefix and parse as hexadecimal
                Integer.parseInt(hex.substring(2), 16)
              } else {
                // Try to parse as is (might be a plain hex number without prefix)
                try {
                  Integer.parseInt(hex, 16)
                } catch {
                  case _: NumberFormatException =>
                    0 // Default value for invalid input
                }
              }
            }
            .toList
        }
        .toList
    } finally {
      source.close()
    }
  }

  private def printAsciiToFile(asciiArt: String): Unit = {
    new PrintWriter(new File("ascii_image.txt")) {
      write(asciiArt);
      close()
    }
    println("\nASCII art has been saved to 'ascii_image.txt'")
  }

  def main(args: Array[String]): Unit = {
    val filePath = Paths.get("src/main/scala/asciiart/testImage")

    val rgbValues = readFile(filePath)

    // Vertical sampling NEEDS to be 2x of horizontal one
    val horizontalSampling = 1
    val verticalSampling   = horizontalSampling * 2

    val rgbValueSampledHorizontally =
      sampleHorizontally(rgbValues, horizontalSampling)
    val rgbValueSampledHorizontallyAndVertically =
      sampleVertically(rgbValueSampledHorizontally, verticalSampling)

    val grayscaleValues = convertToGrayscale(
      rgbValueSampledHorizontallyAndVertically
    )

    // Convert grayscale hex values to ASCII art
    val asciiArt = grayscaleHexToAsciiWithEdges(
      grayscaleValues,
      Charset.Extended,
      true
    )

    printAsciiToFile(asciiArt)

    /*val originalImage = List(
      List(1, 2, 3, 4, 5, 6),
      List(7, 8, 9, 10, 11, 12),
      List(2, 2, 4, 4, 8, 6),
      List(7, 8, 9, 10, 11, 12)
    )

    val rgbValueSampledHorizontally = sampleHorizontally(rgbValues, 3)
    val rgbValueSampledHorizontallyAndVertically =
      sampleVertically(rgbValueSampledHorizontally, 2)
    val a = convertToGrayscale(rgbValueSampledHorizontallyAndVertically)
    val asciiArt = grayscaleHexToAscii(
      a,
      Charset.Default
    )

    print(asciiArt)
    printAsciiToFile(asciiArt)*/
  }
}
