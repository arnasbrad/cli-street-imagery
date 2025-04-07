import asciiart.Algorithms.LuminanceAlgorithm
import asciiart.Models.{ColoredPixels, LuminanceConfig, RGB}
import asciiart.{Charset, Conversions}
import cats.effect.unsafe.implicits.global
import clients.mapillary.MapillaryClient
import clients.mapillary.Models.ApiKey
import common.Models.Coordinates
import runner.Runner

object CustomTUI {
  // ANSI escape character and reset code
  val ESC   = '\u001B'
  val RESET = s"$ESC[0m"

  // Check if terminal supports colors
  def supportsColors(): Boolean = {
    Option(System.getenv("TERM")).exists(term =>
      term.contains("color") || term.contains("xterm") || term.contains("256")
    ) ||
    Option(System.getenv("COLORTERM")).isDefined
  }

  /** Generates ANSI 24-bit true color escape sequence for the given RGB values
    * @param r
    *   Red component (0-255)
    * @param g
    *   Green component (0-255)
    * @param b
    *   Blue component (0-255)
    * @return
    *   ANSI escape sequence for the RGB color
    */
  def rgb(r: Int, g: Int, b: Int): String = {
    if (!supportsColors()) return ""

    // Clamp values to valid range
    val red   = Math.max(0, Math.min(255, r))
    val green = Math.max(0, Math.min(255, g))
    val blue  = Math.max(0, Math.min(255, b))

    // Use explicit concatenation to avoid string interpolation issues
    ESC.toString + "[38;2;" + red + ";" + green + ";" + blue + "m"
  }

  /** Generates ANSI 24-bit true color background escape sequence
    * @param r
    *   Red component (0-255)
    * @param g
    *   Green component (0-255)
    * @param b
    *   Blue component (0-255)
    * @return
    *   ANSI escape sequence for the RGB background color
    */
  def rgbBackground(r: Int, g: Int, b: Int): String = {
    if (!supportsColors()) return ""

    // Clamp values to valid range
    val red   = Math.max(0, Math.min(255, r))
    val green = Math.max(0, Math.min(255, g))
    val blue  = Math.max(0, Math.min(255, b))

    // Use explicit concatenation to avoid string interpolation issues
    ESC.toString + "[48;2;" + red + ";" + green + ";" + blue + "m"
  }

  /** Colors a string with the given RGB values
    * @param text
    *   Text to color
    * @param r
    *   Red component (0-255)
    * @param g
    *   Green component (0-255)
    * @param b
    *   Blue component (0-255)
    * @return
    *   Colored string that resets after the text
    */
  def colorize(text: String, r: Int, g: Int, b: Int): String = {
    if (text.isEmpty || !supportsColors()) return text
    val colorCode = rgb(r, g, b)
    colorCode + text + RESET
  }

  /** Colors a string's background with the given RGB values
    * @param text
    *   Text to highlight
    * @param r
    *   Red component (0-255)
    * @param g
    *   Green component (0-255)
    * @param b
    *   Blue component (0-255)
    * @return
    *   String with colored background that resets after the text
    */
  def highlight(text: String, r: Int, g: Int, b: Int): String = {
    if (text.isEmpty || !supportsColors()) return text
    val bgCode = rgbBackground(r, g, b)
    bgCode + text + RESET
  }

  /** Safe version of colorize that ensures complete ANSI sequences
    * @param text
    *   Text to color
    * @param r
    *   Red component (0-255)
    * @param g
    *   Green component (0-255)
    * @param b
    *   Blue component (0-255)
    * @return
    *   Colored string that resets after the text
    */
  def safeColorize(text: String, r: Int, g: Int, b: Int): String = {
    if (text.isEmpty || !supportsColors()) return text

    try {
      val colorCode = rgb(r, g, b)
      // Ensure we have a complete escape sequence
      if (!colorCode.startsWith(ESC.toString + "[")) {
        return text // Fallback to plain text if escape sequence is malformed
      }
      colorCode + text + RESET
    } catch {
      case _: Exception => text // Return plain text on any error
    }
  }

  /** Safely print a colored line with proper flushing
    * @param line
    *   The line to print
    */
  def printColoredLine(line: String): Unit = {
    print(line + RESET)
    System.out.flush()
    println()
  }

  def printColorGrid[T](
      chars: Array[Array[Char]],
      colors: Array[Array[RGB]]
  ): Unit = {
    for ((line, lineIndex) <- chars.zipWithIndex) {
      val coloredLine = line.zipWithIndex.map { case (char, charIndex) =>
        val rgb = colors(lineIndex)(charIndex)
        safeColorize(char.toString, rgb.r, rgb.g, rgb.b)
      }.mkString

      // Make sure the line ends with reset and flush the buffer
      print(coloredLine + RESET)
      System.out.flush()
      println()
    }
  }

  def main(args: Array[String]): Unit = {
    // Check if terminal supports colors
    println(s"Terminal supports colors: ${supportsColors()}")

    // Example usage
    println(safeColorize("This is custom red text", 255, 0, 0))
    println(safeColorize("This is orange text", 255, 165, 0))
    println(safeColorize("This is custom blue text", 0, 0, 255))

    // Gradient example
    println("Color gradient:")
    for (i <- 0 to 255 by 25) {
      print(safeColorize("■", i, 255 - i, 128))
    }
    System.out.flush()
    println()

    // Background example
    println(highlight("This has a yellow background", 255, 255, 0))

    // Combining foreground and background
    print(
      rgb(255, 255, 255) + rgbBackground(
        0,
        0,
        128
      ) + "White text on blue background" + RESET
    )
    System.out.flush()
    println()

    // Example of printColorGrid with RGB case class
    val sampleChars = Array(
      Array('H', 'e'),
      Array('i', '!')
    )
    val sampleColors = Array(
      Array(RGB(255, 0, 0), RGB(255, 127, 0)),
      Array(RGB(0, 255, 0), RGB(0, 0, 255))
    )

    printColorGrid(sampleChars, sampleColors)
  }
}

object TestShit {
  private def initClients() = {
    for {
      mapillaryClient <- MapillaryClient.make(
        ApiKey.unsafeCreate(
          "key"
        )
      )
    } yield Runner.make(mapillaryClient, null)
  }

  def main(args: Array[String]): Unit = {
    val x = for {
      // Create initial app state
      hexStrings <- initClients().use { runner =>
        runner
          .getHexStringsFromLocation(
            Coordinates(50.978828194603636, 9.472298538718276)
          )
          .value
      }

      image = hexStrings match {
        case Right(img) => img
        case Left(e)    => throw new Exception("as gejus")
      }

      horizontalSampling = 3
      verticalSampling   = horizontalSampling * 2
      charset            = Charset.Extended

      greyscale = Conversions.hexStringsToSampledGreyscaleDecimal(
        horizontalSampling,
        verticalSampling,
        image.hexStrings,
        image.width.value
      )

      asciiWithColors = LuminanceAlgorithm
        .generate(
          LuminanceConfig(greyscale.grayscaleDecimals, charset)
        )

      _ = {
        // Use CustomTUI.printColorGrid to print the ASCII art with colors
        CustomTUI.printColorGrid(asciiWithColors, greyscale.colors)

        // Optional: Print summary info
        println(s"ASCII art rendered: ${asciiWithColors.length} lines")
      }
    } yield ()

    x.unsafeRunSync()
  }
}
