package com.streetascii.asciiart

import scala.util.{Failure, Success, Try}

object Algorithms {
  trait AsciiAlgorithm {
    def generate(
        charset: Charset,
        input: Array[Array[String]]
    ): Array[Array[Char]]
  }

  case object LuminanceAlgorithm extends AsciiAlgorithm {
    override def generate(
        charset: Charset,
        input: Array[Array[String]]
    ): Array[Array[Char]] =
      luminanceAlgorithm(input, charset)

    private def luminanceAlgorithm(
        grayscaleValues: Array[Array[String]],
        charset: Charset
    ): Array[Array[Char]] = {
      grayscaleValues.map { row =>
        row.map { str =>
          Try {
            // Parse the string to an integer
            val rgbValue = str.toInt

            // In grayscale RGB, all channels should be the same, so we can extract any
            // If these are already single-channel values, use them directly
            val grayscaleValue = if (rgbValue > 255) {
              // This is a packed RGB value, extract one channel
              rgbValue & 0xff
            } else {
              // This is already a grayscale value
              rgbValue
            }

            // Map to ASCII character
            val index =
              ((grayscaleValue * (charset.value.length - 1)) / 255.0).toInt

            // Clamp the index to prevent out of bounds
            val safeIndex =
              math.min(math.max(index, 0), charset.value.length - 1)
            charset.value(safeIndex)
          } match {
            case Success(c) => c
            case Failure(e) =>
              println(e.getMessage)
              charset.value(0)
          }
        }
      }
    }
  }

  case object EdgeDetectionAlgorithm extends AsciiAlgorithm {
    override def generate(
        charset: Charset,
        input: Array[Array[String]]
    ): Array[Array[Char]] =
      edgeDetectionAlgorithm(input, charset, false)

    private def detectEdges(
        grayscaleValues: Array[Array[String]],
        invert: Boolean = true
    ): Array[Array[String]] = {
      val height = grayscaleValues.length
      val width  = grayscaleValues.headOption.map(_.length).getOrElse(0)

      // Return original image if too small for edge detection
      if (height < 3 || width < 3) return grayscaleValues

      // Sobel operators
      val sobelX = Array(
        Array(-1, 0, 1),
        Array(-2, 0, 2),
        Array(-1, 0, 1)
      )

      val sobelY = Array(
        Array(-1, -2, -1),
        Array(0, 0, 0),
        Array(1, 2, 1)
      )

      // Generate coordinates for the pixels we'll process (inner pixels only)
      val coordinates = (1 until height - 1).flatMap { y =>
        (1 until width - 1).map { x =>
          (y, x)
        }
      }.toArray

      // Process the inner pixels
      val processedCoordinates = coordinates.map { case (y, x) =>
        // Extract 3x3 window around the pixel and convert strings to integers
        val window = (0 until 3).map { ky =>
          (0 until 3).map { kx =>
            // Parse string to int, with fallback to 0 if parsing fails
            Try {
              grayscaleValues(y - 1 + ky)(x - 1 + kx).toInt & 0xff
            } match {
              case Success(res) => res
              case Failure(_)   => 0
            }
          }.toArray
        }.toArray

        // Apply Sobel operators using fold
        val gx = (0 until 3).foldLeft(0) { (acc, ky) =>
          acc + (0 until 3).foldLeft(0) { (innerAcc, kx) =>
            innerAcc + window(ky)(kx) * sobelX(ky)(kx)
          }
        }

        val gy = (0 until 3).foldLeft(0) { (acc, ky) =>
          acc + (0 until 3).foldLeft(0) { (innerAcc, kx) =>
            innerAcc + window(ky)(kx) * sobelY(ky)(kx)
          }
        }

        // Calculate magnitude
        val magnitude = math.min(255, math.sqrt(gx * gx + gy * gy).toInt)

        // Invert if requested and create result tuple with original color
        val edgeValue =
          if (invert) (255 - magnitude).toString else magnitude.toString
        ((y, x), edgeValue)
      }.toMap

      // Create the result array by copying original values and applying processed ones
      grayscaleValues.zipWithIndex.map { case (row, y) =>
        row.zipWithIndex.map { case (originalValue, x) =>
          processedCoordinates.getOrElse((y, x), originalValue)
        }
      }
    }

    private def edgeDetectionAlgorithm(
        grayscaleValues: Array[Array[String]],
        charset: Charset,
        invert: Boolean
    ): Array[Array[Char]] = {
      // Detect edges
      val edgeValues = detectEdges(grayscaleValues, invert)

      // Convert to ASCII chars
      edgeValues.map { row =>
        row.map { str =>
          Try {
            val intValue = str.toInt
            val index = ((intValue * (charset.value.length - 1)) / 255.0).toInt
            val safeIndex =
              math.min(math.max(index, 0), charset.value.length - 1)
            charset.value(safeIndex)
          } match {
            case Success(res) => res
            case Failure(e)   => charset.value.head
          }
        }
      }
    }
  }

