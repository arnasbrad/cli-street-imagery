package asciiart

import scala.util.Try

object ImageToAsciiTest {
  def sampleHorizontally(
      image: List[List[Int]],
      downsampleFactor: Int
  ): List[List[Int]] = {
    // Ensure downsample factor is at least 1
    val safeFactor = math.max(1, downsampleFactor)

    // For each row (vertical pixel), select columns at regular intervals
    image.map { row =>
      val rowLength    = row.length
      val newRowLength = (rowLength + safeFactor - 1) / safeFactor
      (0 until newRowLength).map { i =>
        row(i * safeFactor)
      }.toList
    }
  }

  def sampleVertically(
      lines: List[List[Int]],
      vertical: Int
  ): List[List[Int]] = {
    // Use max to ensure vertical is at least 1
    val safeVertical = Math.max(1, vertical)

    if (safeVertical == 1) {
      // If vertical is 1, return all lines (no sampling)
      lines
    } else {
      // Keep only indices that are multiples of the vertical parameter
      lines.zipWithIndex
        .filter { case (_, index) => index % safeVertical == 0 }
        .map { case (line, _) =>
          line
        }
    }
  }

  def convertToGrayscale(lines: List[List[Int]]): List[List[Int]] = {
    lines.map { line =>
      line.map { rgbValue =>
        // The values are already parsed integers, no need to handle "0x" prefix
        val r         = (rgbValue >> 16) & 0xff
        val g         = (rgbValue >> 8) & 0xff
        val b         = rgbValue & 0xff
        val grayscale = (0.299 * r + 0.587 * g + 0.114 * b).toInt

        // Return grayscale as an RGB value where R=G=B=grayscale
        (grayscale << 16) | (grayscale << 8) | grayscale
      }
    }
  }

  def luminanceAlgorithm(
      grayscaleValues: List[List[Int]],
      charset: Charset
  ): String = {
    // Calculate the actual dimensions from the processed data
    val height = grayscaleValues.length
    val width  = if (height > 0) grayscaleValues.head.length else 0

    // Convert each row to a string of ASCII characters and join with newlines
    grayscaleValues
      .map { row =>
        row.map { rgbValue =>
          // Extract the grayscale value (since R=G=B in our grayscale RGB, we can use any channel)
          val grayscaleValue = rgbValue & 0xff // Blue channel

          // Map to ASCII character
          val index =
            ((grayscaleValue * (charset.value.length - 1)) / 255.0).toInt
          // Clamp the index to prevent out of bounds issues
          val safeIndex = math.min(math.max(index, 0), charset.value.length - 1)
          charset.value(safeIndex)
        }.mkString
      }
      .mkString("\n")
  }

  def detectEdges(
      grayscaleValues: List[List[Int]],
      invert: Boolean = true
  ): List[List[Int]] = {
    val height = grayscaleValues.length
    val width  = if (height > 0) grayscaleValues.head.length else 0

    // Return original image if too small for edge detection
    if (height < 3 || width < 3) return grayscaleValues

    // Sobel operators
    val sobelX = List(
      List(-1, 0, 1),
      List(-2, 0, 2),
      List(-1, 0, 1)
    )

    val sobelY = List(
      List(-1, -2, -1),
      List(0, 0, 0),
      List(1, 2, 1)
    )

    // Create output matrix
    val edges = Array.ofDim[Int](height - 2, width - 2)

    // Apply Sobel operators
    for (y <- 1 until height - 1) {
      for (x <- 1 until width - 1) {
        var gx = 0
        var gy = 0

        // Apply convolution with Sobel kernels
        for (ky <- 0 until 3) {
          for (kx <- 0 until 3) {
            val pixelValue = grayscaleValues(y - 1 + ky)(x - 1 + kx) & 0xff
            gx += pixelValue * sobelX(ky)(kx)
            gy += pixelValue * sobelY(ky)(kx)
          }
        }

        // Calculate magnitude
        val magnitude = math.min(255, math.sqrt(gx * gx + gy * gy).toInt)

        // Invert if requested
        val value = if (invert) 255 - magnitude else magnitude

        edges(y - 1)(x - 1) = value
      }
    }

    // Convert Array to List
    edges.map(_.toList).toList
  }

  def edgeDetectionAlgorithm(
      grayscaleValues: List[List[Int]],
      charset: Charset,
      invert: Boolean = true
  ): String = {
    // Detect edges
    val edgeValues = detectEdges(grayscaleValues, invert)

    // Convert to ASCII
    edgeValues
      .map { row =>
        row.map { value =>
          val index     = ((value * (charset.value.length - 1)) / 255.0).toInt
          val safeIndex = math.min(math.max(index, 0), charset.value.length - 1)
          charset.value(safeIndex)
        }.mkString
      }
      .mkString("\n")
  }
}
