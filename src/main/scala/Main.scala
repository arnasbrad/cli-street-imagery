import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  val run: IO[Unit] = {
    IO.println("Entry point of the app will be here")
  }
}
