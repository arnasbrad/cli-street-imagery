package com.streetascii

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.toBifunctorOps
import com.streetascii.AppConfig.{ApiConfig, ColorConfig, ProcessingConfig}
import com.streetascii.asciiart.Algorithms.{AsciiAlgorithm, BrailleAlgorithm}
import com.streetascii.asciiart.{Algorithms, Charset}
import com.streetascii.clients.imgur.Models.ClientId
import com.streetascii.clients.mapillary.Models.ApiKey
import com.streetascii.colorfilters.ColorFilter
import com.streetascii.navigation.Models.NavigationType
import com.streetascii.navigation.Models.NavigationType.RadiusBased
import pureconfig._
import pureconfig.error.CannotConvert
import pureconfig.generic.auto._

case class AppConfig(
    api: ApiConfig,
    processing: ProcessingConfig,
    colors: ColorConfig
)

object AppConfig {
  // Case classes representing your configuration structure
  case class ApiConfig(
      mapillaryKey: ApiKey,
      imgurClientId: Option[ClientId],
      traveltimeAppId: Option[clients.traveltime.Models.AppId],
      traveltimeKey: Option[clients.traveltime.Models.ApiKey]
  )
  case class ProcessingConfig(
      navigationType: NavigationType,
      algorithm: AsciiAlgorithm,
      charset: Charset,
      downSamplingRate: Int
  ) {
    val verticalSampling: Int = algorithm match {
      case BrailleAlgorithm => downSamplingRate
      case _                => downSamplingRate * 2
    }
  }
  case class ColorConfig(
      color: Boolean,
      colorFilter: ColorFilter,
      intensity: Double
  )

  def load(configSource: ConfigSource): IO[AppConfig] = {
    IO(configSource.loadOrThrow[AppConfig])
  }

  def loadFromPath(path: String): IO[AppConfig] = {
    IO(ConfigSource.file(path).loadOrThrow[AppConfig])
  }

  private implicit val navigationTypeReader: ConfigReader[NavigationType] =
    ConfigReader.fromString {
      case "Sequence Navigation"      => Right(NavigationType.SequenceBased)
      case "Sequence Fast Navigation" => Right(NavigationType.SequenceBasedFast)
      case "Proximity Navigation"     => Right(NavigationType.RadiusBased)
      case other =>
        Left(
          CannotConvert(
            other,
            "Navigation type",
            "Navigation type is unrecognized"
          )
        )
    }

  private implicit val mapillaryApiReader: ConfigReader[ApiKey] =
    ConfigReader.fromString(str =>
      ApiKey
        .create(str)
        .leftMap(err => CannotConvert(str, "Mapillary token", err.toString))
    )

  private implicit val imgurApiReader: ConfigReader[Option[ClientId]] =
    ConfigReader.fromString {
      case "Disabled" => Right(None)
      case other =>
        ClientId
          .create(other)
          .leftMap(err => CannotConvert(other, "Imgur client id", err.toString))
          .map(Some(_))
    }

  private implicit val traveltimeAppIdReader
      : ConfigReader[Option[clients.traveltime.Models.AppId]] =
    ConfigReader.fromString {
      case "Disabled" => Right(None)
      case other =>
        clients.traveltime.Models.AppId
          .create(other)
          .leftMap(err =>
            CannotConvert(other, "TravelTime app id", err.toString)
          )
          .map(Some(_))
    }

  private implicit val traveltimeApiKeyReader
      : ConfigReader[Option[clients.traveltime.Models.ApiKey]] =
    ConfigReader.fromString {
      case "Disabled" => Right(None)
      case other =>
        clients.traveltime.Models.ApiKey
          .create(other)
          .leftMap(err =>
            CannotConvert(other, "TravelTime API key", err.toString)
          )
          .map(Some(_))
    }

  private implicit val charsetReader: ConfigReader[Charset] =
    ConfigReader.fromString {
      case "Default"          => Right(Charset.Default)
      case "Extended"         => Right(Charset.Extended)
      case "Braille"          => Right(Charset.Braille)
      case "Blocks"           => Right(Charset.Blocks)
      case "Blocks Extended"  => Right(Charset.BlocksExtended)
      case "Blank"            => Right(Charset.Blank)
      case "Braille Patterns" => Right(Charset.BraillePatterns)
      case other =>
        Left(CannotConvert(other, "Charset", "Charset is unrecognized"))
    }

  private implicit val colorFilterReader: ConfigReader[ColorFilter] =
    ConfigReader.fromString {
      case "Contrast"     => Right(ColorFilter.EnhancedContrast)
      case "Tritanopia"   => Right(ColorFilter.Tritanopia)
      case "Protanopia"   => Right(ColorFilter.Protanopia)
      case "Deuteranopia" => Right(ColorFilter.Deuteranopia)
      case "No Filter"    => Right(ColorFilter.NoFilter)
      case other =>
        Left(
          CannotConvert(
            other,
            "Color filter",
            "Color filter is unrecognized"
          )
        )
    }

  private implicit val algortihmReader: ConfigReader[AsciiAlgorithm] =
    ConfigReader.fromString {
      case "Luminance" => Right(Algorithms.LuminanceAlgorithm)
      case "Edge Detection Sobel" =>
        Right(Algorithms.EdgeDetectionSobelAlgorithm)
      case "Edge Detection Canny" =>
        Right(Algorithms.EdgeDetectionCannyAlgorithm)
      case "Braille"      => Right(Algorithms.BrailleAlgorithm)
      case "No Algorithm" => Right(Algorithms.BlankFilledAlgorithm)
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
