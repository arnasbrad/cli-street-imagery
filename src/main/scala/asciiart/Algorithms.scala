package asciiart

import asciiart.Models.{
  AlgorithmConfig,
  BrailleConfig,
  EdgeDetectionConfig,
  LuminanceConfig
}

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
          try {
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
          } catch {
            case e: Exception =>
              // Fallback character if parsing fails
              charset.value(0)
          }
        }
      }
    }
  }

  /*case object EdgeDetectionAlgorithm
      extends AsciiAlgorithm[EdgeDetectionConfig] {
    override def generate(config: EdgeDetectionConfig): String =
      edgeDetectionAlgorithm(config.input, config.charset, config.invert)

    private def detectEdges(
        grayscaleValues: List[List[Int]],
        invert: Boolean = true
    ): List[List[Int]] = {
      val height = grayscaleValues.length
      val width  = grayscaleValues.headOption.map(_.length).getOrElse(0)

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

      // Generate coordinates for the pixels we'll process
      val coordinates = (1 until height - 1).toList.flatMap { y =>
        (1 until width - 1).toList.map { x =>
          (y, x)
        }
      }

      // Create output matrix using functional operations
      coordinates
        .groupBy(_._1) // Group by y-coordinate
        .toList
        .sortBy(_._1) // Sort by y-coordinate
        .map { case (y, pixelsInRow) =>
          pixelsInRow
            .sortBy(_._2) // Sort by x-coordinate
            .map { case (_, x) =>
              // Extract 3x3 window around the pixel
              val window = (0 until 3)
                .map(ky =>
                  (0 until 3)
                    .map(kx => grayscaleValues(y - 1 + ky)(x - 1 + kx) & 0xff)
                    .toList
                )
                .toList

              // Apply Sobel operators through fold operations
              val gx = (0 until 3).foldLeft(0)((acc, ky) =>
                acc + (0 until 3).foldLeft(0)((innerAcc, kx) =>
                  innerAcc + window(ky)(kx) * sobelX(ky)(kx)
                )
              )

              val gy = (0 until 3).foldLeft(0)((acc, ky) =>
                acc + (0 until 3).foldLeft(0)((innerAcc, kx) =>
                  innerAcc + window(ky)(kx) * sobelY(ky)(kx)
                )
              )

              // Calculate magnitude
              val magnitude = math.min(255, math.sqrt(gx * gx + gy * gy).toInt)

              // Invert if requested
              if (invert) 255 - magnitude else magnitude
            }
        }
    }

    private def edgeDetectionAlgorithm(
        grayscaleValues: List[List[Int]],
        charset: Charset,
        invert: Boolean
    ): String = {
      // Detect edges
      val edgeValues = detectEdges(grayscaleValues, invert)

      // Convert to ASCII
      edgeValues
        .map { row =>
          row.map { value =>
            val index = ((value * (charset.value.length - 1)) / 255.0).toInt
            val safeIndex =
              math.min(math.max(index, 0), charset.value.length - 1)
            charset.value(safeIndex)
          }.mkString
        }
        .mkString("\n")
    }
  }

  case object BrailleAlgorithm extends AsciiAlgorithm[BrailleConfig] {
    override def generate(config: BrailleConfig): String =
      brailleAlgorithm(config.input, config.charset, config.threshold)

    private def createBraillePattern(
        grayscaleValues: List[List[Int]],
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
          x < width && y < height && (grayscaleValues(y)(x) & 0xff) < threshold
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
        grayscaleValues: List[List[Int]],
        charset: Charset,
        threshold: Int = 118
    ): String = {
      val height = grayscaleValues.length
      val width  = grayscaleValues.headOption.map(_.length).getOrElse(0)

      Option
        .when(height > 0 && width > 0) {
          // Calculate dimensions of Braille grid
          val brailleWidth  = (width + 1) / 2
          val brailleHeight = (height + 3) / 4

          val brailleRows = (0 until brailleHeight).map { by =>
            val rowChars = (0 until brailleWidth).map { bx =>
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
              charset.value(patternIndex)
            }
            rowChars.mkString
          }

          // Join the rows with newlines
          brailleRows.mkString("\n")
        }
        .getOrElse("")
    }
  }*/
}
