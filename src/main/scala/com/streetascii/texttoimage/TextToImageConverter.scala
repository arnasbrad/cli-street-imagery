package com.streetascii.texttoimage

import java.awt.{Color, Font, Graphics2D, RenderingHints}
import java.awt.image.BufferedImage
import java.io.{File, ByteArrayOutputStream}
import javax.imageio.ImageIO
import java.util.Base64

object TextToImageConverter {
  def main(args: Array[String]): Unit = {
    // Example usage
    val text =
      """--------------------------
        |General inputs
        |[q] - quit
        |[n] - navigation
        |--------------------------
        |Sequence navigation inputs
        |[f] - move forwards
        |[b] - move backwards""".stripMargin
    val imageBytes =
      textToImageBytes(text, 1000, 500) // width, height, font size

    // Save to file (for testing)
    saveImageBytesToFile(imageBytes, "text_image.png")

    // Print first 100 bytes (just to show the binary data)
    println(s"Image byte array length: ${imageBytes.length}")
    println(s"First 100 bytes: ${imageBytes.take(100).mkString(", ")}")

    // Optionally convert to Base64 for display in HTML or other contexts
    val base64 = Base64.getEncoder.encodeToString(imageBytes)
    println(s"Base64 (first 100 chars): ${base64.take(100)}...")
  }

  def textToImageBytes(
      text: String,
      width: Int,
      height: Int
  ): Array[Byte] = {
    // Create a buffered image with transparency
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g2d   = image.createGraphics()

    // Set up the graphics context
    g2d.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    )
    g2d.setRenderingHint(
      RenderingHints.KEY_RENDERING,
      RenderingHints.VALUE_RENDER_QUALITY
    )

    // Set background (black)
    g2d.setColor(Color.BLACK)
    g2d.fillRect(0, 0, width, height)

    // Split the text by newline characters
    val lines = text.split("\n")

    // Calculate optimal font size
    val optimalFontSize = calculateOptimalFontSize(g2d, lines, width, height)

    // Set font and color for text with the calculated optimal size
    val font = new Font("Arial", Font.BOLD, optimalFontSize)
    g2d.setFont(font)
    g2d.setColor(Color.WHITE)

    // Get metrics for positioning with the new font
    val metrics    = g2d.getFontMetrics(font)
    val lineHeight = metrics.getHeight()

    // Calculate y-position for first line
    val totalTextHeight = lineHeight * lines.length
    var y               = (height - totalTextHeight) / 2 + metrics.getAscent()

    // Padding - keep some margin from the edges (10% of width/height)
    val horizontalPadding = width * 0.1
    val maxLineWidth      = width - (2 * horizontalPadding)

    // Draw each line separately
    for (line <- lines) {
      // Center each line horizontally
      val lineWidth = metrics.stringWidth(line)
      val x         = (width - lineWidth) / 2

      // Draw the line
      g2d.drawString(line, 50, y)

      // Move to next line position
      y += lineHeight
    }

    g2d.dispose()

    // Convert the image to a byte array
    val baos = new ByteArrayOutputStream()
    ImageIO.write(image, "png", baos)
    baos.flush()
    val imageBytes = baos.toByteArray
    baos.close()

    imageBytes
  }

  def calculateOptimalFontSize(
      g2d: Graphics2D,
      lines: Array[String],
      width: Int,
      height: Int
  ): Int = {
    // Start with a reasonable size
    var fontSize = 48

    // Padding - keep some margin from the edges (10% of width/height)
    val horizontalPadding = width * 0.1
    val verticalPadding   = height * 0.1
    val maxLineWidth      = width - (2 * horizontalPadding)
    val maxHeight         = height - (2 * verticalPadding)

    var fits = false

    // Binary search approach for finding optimal font size
    var minSize = 8   // Minimum readable font size
    var maxSize = 300 // Some large maximum

    while (maxSize - minSize > 2) {
      fontSize = (minSize + maxSize) / 2
      val font = new Font("Arial", Font.BOLD, fontSize)
      g2d.setFont(font)
      val metrics = g2d.getFontMetrics(font)

      // Check if all lines fit within the width
      val longestLineWidth = lines.map(metrics.stringWidth).max
      val totalTextHeight  = metrics.getHeight() * lines.length

      if (longestLineWidth <= maxLineWidth && totalTextHeight <= maxHeight) {
        // Text fits, try a larger size
        minSize = fontSize
      } else {
        // Text doesn't fit, try a smaller size
        maxSize = fontSize
      }
    }

    // Return the largest size that fits
    minSize
  }

  def saveImageBytesToFile(imageBytes: Array[Byte], filename: String): Unit = {
    import java.nio.file.{Files, Paths}
    Files.write(Paths.get(filename), imageBytes)
    println(s"Image saved to $filename")
  }

  def createZoomedTextImage(
      text: String,
      width: Int = 800,
      height: Int = 600,
      fontSize: Int = 200
  ): Array[Byte] = {
    // Create a larger image first
    val largeImage =
      new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g2d = largeImage.createGraphics()

    // Configure high quality rendering
    g2d.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    )
    g2d.setRenderingHint(
      RenderingHints.KEY_RENDERING,
      RenderingHints.VALUE_RENDER_QUALITY
    )
    g2d.setRenderingHint(
      RenderingHints.KEY_FRACTIONALMETRICS,
      RenderingHints.VALUE_FRACTIONALMETRICS_ON
    )

    // Clear with white background
    g2d.setColor(Color.WHITE)
    g2d.fillRect(0, 0, width, height)

    // Draw text very large
    val font = new Font("Arial", Font.BOLD, fontSize)
    g2d.setFont(font)
    g2d.setColor(Color.BLACK)

    val metrics = g2d.getFontMetrics(font)
    val x       = (width - metrics.stringWidth(text)) / 2
    val y       = ((height - metrics.getHeight()) / 2) + metrics.getAscent()

    g2d.drawString(text, x, y)
    g2d.dispose()

    // Now "zoom in" on a small part of the text
    // For example, let's zoom in on the top-left corner of the "H" in "Hello"
    val zoomX      = x // Adjust these coordinates to zoom on different parts
    val zoomY      = y - metrics.getAscent() / 2
    val zoomWidth  = width / 4
    val zoomHeight = height / 4

    // Make sure we stay within bounds
    val safeZoomX = math.max(0, math.min(zoomX, width - zoomWidth))
    val safeZoomY = math.max(0, math.min(zoomY, height - zoomHeight))

    // Extract the subimage (zoomed region)
    val zoomedImage =
      largeImage.getSubimage(safeZoomX, safeZoomY, zoomWidth, zoomHeight)

    // Convert to bytes
    val baos = new ByteArrayOutputStream()
    ImageIO.write(zoomedImage, "png", baos)
    baos.flush()
    val imageBytes = baos.toByteArray
    baos.close()

    imageBytes
  }
}
