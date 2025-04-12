package com.streetascii

import cats.effect.{ExitCode, IO, IOApp}
import com.streetascii.AppConfig.{ApiConfig, ProcessingConfig}
import com.streetascii.asciiart.Algorithms.LuminanceAlgorithm
import com.streetascii.asciiart.{Charset, Conversions}
import com.streetascii.clients.imgur.ImgurClient
import com.streetascii.clients.mapillary.MapillaryClient
import com.streetascii.clients.mapillary.Models.{ApiKey, MapillaryImageId}
import com.streetascii.customui.CustomTUI
import com.streetascii.navigation.Models.NavigationType
import com.streetascii.runner.RunnerImpl
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {
  implicit def logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  val appConfig: AppConfig = AppConfig(
    api = ApiConfig(mapillaryKey =
      ApiKey.unsafeCreate(
        "key"
      )
    ),
    processing = ProcessingConfig(
      algorithm = LuminanceAlgorithm,
      charset = Charset.BlocksExtended,
      navigationType = NavigationType.SequenceBased,
      downSamplingRate = 4
    )
  )

  private def initClients() = {
    for {
      mapillaryClient <- MapillaryClient.make(appConfig.api.mapillaryKey)
      imgurClient     <- ImgurClient.make()

    } yield RunnerImpl(mapillaryClient, imgurClient)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    initClients().use { runner =>
      for {
        imageInfo <- runner
          // for when bbox aint working
          .getHexStringsFromId(MapillaryImageId("1159410088278870"))
          /*
          .getHexStringsFromLocation(
            Coordinates(51.501001738896115, -0.12600535355615777)
          )
           */
          .value

        exitCode <- imageInfo match {
          case Right(imageInfo) =>
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
