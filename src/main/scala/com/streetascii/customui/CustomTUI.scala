package com.streetascii.customui

import cats.effect.{ExitCode, IO, Resource}
import com.streetascii.AppConfig
import com.streetascii.AppConfig.ColorConfig
import com.streetascii.Main.logger
import com.streetascii.asciiart.Models.{ColoredPixels, ImageInfo, RGB}
import com.streetascii.asciiart.{Algorithms, Conversions}
import com.streetascii.clients.mapillary.Models.{ImageData, MapillaryImageId}
import com.streetascii.common.Models.Radius
import com.streetascii.guessinggame.CountryModels.Country
import com.streetascii.guessinggame.GuessingLocations
import com.streetascii.navigation.Models.NavigationType.{
  RadiusBased,
  SequenceBased,
  SequenceBasedFast
}
import com.streetascii.navigation.Navigation
import com.streetascii.runner.RunnerImpl
import com.streetascii.texttoimage.TextToImageConverter.{
  createColoredAsciiImage,
  saveImageToPng
}
import com.streetascii.texttoimage.{Constants, TextToImageConverter}
import org.jline.terminal.{Terminal, TerminalBuilder}
import org.jline.utils.InfoCmp

import java.io.{BufferedWriter, OutputStreamWriter}
import scala.util.{Failure, Success, Try}

object CustomTUI {
  // ANSI escape character and reset code
  private val ESC: Char     = '\u001B'
  private val RESET: String = s"$ESC[0m"

  private val textAlgorithm = Algorithms.LuminanceAlgorithm

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
  // only works for unix
  private def supportsColors: Boolean =
    Option(System.getenv("TERM")).exists(term =>
      term.contains("color") || term.contains("xterm") || term.contains("256")
    ) ||
      Option(System.getenv("COLORTERM")).isDefined

  /** Safely colorize text with fallbacks */
  private def safeColorize(text: String, r: Int, g: Int, b: Int): String =
    if (text.isEmpty) text
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
  private def terminalResource: Resource[IO, Terminal] =
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
  private def printGrid(
      writer: BufferedWriter,
      chars: Array[Array[Char]],
      colors: Array[Array[RGB]],
      colorConfig: ColorConfig,
      ignoreFilter: Boolean = false
  ): IO[Unit] = {
    IO.blocking {
      val processedColors =
        if (ignoreFilter) colors
        else colorConfig.colorFilter.applyFilter(colors, colorConfig.intensity)

      val sb = new StringBuilder()

      for ((line, lineIndex) <- chars.zipWithIndex) {
        if (lineIndex > 0) {
          sb.append("\n")
        }

        for ((char, charIndex) <- line.zipWithIndex) {
          val rgb = processedColors(lineIndex)(charIndex)
          val processedChar =
            if (colorConfig.color)
              safeColorize(char.toString, rgb.r, rgb.g, rgb.b)
            else char.toString
          sb.append(processedChar)
        }
      }

      writer.write(sb.toString + RESET)
      writer.flush()
    }
  }

