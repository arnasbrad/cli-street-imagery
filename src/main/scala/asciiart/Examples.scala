package asciiart

import asciiart.ImageToAsciiTest.{
  convertToGrayscale,
  grayscaleHexToAscii,
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

    val originalWidth    = 1931
    val originalHeight   = 700
    val verticalSampling = 2

    val sampledHeight = originalHeight / verticalSampling

    val rgbValues    = readFile(filePath)
    val sampledInput = sampleVertically(rgbValues, verticalSampling)

    val grayscaleValues = convertToGrayscale(sampledInput)

    // Convert grayscale hex values to ASCII art
    val asciiArt = grayscaleHexToAscii(
      grayscaleValues,
      originalWidth,
      sampledHeight,
      Charset.Default
    )

    printAsciiToFile(asciiArt)
  }
}
