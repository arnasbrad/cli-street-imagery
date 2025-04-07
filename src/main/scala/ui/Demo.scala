package ui

import cats.effect._
import tui._
import tui.crossterm.{Command, CrosstermJni}
import tui.widgets.BlockWidget
import tui.widgets.tabs.TabsWidget
import ui.Models._

object TabsExample extends IOApp.Simple {
  // Main entry point
  def run: IO[Unit] = {
    val initialApp =
      App(titles = Array(ConfigTab.name, StreetViewTab.name, HelpTab.name))
    for {
      // Create initial app state
      appRef <- IO.ref(initialApp)

      // Use Resource to manage terminal lifecycle (similar to withTerminal)
      _ <- terminalResource.use { case (jni, terminal) =>
        runAppLoop(jni, terminal, appRef)
      }
    } yield ()
  }

  // Resource to manage terminal lifecycle (functionally equivalent to withTerminal)
  // Here we recreate the `withTerminal` function in a more cats-effect compatible way
  private val terminalResource: Resource[IO, (CrosstermJni, Terminal)] = {
    Resource.make {
      IO.blocking {
        val jni = new CrosstermJni
        // setup terminal (same as in withTerminal)
        jni.enableRawMode()
        jni.execute(
          new Command.EnterAlternateScreen(),
          new Command.DisableMouseCapture()
        )

        val backend  = new CrosstermBackend(jni)
        val terminal = Terminal.init(backend)

        (jni, terminal)
      }
    } { case (jni, _) =>
      IO.blocking {
        // restore terminal (same as in withTerminal)
        jni.disableRawMode()
        jni.execute(
          new Command.LeaveAlternateScreen(),
          new Command.DisableMouseCapture()
        )
        val backend = new CrosstermBackend(jni)
        backend.showCursor()
      }
    }
  }

  // The app loop as a proper IO operation
  private def runAppLoop(
      jni: CrosstermJni,
      terminal: Terminal,
      appRef: Ref[IO, App]
  ): IO[Unit] = {
    def loop: IO[Unit] = for {
      // Get current app state
      currentApp <- appRef.get

      // Render current state
      _ <- IO.delay(terminal.draw(f => ui(f, currentApp)))

      // Process input
      event <- IO.delay(jni.read())
      continueLoop <- event match {
        case key: tui.crossterm.Event.Key =>
          val action = mapEventToAction(key, currentApp)
          action match {
            case Action.Exit =>
              IO.pure(false) // Exit the loop
            case _ =>
              // Update state based on action
              appRef.update(app => applyAction(app, action)).as(true)
          }
        case _ => IO.pure(true) // Continue the loop
      }

      // Continue the loop if needed
      _ <- if (continueLoop) loop else IO.unit
    } yield ()

    loop
  }

  // Map input events to actions (pure function)
  private def mapEventToAction(
      event: tui.crossterm.Event.Key,
      app: App
  ): Action = {
    app.inputMode match {
      case InputMode.Normal =>
        event.keyEvent.code match {
          case char: tui.crossterm.KeyCode.Char if char.c() == 'q' =>
            Action.Exit
          case char: tui.crossterm.KeyCode.Char
              if char.c() == 'h' && app.currentState == StreetViewTab =>
            Action.ChangeTab(HelpTab)
          case char: tui.crossterm.KeyCode.Char
              if char
                .c() == 'b' && (app.currentState == HelpTab || app.currentState == ConfigTab) =>
            Action.SwapTabs()
          case char: tui.crossterm.KeyCode.Char
              if char.c() == 'c' && app.currentState == StreetViewTab =>
            Action.ChangeTab(ConfigTab)
          case char: tui.crossterm.KeyCode.Char
              if char.c() == 'e' && app.currentState != HelpTab =>
            Action.SetInputMode(InputMode.Editing)
          case _ => Action.NoOp
        }

      case InputMode.Editing =>
        event.keyEvent.code match {
          case _: tui.crossterm.KeyCode.Enter =>
            Action.AddMessage(app.input)
          case char: tui.crossterm.KeyCode.Char =>
            Action.UpdateInput(app.input + char.c())
          case _: tui.crossterm.KeyCode.Backspace if app.input.nonEmpty =>
            Action.UpdateInput(app.input.substring(0, app.input.length - 1))
          case _: tui.crossterm.KeyCode.Esc =>
            Action.SetInputMode(InputMode.Normal)
          case _ => Action.NoOp
        }
    }
  }

  // Apply an action to produce a new app state (pure function)
  private def applyAction(app: App, action: Action): App = {
    action match {
      case Action.ChangeTab(nextTab) =>
        app.copy(pastState = app.currentState, currentState = nextTab)
      case Action.SwapTabs() =>
        app.copy(currentState = app.pastState, pastState = app.currentState)
      case Action.SetInputMode(mode) =>
        app.copy(inputMode = mode)
      case Action.UpdateInput(newInput) =>
        app.copy(input = newInput)
      case Action.AddMessage(message) =>
        app.copy(input = "", messages = app.messages :+ message)
      case Action.NoOp =>
        app
      case Action.Exit =>
        app // Should never happen as Exit is handled separately
    }
  }

  // UI rendering function (same as before)
  private def ui(f: Frame, app: App): Unit = {
    val chunks = Layout(
      direction = Direction.Vertical,
      margin = Margin(5, 5),
      constraints = Array(Constraint.Length(3), Constraint.Min(0))
    ).split(f.size)

    val mainBackground =
      BlockWidget(style = Style(bg = Some(Color.Black), fg = Some(Color.Gray)))
    f.renderWidget(mainBackground, f.size)

    val titles = app.titles
      .map { t =>
        val (first, rest) = t.splitAt(1)
        Spans.from(
          Span.styled(first, Style(fg = Some(Color.White))),
          Span.styled(rest, Style(fg = Some(Color.White)))
        )
      }

    val tabs = TabsWidget(
      titles = titles,
      block = Some(
        BlockWidget(borders = Borders.ALL, title = Some(Spans.nostyle("Tabs")))
      ),
      selected = app.currentState.index,
      style = Style(fg = Some(Color.Gray)),
      highlightStyle =
        Style(addModifier = Modifier.BOLD, bg = Some(Color.LightGreen))
    )
    f.renderWidget(tabs, chunks(0))

    // Render different content based on the selected tab
    app.currentState.render(f = f, area = chunks(1), app = app)
  }
}
