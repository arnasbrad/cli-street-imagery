package com.streetascii.colorfilters

import com.streetascii.asciiart.Models.RGB

/** Contrast Enhancement for 2D RGB Array in Scala
  *
  * This implementation provides methods to boost contrast in an image
  * represented as a 2D array of RGB values.
  */

object ColorConversions {

  /** Enhances the contrast of the entire image
    * @param image
    *   The 2D array of RGB values
    * @param factor
    *   The contrast enhancement factor (1.0 = no change, >1.0 = increased
    *   contrast)
    * @return
    *   A new 2D array with enhanced contrast
    */
  def enhanceContrast(
      image: Array[Array[RGB]],
      factor: Double
  ): Array[Array[RGB]] = {
    // Find the min and max values for each channel across the entire image
    val (minR, maxR, minG, maxG, minB, maxB) = findMinMaxValues(image)

    // Create a new image with enhanced contrast
    image.map(row =>
      row.map(pixel => {
        val newR = adjustChannel(pixel.r, minR, maxR, factor)
        val newG = adjustChannel(pixel.g, minG, maxG, factor)
        val newB = adjustChannel(pixel.b, minB, maxB, factor)
        RGB(newR, newG, newB)
      })
    )
  }

  /** Adjusts a single channel value to enhance contrast
    * @param value
    *   The original channel value
    * @param min
    *   The minimum value for this channel in the entire image
    * @param max
    *   The maximum value for this channel in the entire image
    * @param factor
    *   The contrast enhancement factor
    * @return
    *   The adjusted channel value
    */
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

  /** Finds the minimum and maximum values for each RGB channel in the image
    * @param image
    *   The 2D array of RGB values
    * @return
    *   A tuple of (minR, maxR, minG, maxG, minB, maxB)
    */
  private def findMinMaxValues(
      image: Array[Array[RGB]]
  ): (Int, Int, Int, Int, Int, Int) = {
    // Start with extreme initial values
    var minR = 255
    var maxR = 0
    var minG = 255
    var maxG = 0
    var minB = 255
    var maxB = 0

    // Iterate through all pixels to find min and max values
    for {
      row   <- image
      pixel <- row
    } {
      minR = math.min(minR, pixel.r)
      maxR = math.max(maxR, pixel.r)
      minG = math.min(minG, pixel.g)
      maxG = math.max(maxG, pixel.g)
      minB = math.min(minB, pixel.b)
      maxB = math.max(maxB, pixel.b)
    }

    (minR, maxR, minG, maxG, minB, maxB)
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
  }
}
