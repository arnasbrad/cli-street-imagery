package com.streetascii.texttoimage

import cats.effect._
import com.streetascii.asciiart.Models.RGB

import java.awt.image.BufferedImage
import java.awt.{Color, Font, Graphics2D, GraphicsEnvironment, RenderingHints}
import java.io.{ByteArrayOutputStream, FileOutputStream}
import java.nio.file.{Files, Path, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import javax.imageio.ImageIO
import scala.annotation.tailrec

object TextToImageConverter extends IOApp {
  private val customFont: Font = {
    val stream = getClass.getResourceAsStream("/tto.ttf")
    try {
      Font.createFont(Font.TRUETYPE_FONT, stream)
    } finally {
      stream.close()
    }
  }
  GraphicsEnvironment.getLocalGraphicsEnvironment.registerFont(customFont)

  private val imageFont: Font = new Font("Courier new", Font.PLAIN, 12)

  // Main entry point
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      imageBytes <- createTextImage(Constants.Help.mainHelp, 265, 72)
      _          <- saveImageToFile(imageBytes, "text_image.png")
      _          <- IO.println(s"Image byte array length: ${imageBytes.length}")
      _ <- IO.println(
        s"First 100 bytes: ${imageBytes.take(100).mkString(", ")}"
      )
      base64 <- IO(Base64.getEncoder.encodeToString(imageBytes))
      _      <- IO.println(s"Base64 (first 100 chars): ${base64.take(100)}...")
    } yield ExitCode.Success
  }

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
            _ <-
              IO {
                val font = customFont.deriveFont(optimalFontSize.toFloat)
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

  def saveImageToPng(imageBytes: Array[Byte]): IO[String] = {
    for {
      timestamp <- IO {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        LocalDateTime.now.format(formatter)
      }

      filename = s"image_$timestamp.png"

      outputPath = Paths.get(".").resolve(filename).toAbsolutePath.normalize()

      _ <- Resource
        .make {
          IO(new FileOutputStream(outputPath.toFile))
        } { fos =>
          IO(fos.close()).handleErrorWith(_ => IO.unit)
        }
        .use { fos =>
          IO {
            fos.write(imageBytes)
            fos.flush()
          }.as(())
        }
    } yield filename
  }

  def createColoredAsciiImage(
      chars: Array[Array[Char]],
      colors: Array[Array[RGB]],
      fontSizePx: Int = 16
  ): IO[Array[Byte]] = {
    for {
      // Calculate dimensions based on grid size and font
      metrics <- IO {
        val tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val g2d       = tempImage.createGraphics()
        try {
          val font = imageFont.deriveFont(fontSizePx.toFloat)
          g2d.setFont(font)
          g2d.getFontMetrics
        } finally {
          g2d.dispose()
        }
      }

      // Use a narrower character for width calculation ('i' is quite narrow)
      charWidth = metrics.charWidth('n')

      // Calculate image dimensions with no extra padding
      width  = chars.headOption.map(_.length).getOrElse(0) * charWidth
      height = chars.length * metrics.getHeight

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

            // Set background to black (or any background color you prefer)
            _ <- IO {
              g2d.setColor(Color.BLACK)
              g2d.fillRect(0, 0, width, height)
            }

            // Set font
            _ <- IO {
              val font = imageFont.deriveFont(fontSizePx.toFloat)
              g2d.setFont(font)
            }

            // Draw each character with its color
            _ <- IO {
              val lineHeight = metrics.getHeight

              for (y <- chars.indices; x <- chars(y).indices) {
                val char = chars(y)(x)
                val rgb  = colors(y)(x)

                g2d.setColor(new Color(rgb.r, rgb.g, rgb.b))
                g2d.drawString(
                  char.toString,
                  x * charWidth, // No padding between characters horizontally
                  y * lineHeight + metrics.getAscent // No padding between lines
                )
              }
            }

            // Convert to byte array
            bytes <- imageToBytes(image)
          } yield bytes
        }
    } yield bytes
  }

  private def configureGraphics(g2d: Graphics2D): IO[Unit] = IO {
    // For pixel fonts, typically you want to disable anti-aliasing
    g2d.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
    )
    g2d.setRenderingHint(
      RenderingHints.KEY_RENDERING,
      RenderingHints.VALUE_RENDER_QUALITY
    )
    g2d.setRenderingHint(
      RenderingHints.KEY_FRACTIONALMETRICS,
      RenderingHints.VALUE_FRACTIONALMETRICS_OFF
    )
  }

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
    @tailrec
    def findOptimalSize(minSize: Int, maxSize: Int): (Int, Font) = {
      if (maxSize - minSize <= 2)
        (minSize, customFont.deriveFont(minSize.toFloat))
      else {
        val fontSize = (minSize + maxSize) / 2
        val font     = customFont.deriveFont(fontSize.toFloat)
        g2d.setFont(font)
        val metrics = g2d.getFontMetrics(font)

        // Check if all lines fit within the width and height
        val longestLineWidth = lines.map(metrics.stringWidth).max
        val totalTextHeight  = metrics.getHeight * lines.length

        if (longestLineWidth <= maxLineWidth && totalTextHeight <= maxHeight) {
          // Text fits, try a larger size
          findOptimalSize(fontSize, maxSize)
        } else {
          // Text doesn't fit, try a smaller size
          findOptimalSize(minSize, fontSize)
        }
      }
    }

    // Get the optimal size and font
    val (optimalSize, _) = findOptimalSize(8, 300) // Min and max font sizes
    optimalSize
  }

  private def renderText(
      g2d: Graphics2D,
      lines: Array[String],
      width: Int,
      height: Int
  ): IO[Unit] = IO {
    val metrics    = g2d.getFontMetrics
    val lineHeight = metrics.getHeight

    // Calculate y-position for first line
    val totalTextHeight = lineHeight * lines.length
    var y               = (height - totalTextHeight) / 2 + metrics.getAscent

    // Left margin
    val leftMargin = (width * 0.1).toInt

    // Draw each line separately
    lines.foreach { line =>
      g2d.drawString(line, leftMargin, y)
      y += lineHeight
    }
  }

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

  private def saveImageToFile(
      imageBytes: Array[Byte],
      filename: String
  ): IO[Path] =
    IO(Files.write(Paths.get(filename), imageBytes))
      .flatTap(_ => IO.println(s"Image saved to $filename"))
}
