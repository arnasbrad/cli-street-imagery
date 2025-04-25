package com.streetascii

import cats.effect.{ExitCode, IO}
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp
import com.streetascii.AppConfig.{ApiConfig, ColorConfig, ProcessingConfig}
import com.streetascii.asciiart.Algorithms.LuminanceAlgorithm
import com.streetascii.asciiart.Models.ImageInfo
import com.streetascii.asciiart.{Charset, Conversions}
import com.streetascii.cli.Cli.{
  addressCommand,
  coordinatesCommand,
  guessingCommand,
  idCommand
}
import com.streetascii.clients.imgur.ImgurClient
import com.streetascii.clients.imgur.Models.ClientId
import com.streetascii.clients.mapillary.MapillaryClient
import com.streetascii.clients.mapillary.Models.ApiKey
import com.streetascii.clients.traveltime.Models.AppId
import com.streetascii.clients.traveltime.TravelTimeClient
import com.streetascii.colorfilters.ColorFilter
import com.streetascii.customui.CustomTUI
import com.streetascii.guessinggame.CountryModels.{Country, GuesserLocation}
import com.streetascii.guessinggame.GuessingLocations
import com.streetascii.navigation.Models.NavigationType
import com.streetascii.runner.RunnerImpl
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main
    extends CommandIOApp(
      name = "StreetAscii",
      header = "Street imagery in your terminal",
      version = "1.0.0"
    ) {
  implicit def logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  val appConfig: AppConfig = AppConfig(
    api = ApiConfig(
      mapillaryKey = ApiKey.unsafeCreate(
        "key"
      ),
      ClientId("id")
    ),
    processing = ProcessingConfig(
      algorithm = LuminanceAlgorithm,
      charset = Charset.Braille,
      navigationType = NavigationType.SequenceBased,
      downSamplingRate = 4
    ),
    colors = ColorConfig(
      color = true,
      colorFilter = ColorFilter.NoFilter
    )
  )

  private def initClients() = {
    for {
      mapillaryClient <- MapillaryClient.make(appConfig.api.mapillaryKey)
      imgurClient     <- ImgurClient.make(appConfig.api.imgurClientId)
      // TODO: parse creds from appConfig
      travelTimeClient <- TravelTimeClient.make(
        clients.traveltime.Models.AppId("x"),
        clients.traveltime.Models.ApiKey("x")
      )

    } yield RunnerImpl(mapillaryClient, imgurClient, travelTimeClient)
  }

  def runTerminalApp(
      imageInfo: ImageInfo,
      runner: RunnerImpl,
      isGuessingMode: Boolean,
      country: Country
  ): IO[ExitCode] = {
    val greyscale = Conversions.hexStringsToSampledGreyscaleDecimal(
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

    CustomTUI.terminalApp(
      asciiWithColors,
      greyscale.colors,
      runner,
      imageInfo,
      appConfig,
      isGuessingMode,
      country
    )
  }

  override def main: Opts[IO[ExitCode]] = {
    idCommand.map { args =>
      initClients().use { runner =>
        for {
          imageInfo <- runner
            .getHexStringsFromId(args.imageId)
            .value

          exitCode <- imageInfo match {
            case Right(imageInfo) =>
              runTerminalApp(
                imageInfo,
                runner,
                isGuessingMode = false,
                Country.Latvia
              )
            case Left(error) =>
              IO.println(
                s"Origin image parsing failed with error: ${error.message}"
              ).as(ExitCode.Error)
          }
        } yield exitCode
      }
    } orElse coordinatesCommand.map { args =>
      initClients().use { runner =>
        for {
          imageInfo <- runner
            .getHexStringsFromLocation(args.coordinates)
            .value

          exitCode <- imageInfo match {
            case Right(imageInfo) =>
              runTerminalApp(
                imageInfo,
                runner,
                isGuessingMode = false,
                Country.Latvia
              )
            case Left(error) =>
              IO.println(
                s"Origin image parsing failed with error: ${error.message}"
              ).as(ExitCode.Error)
          }
        } yield exitCode
      }
    } orElse addressCommand.map { args =>
      initClients().use { runner =>
        for {
          respEith <- runner.getCoordinatesFromAddress(args.address).value

          exitCode <- respEith match {
            case Right(coordinatesOpt) =>
              coordinatesOpt match {
                case Some(coordinates) =>
                  for {
                    imageInfo <- runner
                      .getHexStringsFromLocation(coordinates)
                      .value

                    exitCode <- imageInfo match {
                      case Right(imageInfo) =>
                        runTerminalApp(
                          imageInfo,
                          runner,
                          isGuessingMode = false,
                          Country.Latvia
                        )
                      case Left(error) =>
                        IO.println(
                          s"Origin image parsing failed with error: ${error.message}"
                        ).as(ExitCode.Error)
                    }
                  } yield exitCode
                case None =>
                  IO.println(
                    s"Geocoding dit not return any results for address: ${args.address}"
                  ).as(ExitCode.Error)
              }
            case Left(error) =>
              IO.println(
                s"Error during address geocoding: ${error.message}"
              ).as(ExitCode.Error)
          }

        } yield exitCode
      }
    } orElse guessingCommand.map { args =>
      initClients().use { runner =>
        for {
          location <- GuessingLocations.getRandomLocation
          imageInfoEither <- runner
            .getHexStringsFromId(location.id)
            .value

          exitCode <- imageInfoEither match {
            case Right(imageInfo) =>
              runTerminalApp(
                imageInfo,
                runner,
                isGuessingMode = true,
                location.country
              )
            case Left(error) =>
              IO.println(
                s"Origin image parsing failed with error: ${error.message}"
              ).as(ExitCode.Error)
          }
        } yield exitCode
      }
    }
  }
}
