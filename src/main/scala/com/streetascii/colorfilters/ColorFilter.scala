package com.streetascii.colorfilters

import com.streetascii.asciiart.Models.RGB

trait ColorFilter {
  def applyFilter(
      image: Array[Array[RGB]],
      intensity: Double
  ): Array[Array[RGB]]
}

object ColorFilter {
  case object EnhancedContrast extends ColorFilter {
    def applyFilter(
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
  }

  case object Deuteranopia extends ColorFilter {
    def applyFilter(
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

          // Calculate red-green difference
          val redGreenDiff = r - g

          // Calculate compensation strength - stronger where red and green are similar
          val compensationStrength =
            math.min(1.0, 1.0 - math.abs(redGreenDiff) * 3) * intensity

          // Enhanced compensation for specific problem areas
          val correctedR =
            r + (0.3 * compensationStrength * (if (r > g) 1.0 else -0.5))
          val correctedG =
            g - (0.3 * compensationStrength * (if (g > r) 1.0 else -0.5))

          // Use blue channel for additional signaling (deuteranopes can see blue well)
          val correctedB = if (math.abs(redGreenDiff) < 0.2) {
            // When red and green are very similar, use blue to create a distinctive signal
            if (r > g) {
              b - (0.4 * compensationStrength) // Decrease blue for reddish colors
            } else {
              b + (0.4 * compensationStrength) // Increase blue for greenish colors
            }
          } else {
            b
          }

          // Enhanced contrast in areas of similar luminance
          val luminance           = 0.2126 * r + 0.7152 * g + 0.0722 * b
          val redLuminanceRatio   = r / (luminance + 0.01)
          val greenLuminanceRatio = g / (luminance + 0.01)

          // If red and green contribute similarly to luminance, enhance differences
          val finalR =
            if (math.abs(redLuminanceRatio - greenLuminanceRatio) < 0.3) {
              correctedR * (1.0 + 0.2 * compensationStrength)
            } else {
              correctedR
            }

          val finalG =
            if (math.abs(redLuminanceRatio - greenLuminanceRatio) < 0.3) {
              correctedG * (1.0 - 0.1 * compensationStrength)
            } else {
              correctedG
            }

          // Ensure values stay in valid range
          RGB(
            linearToSRGB(math.min(1.0, math.max(0.0, finalR))),
            linearToSRGB(math.min(1.0, math.max(0.0, finalG))),
            linearToSRGB(math.min(1.0, math.max(0.0, correctedB)))
          )
        })
      )
    }
  }

  case object Protanopia extends ColorFilter {
    def applyFilter(
        image: Array[Array[RGB]],
        intensity: Double
    ): Array[Array[RGB]] = {
      image.map(row =>
        row.map(pixel => {
          // Convert to linear RGB for better color math
          val r = sRGBToLinear(pixel.r)
          val g = sRGBToLinear(pixel.g)
          val b = sRGBToLinear(pixel.b)

          // Simulate protanopia vision
          val pr = 0.567 * r + 0.433 * g + 0.0 * b
          val pg = 0.558 * r + 0.442 * g + 0.0 * b
          val pb = 0.0 * r + 0.242 * g + 0.758 * b

          // Calculate the error between original and protanope vision
          val errorR = r - pr
          val errorG = g - pg
          val errorB = b - pb

          // Shift red-green difference into channels they can perceive better
          val correctedR =
            r + errorG * 0.7 * intensity // Enhance red with green info
          val correctedG =
            g - errorR * 0.7 * intensity // Differentiate green from red
          val correctedB =
            b + (errorR - errorG) * intensity // Use blue channel for additional contrast

          // Enhance overall contrast in problem areas
          val redGreenDiff = Math.abs(r - g)
          val contrastBoost = if (redGreenDiff < 0.2) {
            // If red and green are similar (trouble area for protanopes)
            // Increase their difference by shifting toward blue or yellow
            if (r > g) {
              // Push toward magenta (more blue)
              correctedB * (1.0 + 0.3 * intensity)
            } else {
              // Push toward yellow (less blue)
              correctedB * (1.0 - 0.3 * intensity)
            }
          } else {
            correctedB
          }

          // Ensure values stay in valid range
          RGB(
            linearToSRGB(math.min(1.0, math.max(0.0, correctedR))),
            linearToSRGB(math.min(1.0, math.max(0.0, correctedG))),
            linearToSRGB(
              math.min(1.0, math.max(0.0, math.min(contrastBoost, 1.0)))
            )
          )
        })
      )
    }
  }

  case object Tritanopia extends ColorFilter {
    def applyFilter(
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

          // For tritanopia (blue-yellow confusion), we need to enhance those differences

          // Calculate blue-yellow axis (blue vs red+green)
          val blueYellowDiff = b - ((r + g) / 2)

          // Apply stronger compensation where blue and yellow are similar
          val compensationStrength =
            math.min(1.0, 1.0 - math.abs(blueYellowDiff) * 3) * intensity

          // Shift colors to enhance blue-yellow differences
          val correctedR = r
          // Decrease green when blue is present to create more contrast
          val correctedG = g - (b * 0.4 * compensationStrength)
          // Enhance blue channel to make blues more distinct
          val correctedB = if (b > 0.3) {
            b + (0.6 * compensationStrength)
          } else {
            b - (0.3 * compensationStrength) // Decrease blues when they're already low
          }

          // Additional contrast for problematic blue-green distinctions
          val blueGreenRatio = if (g > 0.1) b / g else 10.0
          if (blueGreenRatio > 0.8 && blueGreenRatio < 1.2) {
            // If blue and green are similar (difficult for tritanopes)
            if (b > g) {
              // Make blue more intense
              correctedB + (0.3 * compensationStrength)
            } else {
              // Make green more vibrant and reduce blue
              correctedG + (0.3 * compensationStrength)
            }
          }

          // Ensure values stay in valid range
          RGB(
            linearToSRGB(math.min(1.0, math.max(0.0, correctedR))),
            linearToSRGB(math.min(1.0, math.max(0.0, correctedG))),
            linearToSRGB(math.min(1.0, math.max(0.0, correctedB)))
          )
        })
      )
    }
  }

  case object NoFilter extends ColorFilter {
    def applyFilter(
        image: Array[Array[RGB]],
        intensity: Double
    ): Array[Array[RGB]] = {
      image
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
    val enhancedImage = ColorFilter.EnhancedContrast.applyFilter(image, 1.5)

    // Print original and enhanced images
    println("Original Image:")
    image.foreach(row => println(row.mkString(", ")))

    println("\nEnhanced Image:")
    enhancedImage.foreach(row => println(row.mkString(", ")))

    // Apply corrective filters for each type of colorblindness
    val deuteranopiaCorrection =
      ColorFilter.Deuteranopia.applyFilter(image, 1.0)
    val protanopiaCorrection = ColorFilter.Protanopia.applyFilter(image, 1.0)
    val tritanopiaCorrection = ColorFilter.Tritanopia.applyFilter(image, 1.0)

    println("\nDeuteranopia Correction:")
    deuteranopiaCorrection.foreach(row => println(row.mkString(", ")))

    println("\nProtanopia Correction:")
    protanopiaCorrection.foreach(row => println(row.mkString(", ")))

    println("\nTritanopia Correction:")
    tritanopiaCorrection.foreach(row => println(row.mkString(", ")))

    // You can also adjust the intensity of the correction
    val mildDeuteranopiaCorrection =
      ColorFilter.Deuteranopia.applyFilter(image, 0.5)

    println("\nMild Deuteranopia Correction (50% intensity):")
    mildDeuteranopiaCorrection.foreach(row => println(row.mkString(", ")))
  }
}
