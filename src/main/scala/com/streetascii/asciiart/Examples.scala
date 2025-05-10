package com.streetascii.asciiart

import com.streetascii.asciiart.Algorithms.{
  BlankFilledAlgorithm,
  BrailleAlgorithm,
  EdgeDetectionCannyAlgorithm,
  EdgeDetectionSobelAlgorithm,
  LuminanceAlgorithm
}
import com.streetascii.asciiart.Conversions.{
  charsToStringList,
  hexStringsToSampledGreyscaleDecimal
}

import java.io.{File, PrintWriter}
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.concurrent.duration._

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

  def main(args: Array[String]): Unit = {
    val filePath  = "testBytesForIgnelis.txt"
    val lineWidth = 1024

    val rgbValues = readHexValues(filePath)

    // Vertical sampling NEEDS to be 2x of horizontal one
    val horizontalSampling = 1
    val verticalSampling   = horizontalSampling * 2
    val algorithm          = "canny"
    val charset            = Charset.Extended

    val grayscaleValues =
      hexStringsToSampledGreyscaleDecimal(
        horizontalSampling,
        verticalSampling,
        rgbValues,
        lineWidth
      )

    val settings = algorithm match {
      case "sobel"   => EdgeDetectionSobelAlgorithm
      case "canny"   => EdgeDetectionCannyAlgorithm
      case "braille" => BrailleAlgorithm
      case "blank"   => BlankFilledAlgorithm
      case _         => LuminanceAlgorithm // Default to luminance
    }

    val start = System.nanoTime()

    val asciiArt = settings match {
      case LuminanceAlgorithm =>
        LuminanceAlgorithm.generate(
          charset,
          grayscaleValues.grayscaleDecimals
        )
      case EdgeDetectionSobelAlgorithm =>
        EdgeDetectionSobelAlgorithm.generate(
          charset,
          grayscaleValues.grayscaleDecimals
        )
      case EdgeDetectionCannyAlgorithm =>
        EdgeDetectionCannyAlgorithm.generate(
          charset,
          grayscaleValues.grayscaleDecimals
        )
      case BrailleAlgorithm =>
        BrailleAlgorithm.generate(
          Charset.BraillePatterns,
          grayscaleValues.grayscaleDecimals
        )
      case BlankFilledAlgorithm =>
        BlankFilledAlgorithm.generate(
          Charset.Blank,
          grayscaleValues.grayscaleDecimals
        )
    }

    val end      = System.nanoTime()
    val duration = (end - start).nanos
    println(s"Time taken: ${duration.toMillis} ms")

    val formatForPrinting = charsToStringList(asciiArt)
    printAsciiToFile(formatForPrinting)
  }
}
