package com.streetascii

import cats.effect.{ExitCode, IO}
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp
import com.streetascii.AppConfig.{ApiConfig, ColorConfig, ProcessingConfig}
import com.streetascii.asciiart.Algorithms.LuminanceAlgorithm
import com.streetascii.asciiart.Models.ImageInfo
import com.streetascii.asciiart.{Charset, Conversions}
import com.streetascii.cli.Cli.{guessingCommand, idCommand}
import com.streetascii.clients.imgur.ImgurClient
import com.streetascii.clients.imgur.Models.ClientId
import com.streetascii.clients.mapillary.MapillaryClient
import com.streetascii.clients.mapillary.Models.ApiKey
import com.streetascii.colorfilters.ColorFilter
import com.streetascii.customui.CustomTUI
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
      navigationType = NavigationType.RadiusBased,
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

    } yield RunnerImpl(mapillaryClient, imgurClient)
  }

  def runTerminalApp(
      imageInfo: ImageInfo,
      runner: RunnerImpl,
      isGuessingMode: Boolean
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
      isGuessingMode
    )
  }

  override def main: Opts[IO[ExitCode]] = {
    idCommand.map { args =>
      initClients().use { runner =>
        for {
          imageInfo <- runner
            // for when bbox aint working
            .getHexStringsFromId(args.imageId)
            // .getHexStringsFromId(MapillaryImageId("1688256144933335"))
            /*
            .getHexStringsFromLocation(
              Coordinates(51.501001738896115, -0.12600535355615777)
            )
             */
            .value

          exitCode <- imageInfo match {
            case Right(imageInfo) =>
              runTerminalApp(imageInfo, runner, isGuessingMode = false)
            case Left(error) =>
              IO.println(
                s"Origin image parsing failed with error: ${error.message}"
              ).as(ExitCode.Error)
          }
        } yield exitCode
      }
    } orElse guessingCommand.map { args =>
      IO(ExitCode.Success)
    }
  }
}
