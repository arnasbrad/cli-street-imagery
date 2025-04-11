package com.streetascii

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.toBifunctorOps
import com.streetascii.AppConfig.{ApiConfig, ProcessingConfig}
import com.streetascii.asciiart.Algorithms.{AsciiAlgorithm, BrailleAlgorithm}
import com.streetascii.asciiart.{Algorithms, Charset}
import com.streetascii.clients.mapillary.Models.ApiKey
import com.streetascii.navigation.Models.NavigationType
import pureconfig._
import pureconfig.error.CannotConvert
import pureconfig.generic.auto._

case class AppConfig(api: ApiConfig, processing: ProcessingConfig)

object AppConfig {
  // Case classes representing your configuration structure
  case class ApiConfig(mapillaryKey: ApiKey)
  case class ProcessingConfig(
      algorithm: AsciiAlgorithm,
      charset: Charset,
      // TODO for Ignelis: implement nav type reading
      navigationType: NavigationType,
      downSamplingRate: Int
  ) {
    val verticalSampling: Int = algorithm match {
      case BrailleAlgorithm => downSamplingRate
      case _                => downSamplingRate * 2
    }
  }

  def load(configSource: ConfigSource): IO[AppConfig] = {
    IO(configSource.loadOrThrow[AppConfig])
  }

  private implicit val mapillaryApiReader: ConfigReader[ApiKey] =
    ConfigReader.fromString(str =>
      ApiKey
        .create(str)
        .leftMap(err => CannotConvert(str, "Mapillary token", err.toString))
    )

  private implicit val charsetReader: ConfigReader[Charset] =
    ConfigReader.fromString {
      case "Default"  => Right(Charset.Default)
      case "Extended" => Right(Charset.Extended)
      case "Braille"  => Right(Charset.Braille)
      case other =>
        Left(CannotConvert(other, "Charset", "Charset is unrecognized"))
    }

  private implicit val algortihmReader: ConfigReader[AsciiAlgorithm] =
    ConfigReader.fromString {
      case "Luminance"          => Right(Algorithms.LuminanceAlgorithm)
      case "EdgeDetectionSobel" => Right(Algorithms.EdgeDetectionSobelAlgorithm)
      case "EdgeDetectionCanny" => Right(Algorithms.EdgeDetectionCannyAlgorithm)
      case "Braille"            => Right(Algorithms.BrailleAlgorithm)
      case other =>
        Left(CannotConvert(other, "Algorithm", "Algorithm is unrecognized"))
    }
}

object Test {
  def main(args: Array[String]): Unit = {
    val x = for {
      config <- AppConfig.load(ConfigSource.default)
      _      <- IO.println(config)
    } yield ()
    x.unsafeRunSync()
  }
}
