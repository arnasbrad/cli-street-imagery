package com.streetascii.texttoimage

import cats.effect._
import cats.syntax.all._
import java.awt.{Color, Font, Graphics2D, RenderingHints}
import java.awt.image.BufferedImage
import java.io.{ByteArrayOutputStream, OutputStream}
import java.nio.file.{Files, Path, Paths}
import javax.imageio.ImageIO
import java.util.Base64

object TextToImageConverter extends IOApp {

  // Main entry point
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      imageBytes <- createTextImage(Constants.help, 1000, 500)
      _          <- saveImageToFile(imageBytes, "text_image.png")
      _          <- IO.println(s"Image byte array length: ${imageBytes.length}")
      _ <- IO.println(
        s"First 100 bytes: ${imageBytes.take(100).mkString(", ")}"
      )
      base64 <- IO(Base64.getEncoder.encodeToString(imageBytes))
      _      <- IO.println(s"Base64 (first 100 chars): ${base64.take(100)}...")
    } yield ExitCode.Success
  }

  /** Creates an image from text and returns the bytes in IO context
    */
  def createTextImage(
      text: String,
      width: Int,
      height: Int
  ): IO[Array[Byte]] = {
    for {
      // Create the image
      image <- IO(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB))

      // Process and render the image
      bytes <- Resource
        .make(IO(image.createGraphics())) { g2d =>
          IO(g2d.dispose())
        }
        .use { g2d =>
          for {
            // Set up graphics context
            _ <- configureGraphics(g2d)

            // Set background
            _ <- IO {
              g2d.setColor(Color.BLACK)
              g2d.fillRect(0, 0, width, height)
            }

            // Process text to lines
            lines <- IO(text.split("\n"))

            // Calculate font size and render
            optimalFontSize <- calculateOptimalFontSize(
              g2d,
              lines,
              width,
              height
            )

            // Set font and color
            _ <- IO {
              val font = new Font("Courier New", Font.BOLD, optimalFontSize)
              g2d.setFont(font)
              g2d.setColor(Color.WHITE)
            }

            // Draw the text
            _ <- renderText(g2d, lines, width, height)

            // Convert to byte array
            bytes <- imageToBytes(image)
          } yield bytes
        }
    } yield bytes
  }

  /** Configure graphics rendering settings
    */
  private def configureGraphics(g2d: Graphics2D): IO[Unit] = IO {
    g2d.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    )
    g2d.setRenderingHint(
      RenderingHints.KEY_RENDERING,
      RenderingHints.VALUE_RENDER_QUALITY
    )
  }

  /** Calculate optimal font size using a binary search algorithm
    */
  private def calculateOptimalFontSize(
      g2d: Graphics2D,
      lines: Array[String],
      width: Int,
      height: Int
  ): IO[Int] = IO {
    // Padding - keep some margin from the edges
    val horizontalPadding = width * 0.1
    val verticalPadding   = height * 0.1
    val maxLineWidth      = width - (2 * horizontalPadding)
    val maxHeight         = height - (2 * verticalPadding)

    // Binary search approach for finding optimal font size
    def findOptimalSize(minSize: Int, maxSize: Int): Int = {
      if (maxSize - minSize <= 2) minSize
      else {
        val fontSize = (minSize + maxSize) / 2
        val font     = new Font("Courier New", Font.BOLD, fontSize)
        g2d.setFont(font)
        val metrics = g2d.getFontMetrics(font)

        // Check if all lines fit within the width and height
        val longestLineWidth = lines.map(metrics.stringWidth).max
        val totalTextHeight  = metrics.getHeight() * lines.length

        if (longestLineWidth <= maxLineWidth && totalTextHeight <= maxHeight) {
          // Text fits, try a larger size
          findOptimalSize(fontSize, maxSize)
        } else {
          // Text doesn't fit, try a smaller size
          findOptimalSize(minSize, fontSize)
        }
      }
    }

    findOptimalSize(8, 300) // Min and max font sizes
  }

  /** Render text lines onto the graphics context
    */
  private def renderText(
      g2d: Graphics2D,
      lines: Array[String],
      width: Int,
      height: Int
  ): IO[Unit] = IO {
    val metrics    = g2d.getFontMetrics
    val lineHeight = metrics.getHeight()

    // Calculate y-position for first line
    val totalTextHeight = lineHeight * lines.length
    var y               = (height - totalTextHeight) / 2 + metrics.getAscent()

    // Left margin
    val leftMargin = (width * 0.1).toInt

    // Draw each line separately
    lines.foreach { line =>
      g2d.drawString(line, leftMargin, y)
      y += lineHeight
    }
  }

  /** Convert BufferedImage to byte array
    */
  private def imageToBytes(image: BufferedImage): IO[Array[Byte]] = {
    Resource
      .make(IO(new ByteArrayOutputStream())) { baos =>
        IO(baos.close())
      }
      .use { baos =>
        for {
          _     <- IO(ImageIO.write(image, "png", baos))
          _     <- IO(baos.flush())
          bytes <- IO(baos.toByteArray)
        } yield bytes
      }
  }

  /** Save image bytes to a file
    */
  def saveImageToFile(imageBytes: Array[Byte], filename: String): IO[Path] =
    IO(Files.write(Paths.get(filename), imageBytes))
      .flatTap(_ => IO.println(s"Image saved to $filename"))

  /** Creates a zoomed view of text
    */
  def createZoomedTextImage(
      text: String,
      width: Int = 800,
      height: Int = 600,
      zoomFactor: Int = 4
  ): IO[Array[Byte]] = {
    val fontSize = 200

    for {
      // Create the large base image
      largeImage <- IO(
        new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
      )

      // Process with a resource for the graphics context
      bytes <- Resource
        .make(IO(largeImage.createGraphics())) { g2d =>
          IO(g2d.dispose())
        }
        .use { g2d =>
          for {
            // Configure high quality rendering
            _ <- IO {
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

              // Set background
              g2d.setColor(Color.WHITE)
              g2d.fillRect(0, 0, width, height)

              // Draw text very large
              val font = new Font("Courier New", Font.BOLD, fontSize)
              g2d.setFont(font)
              g2d.setColor(Color.BLACK)

              val metrics = g2d.getFontMetrics(font)
              val x       = (width - metrics.stringWidth(text)) / 2
              val y = ((height - metrics.getHeight()) / 2) + metrics.getAscent()

              g2d.drawString(text, x, y)
            }

            // Calculate zoom region
            metrics    <- IO(g2d.getFontMetrics)
            centerX    <- IO((width / 2).toInt)
            centerY    <- IO((height / 2).toInt)
            zoomWidth  <- IO(width / zoomFactor)
            zoomHeight <- IO(height / zoomFactor)

            // Calculate safe zoom coordinates (centered)
            safeZoomX <- IO(
              math
                .max(0, math.min(centerX - (zoomWidth / 2), width - zoomWidth))
            )
            safeZoomY <- IO(
              math.max(
                0,
                math.min(centerY - (zoomHeight / 2), height - zoomHeight)
              )
            )

            // Extract zoomed region
            zoomedImage <- IO(
              largeImage
                .getSubimage(safeZoomX, safeZoomY, zoomWidth, zoomHeight)
            )

            // Convert to bytes
            bytes <- imageToBytes(zoomedImage)
          } yield bytes
        }
    } yield bytes
  }
}
