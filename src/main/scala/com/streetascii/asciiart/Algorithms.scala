package com.streetascii.asciiart

import scala.util.{Failure, Success, Try}

object Algorithms {
  trait AsciiAlgorithm {
    def generate(
        charset: Charset,
        input: Array[Array[String]],
        isText: Boolean = false
    ): Array[Array[Char]]
  }

  case object BlankFilledAlgorithm extends AsciiAlgorithm {
    override def generate(
        charset: Charset,
        input: Array[Array[String]],
        isText: Boolean
    ): Array[Array[Char]] = {
      // Get dimensions from input
      val height = input.length
      val width  = if (height > 0) input(0).length else 0

      // Create new 2D array with same dimensions as input
      Array.fill(height, width)(charset.value.head)
    }
  }

  case object LuminanceAlgorithm extends AsciiAlgorithm {
    override def generate(
        charset: Charset,
        input: Array[Array[String]],
        isText: Boolean
    ): Array[Array[Char]] =
      luminanceAlgorithm(input, charset, isText)

    private def luminanceAlgorithm(
        grayscaleValues: Array[Array[String]],
        charset: Charset,
        isText: Boolean
    ): Array[Array[Char]] = {
      val charsetString = charset.getValue(isText)
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
              ((grayscaleValue * (charsetString.length - 1)) / 255.0).toInt

            // Clamp the index to prevent out of bounds
            val safeIndex =
              math.min(math.max(index, 0), charsetString.length - 1)
            charsetString(safeIndex)
          } match {
            case Success(c) => c
            case Failure(e) =>
              println(e.getMessage)
              charsetString(0)
          }
        }
      }
    }
  }

  case object EdgeDetectionSobelAlgorithm extends AsciiAlgorithm {
    override def generate(
        charset: Charset,
        input: Array[Array[String]],
        isText: Boolean
    ): Array[Array[Char]] =
      edgeDetectionAlgorithm(input, charset, isText = isText, invert = false)

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
        isText: Boolean,
        invert: Boolean
    ): Array[Array[Char]] = {
      val charsetString = charset.getValue(isText)
      // Detect edges
      val edgeValues = detectEdges(grayscaleValues, invert)

      // Convert to ASCII chars
      edgeValues.map { row =>
        row.map { str =>
          Try {
            val intValue = str.toInt
            val index = ((intValue * (charsetString.length - 1)) / 255.0).toInt
            val safeIndex =
              math.min(math.max(index, 0), charsetString.length - 1)
            charsetString(safeIndex)
          } match {
            case Success(res) => res
            case Failure(e)   => charsetString.head
          }
        }
      }
    }
  }

  case object EdgeDetectionCannyAlgorithm extends AsciiAlgorithm {
    override def generate(
        charset: Charset,
        input: Array[Array[String]],
        isText: Boolean
    ): Array[Array[Char]] =
      edgeDetectionAlgorithm(input, charset, isText = isText, invert = false)

    private def detectEdges(
        grayscaleValues: Array[Array[String]],
        invert: Boolean = true
    ): (Array[Array[String]], Array[Array[Int]]) = {
      val height = grayscaleValues.length
      val width  = grayscaleValues.headOption.map(_.length).getOrElse(0)

      // Return original image if too small for edge detection
      if (height < 5 || width < 5)
        return (grayscaleValues, Array.ofDim[Int](height, width))

      // Convert string values to integers
      val image =
        grayscaleValues.map(_.map(v => Try(v.toInt & 0xff).getOrElse(0)))

      // Step 1: Apply Gaussian blur to reduce noise
      val blurred = applyGaussianBlur(image)

      // Step 2: Compute gradients using Sobel operators
      val (gradientMagnitudes, gradientDirections) = computeGradients(blurred)

      // Step 3: Apply non-maximum suppression
      val suppressed =
        applyNonMaximumSuppression(gradientMagnitudes, gradientDirections)

      // Step 4: Apply hysteresis thresholding
      val edges = applyHysteresisThresholding(suppressed)

      // Invert if requested and convert back to string array
      val stringEdges = edges.map(_.map { value =>
        val finalValue = if (invert) 255 - value else value
        finalValue.toString
      })

      // Return both the edge values and the directions
      (stringEdges, gradientDirections)
    }

    // Gaussian blur to reduce noise (5x5 kernel)
    private def applyGaussianBlur(
        image: Array[Array[Int]]
    ): Array[Array[Int]] = {
      val height = image.length
      val width  = image.headOption.map(_.length).getOrElse(0)

      // 5x5 Gaussian kernel (sigma ≈ 1.0)
      val kernel = Array(
        Array(2, 4, 5, 4, 2),
        Array(4, 9, 12, 9, 4),
        Array(5, 12, 15, 12, 5),
        Array(4, 9, 12, 9, 4),
        Array(2, 4, 5, 4, 2)
      )
      val kernelSum = 159 // Sum of all elements for normalization

      // Create indices without for-comprehension
      val indices =
        (0 until height).flatMap(y => (0 until width).map(x => (y, x)))

      // Map over all pixel positions
      indices.foldLeft(Array.ofDim[Int](height, width)) {
        case (result, (y, x)) =>
          if (y < 2 || y >= height - 2 || x < 2 || x >= width - 2) {
            // Border pixels remain unchanged
            result(y)(x) = image(y)(x)
          } else {
            // Apply kernel to interior pixels without for loops
            val sum = (0 until 5)
              .flatMap(ky =>
                (0 until 5)
                  .map(kx => image(y - 2 + ky)(x - 2 + kx) * kernel(ky)(kx))
              )
              .sum

            result(y)(x) = sum / kernelSum
          }
          result
      }
    }

    // Compute gradient magnitudes and directions using Sobel
    private def computeGradients(
        image: Array[Array[Int]]
    ): (Array[Array[Int]], Array[Array[Int]]) = {
      val height = image.length
      val width  = image.headOption.map(_.length).getOrElse(0)

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

      // Generate all pixel coordinates
      val indices =
        (0 until height).flatMap(y => (0 until width).map(x => (y, x)))

      // Process each pixel
      val emptyMagnitudes = Array.ofDim[Int](height, width)
      val emptyDirections = Array.ofDim[Int](height, width)

      // Fold over all coordinates to build result arrays
      indices.foldLeft((emptyMagnitudes, emptyDirections)) {
        case ((mags, dirs), (y, x)) =>
          if (y <= 0 || y >= height - 1 || x <= 0 || x >= width - 1) {
            // Skip border pixels
            (mags, dirs)
          } else {
            // Calculate gradients functionally
            val gx = (0 until 3)
              .flatMap(ky =>
                (0 until 3)
                  .map(kx => image(y - 1 + ky)(x - 1 + kx) * sobelX(ky)(kx))
              )
              .sum

            val gy = (0 until 3)
              .flatMap(ky =>
                (0 until 3)
                  .map(kx => image(y - 1 + ky)(x - 1 + kx) * sobelY(ky)(kx))
              )
              .sum

            // Calculate magnitude
            val magnitude = math.min(255, math.sqrt(gx * gx + gy * gy).toInt)
            mags(y)(x) = magnitude

            // Calculate direction and quantize
            val angle = math.atan2(gy.toDouble, gx.toDouble) * 180 / math.Pi
            val direction = ((angle + 180) % 180) match {
              case a if a < 22.5 || a >= 157.5 => 0   // horizontal (0 degrees)
              case a if a < 67.5               => 45  // diagonal (45 degrees)
              case a if a < 112.5              => 90  // vertical (90 degrees)
              case _                           => 135 // diagonal (135 degrees)
            }
            dirs(y)(x) = direction

            (mags, dirs)
          }
      }
    }

    // Apply non-maximum suppression to thin edges
    private def applyNonMaximumSuppression(
        magnitudes: Array[Array[Int]],
        directions: Array[Array[Int]]
    ): Array[Array[Int]] = {
      val height = magnitudes.length
      val width  = magnitudes.headOption.map(_.length).getOrElse(0)

      // Generate all pixel coordinates
      val indices =
        (0 until height).flatMap(y => (0 until width).map(x => (y, x)))

      // Process all pixels using foldLeft
      indices.foldLeft(Array.ofDim[Int](height, width)) {
        case (result, (y, x)) =>
          if (y <= 0 || y >= height - 1 || x <= 0 || x >= width - 1) {
            // Skip border pixels
            result
          } else {
            val dir = directions(y)(x)
            val mag = magnitudes(y)(x)

            // Check if the current pixel is a local maximum in the gradient direction
            val isLocalMax = dir match {
              case 0 =>
                mag >= magnitudes(y)(x - 1) && mag >= magnitudes(y)(
                  x + 1
                ) // horizontal
              case 45 =>
                mag >= magnitudes(y - 1)(x + 1) && mag >= magnitudes(y + 1)(
                  x - 1
                ) // diagonal
              case 90 =>
                mag >= magnitudes(y - 1)(x) && mag >= magnitudes(y + 1)(
                  x
                ) // vertical
              case 135 =>
                mag >= magnitudes(y - 1)(x - 1) && mag >= magnitudes(y + 1)(
                  x + 1
                ) // diagonal
              case _ => false
            }

            result(y)(x) = if (isLocalMax) mag else 0
            result
          }
      }
    }

    // Apply hysteresis thresholding
    private def applyHysteresisThresholding(
        suppressed: Array[Array[Int]]
    ): Array[Array[Int]] = {
      val height = suppressed.length
      val width  = suppressed.headOption.map(_.length).getOrElse(0)

      // Define high and low thresholds
      val highThreshold = 70 // Adjust these thresholds based on your needs
      val lowThreshold  = 30 // Typically highThreshold = 2-3 * lowThreshold

      // Generate all pixel coordinates
      val indices =
        (0 until height).flatMap(y => (0 until width).map(x => (y, x)))

      // First pass: mark strong and weak edges
      val initialResult = indices.foldLeft(Array.ofDim[Int](height, width)) {
        case (result, (y, x)) =>
          val value = suppressed(y)(x)
          result(y)(x) =
            if (value >= highThreshold) 255    // Strong edge
            else if (value >= lowThreshold) 25 // Weak edge (temporary value)
            else 0                             // Non-edge
          result
      }

      // Second pass: trace weak edges connected to strong edges
      // We need a recursive function to handle the iterative process
      def connectWeakEdges(current: Array[Array[Int]]): Array[Array[Int]] = {
        val interiorIndices = (1 until height - 1).flatMap(y =>
          (1 until width - 1).map(x => (y, x))
        )

        // Find weak edges that have strong neighbors and mark them for upgrade
        val upgrades = interiorIndices.filter { case (y, x) =>
          current(y)(x) == 25 && // Is a weak edge
          (0 until 3).exists(ky =>
            (0 until 3).exists(kx =>
              current(y - 1 + ky)(x - 1 + kx) == 255 // Has strong neighbor
            )
          )
        }

        if (upgrades.isEmpty) {
          // No more changes, we're done
          current
        } else {
          // Upgrade weak edges to strong and continue
          val updated = upgrades.foldLeft(current.map(_.clone())) {
            case (result, (y, x)) =>
              result(y)(x) = 255 // Upgrade to strong edge
              result
          }
          connectWeakEdges(updated) // Recursive call to continue process
        }
      }

      // Apply the recursive weak edge connection
      val connected = connectWeakEdges(initialResult)

      // Final pass: keep only strong edges
      indices.foldLeft(connected.map(_.clone())) { case (result, (y, x)) =>
        if (result(y)(x) == 25) {
          result(y)(x) =
            0 // Remove weak edges that weren't connected to strong ones
        }
        result
      }
    }

    // New method to map edge direction to appropriate characters
    private def mapDirectionToChar(
        direction: Int,
        intensity: Int,
        charsetString: String
    ): Char = {
      if (intensity == 0) return charsetString.head // Non-edge pixel

      // Get a set of directional characters based on the direction
      val directionalChar = direction match {
        case 0   => '-'  // Horizontal edge
        case 45  => '/'  // Diagonal (45 degrees)
        case 90  => '|'  // Vertical edge
        case 135 => '\\' // Diagonal (135 degrees)
        case _   => '+'  // Fallback or junction
      }

      // For very strong edges, we might want to use stronger visual characters
      if (intensity > 200) {
        direction match {
          case 0   => '═' // Strong horizontal
          case 45  => '╱' // Strong diagonal
          case 90  => '║' // Strong vertical
          case 135 => '╲' // Strong diagonal
          case _   => '╬' // Strong junction
        }
      } else {
        directionalChar
      }
    }

    // Modified edge detection algorithm to use directional characters
    private def edgeDetectionAlgorithm(
        grayscaleValues: Array[Array[String]],
        charset: Charset,
        isText: Boolean,
        invert: Boolean
    ): Array[Array[Char]] = {
      val charsetString = charset.getValue(isText)
      // Detect edges and get directions
      val (edgeValues, directions) = detectEdges(grayscaleValues, invert)

      // Convert to directional ASCII chars
      edgeValues.zipWithIndex.map { case (row, y) =>
        row.zipWithIndex.map { case (str, x) =>
          Try {
            val intValue = str.toInt

            // If this is an edge pixel, use directional character
            if (intValue > 50) { // Threshold to determine if it's an edge
              val direction = directions(y)(x)
              mapDirectionToChar(direction, intValue, charsetString)
            } else {
              // For non-edge pixels, use the regular charset mapping
              val index =
                ((intValue * (charsetString.length - 1)) / 255.0).toInt
              val safeIndex =
                math.min(math.max(index, 0), charsetString.length - 1)
              charsetString(safeIndex)
            }
          } match {
            case Success(res) => res
            case Failure(_)   => charsetString.head
          }
        }
      }
    }
  }

  case object BrailleAlgorithm extends AsciiAlgorithm {
    override def generate(
        charset: Charset,
        input: Array[Array[String]],
        isText: Boolean
    ): Array[Array[Char]] =
      brailleAlgorithm(input, charset, isText)

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
        charset: Charset,
        isText: Boolean
    ): Array[Array[Char]] = {
      val charsetString = charset.getValue(isText)
      val height        = grayscaleValues.length
      val width         = if (height > 0) grayscaleValues(0).length else 0
      val brightness    = calculateAverageBrightness(grayscaleValues) - 55

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
              charsetString(patternIndex)
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
