package asciiart

import asciiart.Models.{
  AlgorithmConfig,
  BrailleConfig,
  EdgeDetectionConfig,
  LuminanceConfig
}

import scala.util.{Failure, Success, Try}

object Algorithms {
  trait AsciiAlgorithm[T <: AlgorithmConfig] {
    def generate(config: T): Array[Array[Char]]
  }

  case object LuminanceAlgorithm extends AsciiAlgorithm[LuminanceConfig] {
    override def generate(config: LuminanceConfig): Array[Array[Char]] =
      luminanceAlgorithm(config.input, config.charset)

    private def luminanceAlgorithm(
        grayscaleValues: Array[Array[String]],
        charset: Charset
    ): Array[Array[Char]] = {
      grayscaleValues.map { row =>
        row.map { grayscaleString =>
          Try {
            // Parse the string to an integer
            val rgbValue = grayscaleString.toInt

            // In grayscale RGB, all channels should be the same, so we can extract any
            // If these are already single-channel values, use them directly
            val grayscaleValue = if (rgbValue > 255) {
              // This is a packed RGB value, extract one channel
              (rgbValue & 0xff)
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

  case object EdgeDetectionAlgorithm
      extends AsciiAlgorithm[EdgeDetectionConfig] {
    override def generate(config: EdgeDetectionConfig): Array[Array[Char]] =
      edgeDetectionAlgorithm(config.input, config.charset, config.invert)

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

      // Generate coordinates for the pixels we'll process
      val coordinates = (1 until height - 1).flatMap { y =>
        (1 until width - 1).map { x =>
          (y, x)
        }
      }.toArray

      // Group coordinates by y-value to form rows
      val edgeValues = coordinates
        .groupBy(_._1)
        .toArray
        .sortBy(_._1)
        .map { case (y, pixelsInRow) =>
          pixelsInRow
            .sortBy(_._2)
            .map { case (_, x) =>
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

              // Invert if requested and convert back to string
              if (invert) (255 - magnitude).toString else magnitude.toString
            }
        }

      edgeValues
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
        row.map { value =>
          Try {
            val intValue = value.toInt
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

  case object BrailleAlgorithm extends AsciiAlgorithm[BrailleConfig] {
    override def generate(config: BrailleConfig): Array[Array[Char]] =
      brailleAlgorithm(config.input, config.charset, config.threshold)

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
        charset: Charset,
        threshold: Int = 118
    ): Array[Array[Char]] = {
      val height = grayscaleValues.length
      val width  = if (height > 0) grayscaleValues(0).length else 0

      if (height <= 0 || width <= 0) {
        // Return a properly structured empty result
        return Array(Array.empty[Char])
      }

      // Calculate dimensions of Braille grid
      val brailleWidth  = (width + 1) / 2
      val brailleHeight = (height + 3) / 4

      try {
        // Pre-allocate the entire array with the exact dimensions
        val result = Array.fill(brailleHeight)(Array.fill(brailleWidth)(' '))

        // Populate the array cell by cell
        for (by <- 0 until brailleHeight) {
          for (bx <- 0 until brailleWidth) {
            val startX = bx * 2
            val startY = by * 4
            val patternIndex = createBraillePattern(
              grayscaleValues,
              startX,
              startY,
              width,
              height,
              threshold
            )
            result(by)(bx) = charset.value(patternIndex)
          }
        }

        result
      } catch {
        case e: Exception =>
          // If any error occurs, return a consistent array structure
          Array.fill(brailleHeight)(Array.fill(brailleWidth)(' '))
      }
    }
  }
}
