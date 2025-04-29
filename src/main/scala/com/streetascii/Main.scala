package com.streetascii

import cats.effect.{ExitCode, IO, Resource}
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp
import com.streetascii.asciiart.Conversions
import com.streetascii.asciiart.Models.ImageInfo
import com.streetascii.cli.Cli.{
  addressCommand,
  coordinatesCommand,
  guessingCommand,
  idCommand
}
import com.streetascii.clients.imgur.ImgurClient
import com.streetascii.clients.mapillary.MapillaryClient
import com.streetascii.clients.traveltime.TravelTimeClient
import com.streetascii.customui.CustomTUI
import com.streetascii.guessinggame.CountryModels.Country
import com.streetascii.guessinggame.GuessingLocations
import com.streetascii.runner.RunnerImpl
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource

object Main
    extends CommandIOApp(
      name = "StreetAscii",
      header = "Street imagery in your terminal",
      version = "1.0.0"
    ) {
  implicit def logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  /*
  val appConfig: AppConfig = AppConfig(
    api = ApiConfig(
      mapillaryKey = ApiKey.unsafeCreate(
        "key"
      ),
      Some(ClientId("id")),
      None,
      None
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
   */

  private def initClients(appConfig: AppConfig) = {
    for {
      mapillaryClient <- MapillaryClient.make(appConfig.api.mapillaryKey)
      imgurClientOpt <- appConfig.api.imgurClientId match {
        case Some(clientId) => ImgurClient.make(clientId).map(Some(_))
        case None           => Resource.pure[IO, Option[ImgurClient]](None)
      }
      travelTimeClientOpt <- (
        appConfig.api.traveltimeAppId,
        appConfig.api.traveltimeKey
      ) match {
        case (Some(id), Some(key)) =>
          TravelTimeClient.make(id, key).map(Some(_))
        case _ => Resource.pure[IO, Option[TravelTimeClient]](None)
      }

    } yield RunnerImpl(mapillaryClient, imgurClientOpt, travelTimeClientOpt)
  }

  def runTerminalApp(
      imageInfo: ImageInfo,
      runner: RunnerImpl,
      isGuessingMode: Boolean,
      country: Country,
      appConfig: AppConfig
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
      AppConfig.loadFromPath(args.configPath).flatMap { appConfig =>
        initClients(appConfig).use { runner =>
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
                  Country.Latvia,
                  appConfig
                )
              case Left(error) =>
                IO.println(
                  s"Origin image parsing failed with error: ${error.message}"
                ).as(ExitCode.Error)
            }
          } yield exitCode
        }
      }

    } orElse coordinatesCommand.map { args =>
      AppConfig.loadFromPath(args.configPath).flatMap { appConfig =>
        initClients(appConfig).use { runner =>
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
                  Country.Latvia,
                  appConfig
                )
              case Left(error) =>
                IO.println(
                  s"Origin image parsing failed with error: ${error.message}"
                ).as(ExitCode.Error)
            }
          } yield exitCode
        }
      }
    } orElse addressCommand.map { args =>
      AppConfig.loadFromPath(args.configPath).flatMap { appConfig =>
        initClients(appConfig).use { runner =>
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
                            Country.Latvia,
                            appConfig
                          )
                        case Left(error) =>
                          IO.println(
                            s"Origin image parsing failed with error: ${error.message}"
                          ).as(ExitCode.Error)
                      }
                    } yield exitCode
                  case None =>
                    IO.println(
                      s"Geocoding did not return any results for address: ${args.address}"
                    ).as(ExitCode.Error)
                }
              case Left(error) =>
                IO.println(
                  s"Error during address geocoding: ${error.message}"
                ).as(ExitCode.Error)
            }

          } yield exitCode
        }
      }
    } orElse guessingCommand.map { args =>
      AppConfig.loadFromPath(args.configPath).flatMap { appConfig =>
        initClients(appConfig).use { runner =>
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
                  location.country,
                  appConfig
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
}
