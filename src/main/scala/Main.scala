import asciiart.Algorithms.LuminanceAlgorithm
import asciiart.Models.LuminanceConfig
import asciiart.{Charset, Conversions}
import cats.effect.{ExitCode, IO, IOApp}
import clients.imgur.ImgurClient
import clients.mapillary.MapillaryClient
import clients.mapillary.Models.ApiKey
import common.Models.Coordinates
import customui.CustomTUI
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import runner.Runner

object Main extends IOApp {
  implicit def logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  private def initClients() = {
    for {
      mapillaryClient <- MapillaryClient.make(
        ApiKey.unsafeCreate(
          "key"
        )
      )
      imgurClient <- ImgurClient.make()

    } yield Runner.make(mapillaryClient, imgurClient)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    initClients().use { runner =>
      for {
        imageInfo <- runner
          .getHexStringsFromLocation(
            Coordinates(50.978828194603636, 9.472298538718276)
          )
          .value

        exitCode <- imageInfo match {
          case Right(imageInfo) =>
            val horizontalSampling = 3
            val verticalSampling   = horizontalSampling * 2
            val charset            = Charset.Extended

            val greyscale = Conversions.hexStringsToSampledGreyscaleDecimal(
              horizontalSampling,
              verticalSampling,
              imageInfo.hexImage.hexStrings,
              imageInfo.hexImage.width.value
            )

            val asciiWithColors = LuminanceAlgorithm
              .generate(
                LuminanceConfig(greyscale.grayscaleDecimals, charset)
              )

            CustomTUI.terminalApp(
              asciiWithColors,
              greyscale.colors,
              runner,
              imageInfo
            )

          case Left(error) =>
            logger
              .error(s"Origin image parsing failed with error: $error")
              .as(ExitCode.Error)
        }
      } yield exitCode

    }
  }
}
