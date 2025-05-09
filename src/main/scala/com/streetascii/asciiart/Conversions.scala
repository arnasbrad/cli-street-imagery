package com.streetascii.asciiart

import com.streetascii.asciiart.Models.{
  ColoredPixels,
  HexImage,
  ImageHeight,
  ImageWidth,
  RGB
}
import cats.effect.{IO, Resource}

import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

trait Conversions {
  def hexStringsToSampledGreyscaleDecimal(
      horizontalSampling: Int,
      verticalSampling: Int,
      rgbValues: Array[String],
      lineWidth: Int
  ): ColoredPixels

  def convertBytesToHexImage(imageBytes: Array[Byte]): IO[HexImage]
}

object Conversions extends Conversions {
  def hexStringsToSampledGreyscaleDecimal(
      horizontalSampling: Int,
      verticalSampling: Int,
      rgbValues: Array[String],
      lineWidth: Int
  ): ColoredPixels = {
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

  def convertTo2DArray(
      hexImage: HexImage
  ): Array[Array[String]] = {

    // Use the grouped method to split the array into chunks of innerLength
    hexImage.hexStrings.grouped(hexImage.width.value).toArray
  }

  def charsToStringList(chars: Array[Array[Char]]): List[String] = {
    // Convert each row of chars to a string and collect into a List
    chars.map { row =>
      // Extract just the character from each tuple and join into a single string
      row.mkString
    }.toList
  }

  def sampleHorizontally(
      image: Array[Array[String]],
      downsampleFactor: Int
  ): Array[Array[String]] = {
    // Ensure downsample factor is at least 1
    val safeFactor = math.max(1, downsampleFactor)

    // For each row (vertical pixel), select columns at regular intervals
    image.map { row =>
      val rowLength    = row.length
      val newRowLength = (rowLength + safeFactor - 1) / safeFactor
      (0 until newRowLength).map { i =>
        val index = i * safeFactor
        if (index < rowLength) row(index) else ""
      }.toArray
    }
  }

  def sampleVertically(
      lines: Array[Array[String]],
      vertical: Int
  ): Array[Array[String]] = {
    // Use max to ensure vertical is at least 1
    val safeVertical = Math.max(1, vertical)

    if (safeVertical == 1) {
      // If vertical is 1, return all lines (no sampling)
      lines
    } else {
      // Keep only indices that are multiples of the vertical parameter
      lines.zipWithIndex
        .filter { case (_, index) => index % safeVertical == 0 }
        .map { case (line, _) => line }
    }
  }

  def convertToGrayscale(
      lines: Array[Array[String]]
  ): ColoredPixels = {
    // Pre-allocate arrays for the grayscale values and colors
    val height = lines.length
    val width  = if (height > 0) lines(0).length else 0

    val grayscaleValues = Array.ofDim[String](height, width)
    val colorValues     = Array.ofDim[RGB](height, width)

    // Fill arrays in a single pass
    for (y <- 0 until height) {
      for (x <- 0 until width) {
        val hexString = lines(y)(x)

        // Parse the hex string to an integer
        val rgbValue = java.lang.Long.parseLong(hexString, 16).toInt

        // Extract RGB components
        val r = (rgbValue >> 16) & 0xff
        val g = (rgbValue >> 8) & 0xff
        val b = rgbValue & 0xff

        // Calculate grayscale using the same formula
        val grayscale = (0.299 * r + 0.587 * g + 0.114 * b).toInt

        // Create new RGB where R=G=B=grayscale
        val grayScaleRgbValue = (grayscale << 16) | (grayscale << 8) | grayscale

        // Store values directly in the respective arrays
        grayscaleValues(y)(x) = grayScaleRgbValue.toString
        colorValues(y)(x) = RGB(r, g, b)
      }
    }

    ColoredPixels(grayscaleValues, colorValues)
  }

  def convertBytesToHexImage(imageBytes: Array[Byte]): IO[HexImage] = {
    // Load the image
    Resource
      .make(IO(new ByteArrayInputStream(imageBytes)))(s => IO(s.close()))
      .use { stream =>
        IO(ImageIO.read(stream))
      }
      .map { image =>
        val width  = image.getWidth
        val height = image.getHeight

        // Create an array to hold all RGB values
        val rgbArray = new Array[Int](width * height)

        // Read RGB values from the image
        var index = 0
        for (y <- 0 until height) {
          for (x <- 0 until width) {
            val rgb = image.getRGB(x, y)
            // Convert ARGB to RGB (remove alpha channel)
            val rgbValue = rgb & 0x00ffffff
            rgbArray(index) = rgbValue
            index += 1
          }
        }

        HexImage(
          hexStrings = rgbArray.map(rgbToHex),
          width = ImageWidth(width),
          height = ImageHeight(height)
        )
      }
  }

  private def rgbToHex(rgb: Int): String = {
    // Format as 0x00RRGGBB
    f"00$rgb%06X"
  }
}
