package com.streetascii.colorfilters

import com.streetascii.asciiart.Models.RGB

object ColorConversions {
  def enhanceContrast(
      image: Array[Array[RGB]],
      intensity: Double
  ): Array[Array[RGB]] = {
    // Find the min and max values for each channel across the entire image
    val (minR, maxR, minG, maxG, minB, maxB) = findMinMaxValues(image)

    // Create a new image with enhanced contrast
    image.map(row =>
      row.map(pixel => {
        val newR = adjustChannel(pixel.r, minR, maxR, intensity)
        val newG = adjustChannel(pixel.g, minG, maxG, intensity)
        val newB = adjustChannel(pixel.b, minB, maxB, intensity)
        RGB(newR, newG, newB)
      })
    )
  }

  def correctForDeuteranopia(
      image: Array[Array[RGB]],
      intensity: Double
  ): Array[Array[RGB]] = {
    image.map(row =>
      row.map(pixel => {
        // Convert to linear RGB for better color math
        val r = sRGBToLinear(pixel.r)
        val g = sRGBToLinear(pixel.g)
        val b = sRGBToLinear(pixel.b)

        // First, simulate how deuteranopes see this color
        val dr = 0.625 * r + 0.375 * g + 0.0 * b
        val dg = 0.7 * r + 0.3 * g + 0.0 * b
        val db = 0.0 * r + 0.3 * g + 0.7 * b

        // Calculate the error between original and deuteranope vision
        val errorR = r - dr
        val errorG = g - dg
        val errorB = b - db

        // Adjust error based on intensity
        val adjustedErrorR = errorR * intensity
        val adjustedErrorG = errorG * intensity

        // Apply corrections by redistributing the error to enhance color differences
        // Specifically emphasize red-green differences by shifting them to blue channel
        val correctedR = r
        val correctedG = g
        val correctedB = b + adjustedErrorR * 0.7 + adjustedErrorG * 0.7

        // Ensure values stay in valid range
        RGB(
          linearToSRGB(math.min(1.0, math.max(0.0, correctedR))),
          linearToSRGB(math.min(1.0, math.max(0.0, correctedG))),
          linearToSRGB(math.min(1.0, math.max(0.0, correctedB)))
        )
      })
    )
  }

  def correctForProtanopia(
      image: Array[Array[RGB]],
      intensity: Double
  ): Array[Array[RGB]] = {
    image.map(row =>
      row.map(pixel => {
        // Convert to linear RGB for better color math
        val r = sRGBToLinear(pixel.r)
        val g = sRGBToLinear(pixel.g)
        val b = sRGBToLinear(pixel.b)

        // First, simulate how protanopes see this color
        val pr = 0.567 * r + 0.433 * g + 0.0 * b
        val pg = 0.558 * r + 0.442 * g + 0.0 * b
        val pb = 0.0 * r + 0.242 * g + 0.758 * b

        // Calculate the error between original and protanope vision
        val errorR = r - pr
        val errorG = g - pg
        val errorB = b - pb

        // Adjust error based on intensity
        val adjustedErrorR = errorR * intensity
        val adjustedErrorG = errorG * intensity

        // Apply corrections by redistributing the error to enhance color differences
        // Emphasize red-green differences by shifting them to blue channel
        val correctedR = r
        val correctedG = g
        val correctedB = b + adjustedErrorR * 0.7 + adjustedErrorG * 0.7

        // Ensure values stay in valid range
        RGB(
          linearToSRGB(math.min(1.0, math.max(0.0, correctedR))),
          linearToSRGB(math.min(1.0, math.max(0.0, correctedG))),
          linearToSRGB(math.min(1.0, math.max(0.0, correctedB)))
        )
      })
    )
  }

