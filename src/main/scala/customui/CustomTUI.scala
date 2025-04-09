package customui

import asciiart.Models.{ImageInfo, RGB}
import cats.effect.{ExitCode, IO, Resource}
import common.Models.Radius
import org.jline.terminal.{Terminal, TerminalBuilder}
import org.jline.utils.InfoCmp
import runner.Runner

import java.io.{BufferedWriter, OutputStreamWriter}
import scala.util.{Failure, Success, Try}

object CustomTUI {
  // ANSI escape character and reset code
  private val ESC: Char     = '\u001B'
  private val RESET: String = s"$ESC[0m"

  // Pure function to clamp values to valid range
  private def clamp(value: Int, min: Int = 0, max: Int = 255): Int =
    math.max(min, math.min(max, value))

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
    val red   = clamp(r)
    val green = clamp(g)
    val blue  = clamp(b)

    s"$ESC[38;2;$red;$green;${blue}m"
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
  def colorize(text: String, r: Int, g: Int, b: Int): String =
    rgb(r, g, b) + text + RESET

  /** Check if terminal supports colors */
  private def supportsColors: Boolean =
    Option(System.getenv("TERM")).exists(term =>
      term.contains("color") || term.contains("xterm") || term.contains("256")
    ) ||
      Option(System.getenv("COLORTERM")).isDefined

  /** Safely colorize text with fallbacks */
  private def safeColorize(text: String, r: Int, g: Int, b: Int): String =
    if (text.isEmpty || !supportsColors) text
    else {
      Try {
        val colorCode = rgb(r, g, b)
        // Ensure we have a complete escape sequence
        if (!colorCode.startsWith(s"$ESC[")) {
          text // Fallback to plain text if escape sequence is malformed
        } else {
          colorCode + text + RESET
        }
      } match {
        case Success(formedStr) => formedStr
        case Failure(_)         => text
      }
    }

  /** Resource for managing a JLine Terminal */
  def terminalResource: Resource[IO, Terminal] =
    Resource.make(
      IO.blocking {
        val terminal = TerminalBuilder
          .builder()
          .system(true)
          .build()

        terminal.enterRawMode()
        terminal.puts(InfoCmp.Capability.clear_screen)
        terminal.flush()
        terminal
      }
    )(terminal => IO.blocking(terminal.close()).handleErrorWith(_ => IO.unit))

  /** Resource for managing a BufferedWriter for the terminal */
  private def writerResource(terminal: Terminal): Resource[IO, BufferedWriter] =
    Resource.make(
      IO.blocking(new BufferedWriter(new OutputStreamWriter(terminal.output())))
    )(writer => IO.blocking(writer.close()).handleErrorWith(_ => IO.unit))

  /** Read a key from the terminal */
  private def readKey(terminal: Terminal): IO[Int] =
    IO.blocking(terminal.reader().read())

  /** Clear the terminal screen */
  private def clearScreen(terminal: Terminal): IO[Unit] =
    IO.blocking {
      terminal.puts(InfoCmp.Capability.clear_screen)
      terminal.flush()
    }

  /** Print a color grid to the terminal in a functional way */
  private def printColorGrid(
      writer: BufferedWriter,
      chars: Array[Array[Char]],
      colors: Array[Array[RGB]]
  ): IO[Unit] = {
    IO.blocking {
      val sb = new StringBuilder()

      for ((line, lineIndex) <- chars.zipWithIndex) {
        if (lineIndex > 0) {
          sb.append("\n")
        }

        for ((char, charIndex) <- line.zipWithIndex) {
          val rgb         = colors(lineIndex)(charIndex)
          val coloredChar = safeColorize(char.toString, rgb.r, rgb.g, rgb.b)
          sb.append(coloredChar)
        }
      }

      writer.write(sb.toString + RESET)
      writer.flush()
    }
  }

