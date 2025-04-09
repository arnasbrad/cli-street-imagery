import asciiart.Algorithms.LuminanceAlgorithm
import asciiart.Models.LuminanceConfig
import asciiart.{Charset, Conversions}
import cats.effect.{ExitCode, IO, IOApp}
import clients.imgur.ImgurClient
import clients.mapillary.MapillaryClient
import clients.mapillary.Models.ApiKey
import common.Models.Coordinates
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
    for {
      // Create initial app state
      hexStrings <- initClients().use { runner =>
        runner
          .getHexStringsFromLocation(
            Coordinates(50.978828194603636, 9.472298538718276)
          )
          .value
      }

      exitCode <- hexStrings match {
        case Right(image) =>
          val horizontalSampling = 3
          val verticalSampling   = horizontalSampling * 2
          val charset            = Charset.Extended

          val greyscale = Conversions.hexStringsToSampledGreyscaleDecimal(
            horizontalSampling,
            verticalSampling,
            image.hexStrings,
            image.width.value
          )

          val asciiWithColors = LuminanceAlgorithm
            .generate(
              LuminanceConfig(greyscale.grayscaleDecimals, charset)
            )

          FunctionalTUI.terminalApp(asciiWithColors, greyscale.colors)
        case Left(error) =>
          logger
            .error(s"Origin image parsing failed with error: $error")
            .as(ExitCode.Error)
      }
    } yield exitCode
  }
}
