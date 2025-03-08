package asciiart

import asciiart.ImageToAsciiTest.{
  brailleAlgorithm,
  convertToGrayscale,
  edgeDetectionAlgorithm,
  luminanceAlgorithm,
  sampleHorizontally,
  sampleVertically
}

import java.io.{File, PrintWriter}
import java.nio.file.{Path, Paths}
import scala.io.Source

object Examples {
  sealed trait Algorithm
  case object Luminance     extends Algorithm
  case object EdgeDetection extends Algorithm
  case object Braille       extends Algorithm

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

  private def imageDataTransformations(
      horizontalSampling: Int,
      verticalSampling: Int,
      rgbValues: List[List[Int]]
  ): List[List[Int]] = {
    val rgbValueSampledHorizontally =
      sampleHorizontally(rgbValues, horizontalSampling)
    val rgbValueSampledHorizontallyAndVertically =
      sampleVertically(rgbValueSampledHorizontally, verticalSampling)

    convertToGrayscale(
      rgbValueSampledHorizontallyAndVertically
    )
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
    val algorithm          = "braille"
    val charset            = Charset.Extended

    val grayscaleValues =
      imageDataTransformations(horizontalSampling, verticalSampling, rgbValues)

    val settings = algorithm match {
      case "edge"    => EdgeDetection
      case "braille" => Braille
      case _         => Luminance // Default to luminance
    }

    val asciiArt = settings match {
      case Luminance =>
        luminanceAlgorithm(grayscaleValues, charset)
      case EdgeDetection =>
        edgeDetectionAlgorithm(grayscaleValues, charset, false)
      case Braille =>
        brailleAlgorithm(grayscaleValues, Charset.BraillePatterns)
    }

    printAsciiToFile(asciiArt)
  }
}