  private def printPossibleNavigationOptions(
      runner: Runner,
      imageInfo: ImageInfo,
      writer: BufferedWriter
  ): IO[Unit] = {
    for {
      either <- runner
        .getNeighborImageIds(
          currentImageId = imageInfo.imageId,
          currentCoordinates = imageInfo.coordinates,
          radius = Radius.unsafeCreate(15),
          maxAmount = 5
        )
        .value

      _ <- either match {
        case Right(newImageIds) =>
          IO.blocking {
            writer.write(newImageIds.toString)
            writer.flush()
          }
        case Left(_) =>
          IO.blocking {
            writer.write("something failed")
            writer.flush()
          }
      }
    } yield ()
  }

  /** Run a program with a managed terminal and writer */
  private def withTerminal[A](
      program: (Terminal, BufferedWriter) => IO[A]
  ): IO[A] = {
    val resources: Resource[IO, (Terminal, BufferedWriter)] = for {
      terminal <- terminalResource
      writer   <- writerResource(terminal)
    } yield (terminal, writer)

    resources.use { case (terminal, writer) =>
      program(terminal, writer)
    }
  }

  /** Main application logic with key handling for ASCII art */
  def terminalApp(
      initialChars: Array[Array[Char]],
      initialColors: Array[Array[RGB]],
      runner: Runner,
      initialImageInfo: ImageInfo
  ): IO[ExitCode] = {
    withTerminal { (terminal, writer) =>
      def loop(
          chars: Array[Array[Char]],
          colors: Array[Array[RGB]],
          imageInfo: ImageInfo
      ): IO[ExitCode] = {
        for {
          key <- readKey(terminal)
          exitCode <- key match {
            case 'q' =>
              IO.pure(ExitCode.Success) // Quit

            case 'c' =>
              for {
                _    <- clearScreen(terminal)
                code <- loop(chars, colors, imageInfo)
              } yield code

            case 'r' =>
              for {
                _    <- clearScreen(terminal)
                _    <- printColorGrid(writer, chars, colors)
                code <- loop(chars, colors, imageInfo)
              } yield code

            case 'n' =>
              for {
                _ <- clearScreen(terminal)
                _ <- printPossibleNavigationOptions(runner, imageInfo, writer)
                navKey <- readKey(terminal)
                code <- navKey match {
                  case '1' =>
                    for {
                      _   <- clearScreen(terminal)
                      res <- runner.getHexStringsFromId(imageInfo.imageId).value
                      code <- res match {
                        case Right(imageInfo) =>
                          for {
                            _ <- IO.blocking {
                              writer.write(imageInfo.imageId.id)
                              writer.flush()
                            }
                            /*
                            _ <- printColorGrid(
                              writer,
                              imageInfo.hexImage.,
                              initialColors
                            )
                             */
                            code <- loop(
                              initialChars,
                              initialColors,
                              initialImageInfo
                            )
                          } yield code
                        case Left(err) =>
                          IO.blocking {
                            writer.write("something failed AAA")
                            writer.flush()
                          }.as(ExitCode.Error)
                      }
                    } yield code
                  case _ =>
                    loop(chars, colors, imageInfo)
                }
              } yield code

            case _ =>
              loop(chars, colors, imageInfo) // Ignore and continue
          }
        } yield exitCode
      }

      printColorGrid(writer, initialChars, initialColors) >> loop(
        initialChars,
        initialColors,
        initialImageInfo
      )
    }
  }

  /** Entry point when used as an application */
  /*
  def run(args: List[String]): IO[ExitCode] = {
    // Sample ASCII art data for demonstration
    val sampleChars = Array(
      Array('H', 'e', 'l', 'l', 'o'),
      Array('W', 'o', 'r', 'l', 'd')
    )

    val sampleColors = Array(
      Array(
        RGB(255, 0, 0),
        RGB(255, 165, 0),
        RGB(255, 255, 0),
        RGB(0, 255, 0),
        RGB(0, 0, 255)
      ),
      Array(
        RGB(128, 0, 128),
        RGB(255, 192, 203),
        RGB(255, 255, 255),
        RGB(128, 128, 128),
        RGB(0, 0, 0)
      )
    )

    // Run the app with sample data
    terminalApp(sampleChars, sampleColors, runner, imageInfo)
  }
   */
}