  private def getAsciiConversionResult(
      imageInfo: ImageInfo,
      appConfig: AppConfig
  ): (ColoredPixels, Array[Array[Char]]) = {
    val greyscale =
      Conversions
        .hexStringsToSampledGreyscaleDecimal(
          appConfig.processing.downSamplingRate,
          appConfig.processing.verticalSampling,
          imageInfo.hexImage.hexStrings,
          imageInfo.hexImage.width.value
        )

    val asciiWithColors = appConfig.processing.algorithm
      .generate(
        appConfig.processing.charset,
        greyscale.grayscaleDecimals
      )
    (greyscale, asciiWithColors)
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
      runner: RunnerImpl,
      initialImageInfo: ImageInfo,
      appConfig: AppConfig,
      isGuessingMode: Boolean,
      initialCountry: Country
  ): IO[ExitCode] = {
    val mainHelpText =
      if (isGuessingMode) Constants.Help.mainHelpWithGuessing
      else Constants.Help.mainHelp

    withTerminal { (terminal, writer) =>
      def loop(
          chars: Array[Array[Char]],
          colors: Array[Array[RGB]],
          imageInfo: ImageInfo,
          currentCountry: Country
      ): IO[ExitCode] = {
        def radiusNavigationLogic(
            imageInfo: ImageInfo
        ) = {
          def readRadiusNavigationChoice(
              newImages: List[ImageData],
              isHelpMode: Boolean = false
          ): IO[ExitCode] = {
            val newImageIds = newImages.map(_.id)
            for {
              navKey <- readKey(terminal)
              code <- navKey match {
                case k if k >= '1' && k <= ('0' + newImageIds.length) =>
                  val index = k - '1' // Convert ASCII value to 0-based index
                  navigateToLocation(newImageIds(index), currentCountry)
                case 'q' =>
                  exitApp(chars)
                case 'h' =>
                  for {
                    _ <- printAsciiText(chars, Constants.Help.radiusNavHelp)
                    code <- readRadiusNavigationChoice(
                      newImages,
                      isHelpMode = true
                    )
                  } yield code
                case 'r' => reRender
                case 'n' if isHelpMode =>
                  for {
                    _ <- printAsciiText(
                      chars,
                      Constants.radiusNavOptionsList(imageInfo, newImages)
                    )
                    code <- readRadiusNavigationChoice(newImages)
                  } yield code
                case _ =>
                  for {
                    code <- readRadiusNavigationChoice(newImages)
                  } yield code
              }
            } yield code
          }

          for {
            _ <- clearScreen(terminal)

            navOptsEither <-
              Navigation
                .findNearbyImages(
                  currentImageId = imageInfo.imageId,
                  currentCoordinates = imageInfo.coordinates,
                  radius = Radius.unsafeCreate(15),
                  maxAmount = 5
                )(runner.mapillaryClient)
                .value

            // for when bbox aint working
            /*
            navOptsEither: Either[MapillaryError, List[MapillaryImageId]] =
              (List(MapillaryImageId("4169705706383182")))
                .asRight[MapillaryError]
             */

            code <- navOptsEither match {
              case Right(newImages) =>
                for {
                  _ <- printAsciiText(
                    chars,
                    Constants.radiusNavOptionsList(imageInfo, newImages)
                  )
                  code <- readRadiusNavigationChoice(newImages)
                } yield code

              case Left(e) =>
                IO.println(e.message)
                  .as(
                    ExitCode.Error
                  )
            }

          } yield code
        }

        def sequenceNavigationLogic(
            imageInfo: ImageInfo
        ) = {
          def readSequenceNavigationChoice(
              backwardsOpt: Option[MapillaryImageId],
              forwardsOpt: Option[MapillaryImageId],
              isHelpMode: Boolean = false
          ): IO[ExitCode] =
            for {
              navKey <- readKey(terminal)
              code <- navKey match {
                case 'b' =>
                  backwardsOpt match {
                    case Some(backwards) =>
                      navigateToLocation(backwards, currentCountry)
                    case None =>
                      readSequenceNavigationChoice(backwardsOpt, forwardsOpt)
                  }

                case 'f' =>
                  forwardsOpt match {
                    case Some(forwards) =>
                      navigateToLocation(forwards, currentCountry)
                    case None =>
                      readSequenceNavigationChoice(backwardsOpt, forwardsOpt)
                  }

                case 'r' => reRender

                case 'h' =>
                  for {
                    _ <- printAsciiText(chars, Constants.Help.sequenceNavHelp)
                    code <- readSequenceNavigationChoice(
                      backwardsOpt,
                      forwardsOpt,
                      isHelpMode = true
                    )
                  } yield code

                case 'n' if isHelpMode =>
                  for {
                    _ <- printAsciiText(
                      chars,
                      Constants.sequenceNavOptsList(backwardsOpt, forwardsOpt)
                    )
                    code <- readSequenceNavigationChoice(
                      backwardsOpt,
                      forwardsOpt
                    )
                  } yield code

                case 'q' =>
                  exitApp(chars)

                case _ =>
                  for {
                    code <- readSequenceNavigationChoice(
                      backwardsOpt,
                      forwardsOpt
                    )
                  } yield code
              }
            } yield code

          for {
            _ <- clearScreen(terminal)

            navOptsEither <-
              Navigation
                .findSequenceNeighbors(
                  currentImageId = imageInfo.imageId,
                  sequenceId = imageInfo.sequenceId
                )(runner.mapillaryClient)
                .value

            code <- navOptsEither match {
              case Right((backwardsOpt, forwardsOpt)) =>
                for {
                  _ <- printAsciiText(
                    chars,
                    Constants.sequenceNavOptsList(backwardsOpt, forwardsOpt)
                  )

                  code <- readSequenceNavigationChoice(
                    backwardsOpt,
                    forwardsOpt
                  )
                } yield code

              case Left(e) =>
                IO.println(e.message)
                  .as(
                    ExitCode.Error
                  )
            }
          } yield code
        }

        def sequenceFastNavigationLogic(
            imageInfo: ImageInfo
        ) = {
          def readSequenceFastNavigationChoice(
              backwardsOpt: Option[MapillaryImageId],
              forwardsOpt: Option[MapillaryImageId],
              isHelpMode: Boolean = false
          ): IO[ExitCode] =
            for {
              navKey <- readKey(terminal)
              code <- navKey match {
                case 'b' =>
                  backwardsOpt match {
                    case Some(backwards) =>
                      navigateToLocation(backwards, currentCountry)
                    case None =>
                      for {
                        _ <- printAsciiText(chars, "CANNOT GO\nFURTHER BACK")
                        code <- readSequenceFastNavigationChoice(
                          backwardsOpt,
                          forwardsOpt
                        )
                      } yield code
                  }

                case 'f' =>
                  forwardsOpt match {
                    case Some(forwards) =>
                      navigateToLocation(forwards, currentCountry)
                    case None =>
                      for {
                        _ <- printAsciiText(chars, "CANNOT GO\nFURTHER FORWARD")
                        code <- readSequenceFastNavigationChoice(
                          backwardsOpt,
                          forwardsOpt
                        )
                      } yield code
                  }

                case 'h' =>
                  for {
                    _ <- printAsciiText(chars, Constants.Help.sequenceNavHelp)
                    code <- readSequenceFastNavigationChoice(
                      backwardsOpt,
                      forwardsOpt,
                      isHelpMode = true
                    )
                  } yield code

                case 'n' if isHelpMode =>
                  for {
                    _ <- printAsciiText(
                      chars,
                      Constants.sequenceNavOptsList(backwardsOpt, forwardsOpt)
                    )
                    code <- readSequenceFastNavigationChoice(
                      backwardsOpt,
                      forwardsOpt
                    )
                  } yield code

                case 'q' =>
                  exitApp(chars)

                case _ =>
                  for {
                    code <- readSequenceFastNavigationChoice(
                      backwardsOpt,
                      forwardsOpt
                    )
                  } yield code
              }
            } yield code

          for {
            navOptsEither <-
              Navigation
                .findSequenceNeighbors(
                  currentImageId = imageInfo.imageId,
                  sequenceId = imageInfo.sequenceId
                )(runner.mapillaryClient)
                .value

            code <- navOptsEither match {
              case Right((backwardsOpt, forwardsOpt)) =>
                for {
                  code <- readSequenceFastNavigationChoice(
                    backwardsOpt,
                    forwardsOpt
                  )
                } yield code

              case Left(e) =>
                IO.println(e.message)
                  .as(
                    ExitCode.Error
                  )
            }
          } yield code
        }

        def readGuessingOpt(
            formattedString: String,
            correctIndex: Int,
            isHelpMode: Boolean = false
        ): IO[ExitCode] = {
          for {
            navKey <- readKey(terminal)
            code <- navKey match {
              case k if k >= '1' && k <= ('0' + 5) =>
                val index = k - '1' // Convert ASCII value to 0-based index
                for {
                  _ <- clearScreen(terminal)
                  code <-
                    if (index == correctIndex) {
                      def readGoNextKey(): IO[ExitCode] = {
                        for {
                          navKey <- readKey(terminal)
                          code <- navKey match {
                            case '\n' | '\r' =>
                              for {
                                _ <- clearScreen(terminal)
                                location <-
                                  GuessingLocations.getRandomLocation
                                _ <- logger.info(location.toString)
                                imageInfoEither <- runner
                                  .getHexStringsFromId(location.id)
                                  .value
                                code <- imageInfoEither match {
                                  case Right(imageInfo) =>
                                    navigateToLocation(
                                      imageInfo.imageId,
                                      location.country
                                    )
                                  case Left(e) =>
                                    IO.println(e.message)
                                      .as(
                                        ExitCode.Error
                                      )
                                }
                              } yield code
                            case 'q' =>
                              exitApp(chars)
                            case _ => readGoNextKey()
                          }
                        } yield code
                      }
                      for {
                        _ <- printAsciiText(
                          chars,
                          Constants.correctGuess(currentCountry)
                        )
                        code <- readGoNextKey()
                      } yield code

                    } else {
                      printAsciiText(
                        chars,
                        Constants.wrongGuess(currentCountry)
                      )
                        .as(ExitCode.Success)
                    }
                } yield code
              case 'h' =>
                for {
                  _ <- printAsciiText(chars, Constants.Help.guessingHelp)
                  code <- readGuessingOpt(
                    formattedString,
                    correctIndex,
                    isHelpMode = true
                  )
                } yield code
              case 'g' if isHelpMode =>
                for {
                  _    <- printAsciiText(chars, formattedString)
                  code <- readGuessingOpt(formattedString, correctIndex)
                } yield code
              case 'q' =>
                exitApp(chars)
              case _ =>
                readGuessingOpt(formattedString, correctIndex)
            }
          } yield code
        }

        def navigateToLocation(
            imageId: MapillaryImageId,
            nextCountry: Country
        ) = {
          for {
            _ <- logger.info("NAVIGATING")
            res <- runner
              .getHexStringsFromId(imageId)
              .value
            _ <- logger.info(s"got hex ${res.isRight}")
            _ <- clearScreen(terminal)
            code <- res match {
              case Right(imageInfo) =>
                val (greyscale, asciiWithColors) =
                  getAsciiConversionResult(imageInfo, appConfig)
                for {
                  _ <- logger.info(s"got the good stuff ${imageInfo.imageId}")
                  _ <- clearScreen(terminal)
                  _ <- logger.info(s"cleared")
                  _ <- printGrid(
                    writer,
                    asciiWithColors,
                    greyscale.colors,
                    appConfig.colors
                  )
                  _ <- logger.info(s"printed")
                  code <- loop(
                    asciiWithColors,
                    greyscale.colors,
                    imageInfo,
                    nextCountry
                  )
                } yield code
              case Left(e) =>
                IO.println(e.message)
                  .as(
                    ExitCode.Error
                  )
            }
          } yield code
        }

        def reRender =
          for {
            _ <- clearScreen(terminal)
            _ <- printGrid(
              writer,
              chars,
              colors,
              appConfig.colors
            )
            code <- loop(chars, colors, imageInfo, currentCountry)
          } yield code

        for {
          key <- readKey(terminal)
          exitCode <- key match {
            case 'q' =>
              exitApp(chars)

            case 's' =>
              for {
                _          <- clearScreen(terminal)
                imageBytes <- createColoredAsciiImage(chars, colors)
                urlsEith   <- runner.generateSocialMediaLinks(imageBytes).value

                code <- urlsEith match {
                  case Right(urls) =>
                    val urlStrings = urls.map(_.uri.toString())
                    for {
                      _ <- IO.blocking {
                        writer.write(urlStrings.mkString("\n"))
                        writer.flush()
                      }
                      code <- loop(chars, colors, imageInfo, currentCountry)
                    } yield code
                  case Left(e) =>
                    IO.println(e.message)
                      .as(
                        ExitCode.Error
                      )
                }
              } yield code

            case 'r' => reRender

            case 'c' =>
              for {
                _          <- clearScreen(terminal)
                imageBytes <- createColoredAsciiImage(chars, colors)
                fileName   <- saveImageToPng(imageBytes)
                _ <- printAsciiText(
                  chars,
                  s"IMAGE SAVED WITH NAME:\n$fileName"
                )

                code <- loop(chars, colors, imageInfo, currentCountry)
              } yield code

            case 'n' =>
              appConfig.processing.navigationType match {
                case RadiusBased       => radiusNavigationLogic(imageInfo)
                case SequenceBased     => sequenceNavigationLogic(imageInfo)
                case SequenceBasedFast => sequenceFastNavigationLogic(imageInfo)
              }

            case 'g' if isGuessingMode =>
              for {
                otherCountries <- Country.randomPickedCountries(currentCountry)
                tuple <- Constants.guessingOptsList(
                  currentCountry,
                  otherCountries
                )
                (formattedString, correctIndex) = tuple
                _    <- printAsciiText(chars, formattedString)
                code <- readGuessingOpt(formattedString, correctIndex)
              } yield code

            case 'h' =>
              for {
                _    <- printAsciiText(chars, mainHelpText)
                code <- loop(chars, colors, imageInfo, currentCountry)
              } yield code

            case 'x' =>
              for {
                _ <- clearScreen(terminal)
                _ <- IO.blocking {
                  writer.write(mainHelpText)
                  writer.flush()
                }
                code <- loop(chars, colors, imageInfo, currentCountry)
              } yield code

            case _ =>
              loop(
                chars,
                colors,
                imageInfo,
                currentCountry
              ) // Ignore and continue
          }
        } yield exitCode
      }

      def printAsciiText(currChars: Array[Array[Char]], text: String) = {
        for {
          _ <- clearScreen(terminal)

          bytes <- TextToImageConverter.createTextImage(
            text,
            currChars.head.length,
            currChars.length
          )
          helpImage <- Conversions.convertBytesToHexImage(bytes)

          greyscale = Conversions.hexStringsToSampledGreyscaleDecimal(
            1,
            1,
            helpImage.hexStrings,
            helpImage.width.value
          )

          asciiWithColors = textAlgorithm
            .generate(
              appConfig.processing.charset.valueForText,
              greyscale.grayscaleDecimals
            )

          _ <- printGrid(
            writer,
            asciiWithColors,
            greyscale.colors,
            appConfig.colors,
            ignoreFilter = true
          )
        } yield ()
      }

      def exitApp(currChars: Array[Array[Char]]): IO[ExitCode] =
        printAsciiText(currChars, Constants.exiting).as(ExitCode.Success)

      printGrid(
        writer,
        initialChars,
        initialColors,
        appConfig.colors
      ) >> loop(
        initialChars,
        initialColors,
        initialImageInfo,
        initialCountry
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
