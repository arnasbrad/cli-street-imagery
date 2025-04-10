package com.streetascii.asciiart

import com.streetascii.asciiart.Algorithms.{
  BrailleAlgorithm,
  EdgeDetectionAlgorithm,
  LuminanceAlgorithm
}
import com.streetascii.asciiart.Conversions.{
  charsToStringList,
  hexStringsToSampledGreyscaleDecimal
}

import java.io.{File, PrintWriter}
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
          charset,
          grayscaleValues.grayscaleDecimals
        )
      case EdgeDetectionAlgorithm =>
        EdgeDetectionAlgorithm.generate(
          charset,
          grayscaleValues.grayscaleDecimals
        )
      case BrailleAlgorithm =>
        BrailleAlgorithm.generate(
          Charset.BraillePatterns,
          grayscaleValues.grayscaleDecimals
        )

    }

    val formatForPrinting = charsToStringList(asciiArt)
    printAsciiToFile(formatForPrinting)
  }
}