  def correctForTritanopia(
      image: Array[Array[RGB]],
      intensity: Double
  ): Array[Array[RGB]] = {
    image.map(row =>
      row.map(pixel => {
        // Convert to linear RGB for better color math
        val r = sRGBToLinear(pixel.r)
        val g = sRGBToLinear(pixel.g)
        val b = sRGBToLinear(pixel.b)

        // First, simulate how tritanopes see this color
        val tr = 0.95 * r + 0.05 * g + 0.0 * b
        val tg = 0.0 * r + 0.433 * g + 0.567 * b
        val tb = 0.0 * r + 0.475 * g + 0.525 * b

        // Calculate the error between original and tritanope vision
        val errorR = r - tr
        val errorG = g - tg
        val errorB = b - tb

        // Adjust error based on intensity
        val adjustedErrorB = errorB * intensity

        // Apply corrections by redistributing the error to enhance color differences
        // For tritanopia, enhance the blue-yellow difference by primarily affecting red and green
        val correctedR = r + adjustedErrorB * 0.7
        val correctedG = g + adjustedErrorB * 0.7
        val correctedB = b

        // Ensure values stay in valid range
        RGB(
          linearToSRGB(math.min(1.0, math.max(0.0, correctedR))),
          linearToSRGB(math.min(1.0, math.max(0.0, correctedG))),
          linearToSRGB(math.min(1.0, math.max(0.0, correctedB)))
        )
      })
    )
  }

  private def adjustChannel(
      value: Int,
      min: Int,
      max: Int,
      factor: Double
  ): Int = {
    // Convert to a value between 0 and 1
    val normalized =
      if (max > min) (value - min).toDouble / (max - min) else 0.5

    // Apply contrast enhancement formula:
    // Bring the value toward the middle for factor < 1 or away from the middle for factor > 1
    val adjusted = 0.5 + factor * (normalized - 0.5)

    // Clamp to 0-1 range
    val clamped = math.min(1.0, math.max(0.0, adjusted))

    // Convert back to 0-255 range and return as an Int
    (clamped * 255).toInt
  }

  def findMinMaxValues(
      image: Array[Array[RGB]]
  ): (Int, Int, Int, Int, Int, Int) = {
    // Flatten the 2D array into a sequence of pixels
    val pixels = image.flatten

    // Use foldLeft to accumulate min/max values
    val initial =
      (255, 0, 255, 0, 255, 0) // (minR, maxR, minG, maxG, minB, maxB)

    pixels.foldLeft(initial) {
      case ((minR, maxR, minG, maxG, minB, maxB), pixel) =>
        (
          math.min(minR, pixel.r),
          math.max(maxR, pixel.r),
          math.min(minG, pixel.g),
          math.max(maxG, pixel.g),
          math.min(minB, pixel.b),
          math.max(maxB, pixel.b)
        )
    }
  }

  private def sRGBToLinear(srgb: Int): Double = {
    val v = srgb / 255.0
    if (v <= 0.04045) v / 12.92
    else math.pow((v + 0.055) / 1.055, 2.4)
  }

  private def linearToSRGB(linear: Double): Int = {
    val v =
      if (linear <= 0.0031308) linear * 12.92
      else 1.055 * math.pow(linear, 1 / 2.4) - 0.055

    math.min(255, math.max(0, (v * 255).toInt))
  }
}

// Usage Example
object Example {
  def main(args: Array[String]): Unit = {
    // Example 2D array of RGB values
    val image = Array(
      Array(RGB(100, 150, 200), RGB(120, 160, 210)),
      Array(RGB(80, 130, 180), RGB(90, 140, 190))
    )

    // Enhance contrast with a factor of 1.5 (increased contrast)
    val enhancedImage = ColorConversions.enhanceContrast(image, 1.5)

    // Print original and enhanced images
    println("Original Image:")
    image.foreach(row => println(row.mkString(", ")))

    println("\nEnhanced Image:")
    enhancedImage.foreach(row => println(row.mkString(", ")))

    // Apply corrective filters for each type of colorblindness
    val deuteranopiaCorrection =
      ColorConversions.correctForDeuteranopia(image, 1.0)
    val protanopiaCorrection = ColorConversions.correctForProtanopia(image, 1.0)
    val tritanopiaCorrection = ColorConversions.correctForTritanopia(image, 1.0)

    println("\nDeuteranopia Correction:")
    deuteranopiaCorrection.foreach(row => println(row.mkString(", ")))

    println("\nProtanopia Correction:")
    protanopiaCorrection.foreach(row => println(row.mkString(", ")))

    println("\nTritanopia Correction:")
    tritanopiaCorrection.foreach(row => println(row.mkString(", ")))

    // You can also adjust the intensity of the correction
    val mildDeuteranopiaCorrection =
      ColorConversions.correctForDeuteranopia(image, 0.5)

    println("\nMild Deuteranopia Correction (50% intensity):")
    mildDeuteranopiaCorrection.foreach(row => println(row.mkString(", ")))
  }
}
