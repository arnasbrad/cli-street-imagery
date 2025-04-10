import AppConfig.{ApiConfig, ProcessingConfig}
import asciiart.Charset
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.toBifunctorOps
import clients.mapillary.Models.ApiKey
import pureconfig._
import pureconfig.error.CannotConvert
import pureconfig.generic.auto._

case class AppConfig(api: ApiConfig, processing: ProcessingConfig)

object AppConfig {
  // Case classes representing your configuration structure
  case class ApiConfig(mapillaryKey: ApiKey)
  case class ProcessingConfig(
      algorithm: String,
      charset: String,
      downSamplingRate: Int
  )

  def load(configSource: ConfigSource): IO[AppConfig] = {
    IO(configSource.loadOrThrow[AppConfig])
  }

  private implicit val mapillaryApiReader: ConfigReader[ApiKey] =
    ConfigReader.fromString(str =>
      ApiKey
        .create(str)
        .leftMap(err =>
          CannotConvert("String", "Mapillary token", err.toString)
        )
    )
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
