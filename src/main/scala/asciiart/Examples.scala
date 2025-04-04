package asciiart

import asciiart.Algorithms._
import asciiart.Conversions._
import asciiart.Models._

import java.awt.image.BufferedImage
import java.io.{File, PrintWriter}
import java.nio.file.{Path, Paths}
import javax.imageio.ImageIO
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object Examples {
  def readHexValues(filePath: String): Array[String] = {
    val buffer         = new ArrayBuffer[String]()
    var source: Source = null

    try {
      source = Source.fromFile(filePath)

      // Process the file content using functional approach
      val content = source.mkString

      // Split by comma and trim each value
      buffer ++= content
        .split(",")
        .map(_.trim)
        .filter(_.nonEmpty)

      buffer.toArray
    } catch {
      case e: Exception =>
        println(s"Error reading file: ${e.getMessage}")
        Array.empty[String]
    } finally {
      if (source != null) {
        source.close()
      }
    }
  }

  private def imageDataTransformations(
      horizontalSampling: Int,
      verticalSampling: Int,
      rgbValues: Array[String],
      lineWidth: Int
  ): Array[Array[String]] = {
    val hexImage =
      HexImage(rgbValues, ImageWidth(lineWidth), ImageHeight(64665))
    val twoDimentionalArray = convertTo2DArray(hexImage)
    val rgbValueSampledHorizontally =
      sampleHorizontally(twoDimentionalArray, horizontalSampling)
    val rgbValueSampledHorizontallyAndVertically =
      sampleVertically(rgbValueSampledHorizontally, verticalSampling)
    convertToGrayscale(
      rgbValueSampledHorizontallyAndVertically
    )
  }

  private def printAsciiToFile(asciiArt: List[String]): Unit = {
    new PrintWriter(new File("ascii_image.txt")) {
      // Write each string from the list on its own line
      asciiArt.foreach { line =>
        write(line)
        write("\n") // Add a newline after each line
      }
      close()
    }
    println("\nASCII art has been saved to 'ascii_image.txt'")
  }

  def main(args: Array[String]): Unit = {
    val filePath  = "testBytesForIgnelis.txt"
    val lineWidth = 1024

    val rgbValues = readHexValues(filePath)

    // Vertical sampling NEEDS to be 2x of horizontal one
    val horizontalSampling = 1
    val verticalSampling   = horizontalSampling * 2
    val algorithm          = "a"
    val charset            = Charset.Braille

    val grayscaleValues =
      imageDataTransformations(
        horizontalSampling,
        verticalSampling,
        rgbValues,
        lineWidth
      )

    val settings = algorithm match {
      case "edge"    => EdgeDetectionAlgorithm
      case "braille" => BrailleAlgorithm
      case _         => LuminanceAlgorithm // Default to luminance
    }

    val asciiArt = settings match {
      case LuminanceAlgorithm =>
        LuminanceAlgorithm.generate(LuminanceConfig(grayscaleValues, charset))
      case EdgeDetectionAlgorithm =>
        EdgeDetectionAlgorithm.generate(
          EdgeDetectionConfig(grayscaleValues, charset, invert = false)
        )
      case BrailleAlgorithm =>
        BrailleAlgorithm.generate(
          BrailleConfig(grayscaleValues, Charset.BraillePatterns)
        )
    }

    val formatForPrinting = charsToStringList(asciiArt)
    printAsciiToFile(formatForPrinting)
  }
}
