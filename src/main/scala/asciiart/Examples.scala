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
  private def readFileLines(path: Path): List[String] = {
    val source = Source.fromFile(path.toFile)
    try {
      source.getLines().toList
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

    val rgbValues    = readFileLines(filePath)
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
