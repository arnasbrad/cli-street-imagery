package asciiart

import asciiart.Algorithms._
import asciiart.Conversions._
import asciiart.Models._

import java.io.{File, PrintWriter}
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.util.{Failure, Success, Try}

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

  private def calculateAverageBrightness(
      packedRgbArray: Array[Array[String]]
  ): Int = {
    if (packedRgbArray.isEmpty || packedRgbArray.forall(_.isEmpty)) {
      return 0
    }
    println("aaa")
    // Calculate grayscale for a single RGB value
    def calculateBrightness(packedRgb: String): Int = {
      Try {
        val rgbValue = packedRgb.toDouble

        // Extract only the red component since R=G=B in this case
        val r = ((rgbValue / 65536) % 256).toInt
        println("bbb")
        // Return red value directly without the weighted calculation
        r.toInt
      } match {
        case Success(res) => res
        case Failure(e)   => 0
      }
    }

    // Flatten array, filter out nulls and empty strings, calculate grayscales
    val grayscaleValues = packedRgbArray.flatten
      .filter(rgb => rgb != null && rgb.nonEmpty)
      .map(a => calculateBrightness(a))

    // Calculate average if we have values, otherwise return 0.0
    if (grayscaleValues.nonEmpty) {
      (grayscaleValues.sum / grayscaleValues.length).toInt
    } else {
      0
    }
  }

  def main(args: Array[String]): Unit = {
    val filePath  = "testBytesForIgnelis.txt"
    val lineWidth = 1024

    val rgbValues = readHexValues(filePath)

    // Vertical sampling NEEDS to be 2x of horizontal one
    val horizontalSampling = 1
    val verticalSampling   = horizontalSampling * 2
    val algorithm          = "braille"
    val charset            = Charset.Braille

    val grayscaleValues =
      hexStringsToSampledGreyscaleDecimal(
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
        LuminanceAlgorithm.generate(
          LuminanceConfig(grayscaleValues.grayscaleDecimals, charset)
        )
      case EdgeDetectionAlgorithm =>
        EdgeDetectionAlgorithm.generate(
          EdgeDetectionConfig(
            grayscaleValues.grayscaleDecimals,
            charset,
            invert = false
          )
        )
      case BrailleAlgorithm =>
        BrailleAlgorithm.generate(
          BrailleConfig(
            grayscaleValues.grayscaleDecimals,
            Charset.BraillePatterns
          )
        )

    }

    val formatForPrinting = charsToStringList(asciiArt)
    printAsciiToFile(formatForPrinting)
  }
}