  case object BrailleAlgorithm extends AsciiAlgorithm {
    override def generate(
        charset: Charset,
        input: Array[Array[String]]
    ): Array[Array[Char]] =
      brailleAlgorithm(input, charset)

    private def calculateAverageBrightness(
        packedRgbArray: Array[Array[String]]
    ): Int = {
      if (packedRgbArray.isEmpty || packedRgbArray.forall(_.isEmpty)) {
        return 0
      }

      // Calculate grayscale for a single RGB value
      def calculateBrightness(packedRgb: String): Int = {
        Try {
          val rgbValue = packedRgb.toDouble

          // Extract only the red component since R=G=B in this case
          val r = ((rgbValue / 65536) % 256).toInt

          // Return red value directly without the weighted calculation
          r.toInt
        } match {
          case Success(res) => res
          case Failure(e)   => 0
        }
      }

      // Flatten array, filter out nulls and empty strings, calculate grayscales
      val grayscaleValues = packedRgbArray.flatten
        .map(a => calculateBrightness(a))

      // Calculate average if we have values, otherwise return 0.0
      if (grayscaleValues.nonEmpty) {
        (grayscaleValues.sum / grayscaleValues.length).toInt
      } else {
        0
      }
    }

    private def createBraillePattern(
        grayscaleValues: Array[Array[String]],
        startX: Int,
        startY: Int,
        width: Int,
        height: Int,
        threshold: Int
    ): Int = {
      // Dot mapping in Braille:
      // 0 3
      // 1 4
      // 2 5
      // 6 7
      val dotPositions = List(
        (0, 0, 0x01), // top-left
        (0, 1, 0x02), // middle-left
        (0, 2, 0x04), // bottom-left
        (1, 0, 0x08), // top-right
        (1, 1, 0x10), // middle-right
        (1, 2, 0x20), // bottom-right
        (0, 3, 0x40), // lower-left
        (1, 3, 0x80)  // lower-right
      )

      dotPositions.foldLeft(0) { case (pattern, (dx, dy, value)) =>
        val x = startX + dx
        val y = startY + dy

        // Check if position is within bounds and darker than threshold
        if (
          x < width && y < height &&
          y >= 0 && x >= 0 && // Additional bounds check
          (grayscaleValues(y)(x).toInt & 0xff) < threshold
        ) {
          // Set the bit in the pattern
          pattern | value
        } else {
          // Keep the pattern unchanged
          pattern
        }
      }
    }

    private def brailleAlgorithm(
        grayscaleValues: Array[Array[String]],
        charset: Charset
    ): Array[Array[Char]] = {
      val height     = grayscaleValues.length
      val width      = if (height > 0) grayscaleValues(0).length else 0
      val brightness = calculateAverageBrightness(grayscaleValues) - 55

      if (height <= 0 || width <= 0) {
        // Return a properly structured empty result
        return Array(Array.empty[Char])
      }

      // Calculate dimensions of Braille grid
      val brailleWidth  = (width + 1) / 2
      val brailleHeight = (height + 3) / 4

      Try {
        // Pre-allocate the entire array with the exact dimensions
        val result =
          (0 until brailleHeight).map { by =>
            (0 until brailleWidth).map { bx =>
              val startX = bx * 2
              val startY = by * 4
              val patternIndex = createBraillePattern(
                grayscaleValues,
                startX,
                startY,
                width,
                height,
                brightness
              )
              charset.value(patternIndex)
            }.toArray
          }.toArray

        result
      } match {
        case Success(res) => res
        case Failure(e) =>
          Array.fill(brailleHeight)(Array.fill(brailleWidth)(' '))
      }
    }
  }
}
