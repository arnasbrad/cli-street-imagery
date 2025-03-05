package tuiexamples

import tui._
import tui.crossterm.CrosstermJni
import tui.widgets.tabs.TabsWidget
import tui.widgets.{BlockWidget, ParagraphWidget}

object TabsExample {
  sealed trait InputMode
  object InputMode {
    case object Normal extends InputMode
    case object Editing extends InputMode
  }

  case class App(
      titles: Array[String],
      var index: Int = 1,
      var input_mode: InputMode = InputMode.Normal,
      var input: String = "",
      var messages: Array[String] = Array.empty
  ) {

    def changeTab(nextTab: String): Unit =
      nextTab match {
        case "config"     => index = 0
        case "streetview" => index = 1
        case "help"       => index = 2
      }
  }
  def main(args: Array[String]): Unit = withTerminal { (jni, terminal) =>
    // create app and run it
    val app = App(titles = Array("Config", "StreetView", "Help"))
    run_app(terminal, app, jni);
  }

  def run_app(terminal: Terminal, app: App, jni: CrosstermJni): Unit = {
    var currentState = "streetview"
    var pastState = ""

    while (true) {
      terminal.draw(f => ui(f, app))

      jni.read() match {
        case key: tui.crossterm.Event.Key =>
          app.input_mode match {
            case InputMode.Normal =>
              key.keyEvent.code match {
                // Original tab navigation
                case char: tui.crossterm.KeyCode.Char if char.c() == 'q' =>
                  return
                case char: tui.crossterm.KeyCode.Char
                    if char.c() == 'h' && currentState == "streetview" => {
                  app.changeTab("help")
                  pastState = currentState
                  currentState = "help"
                }
                case char: tui.crossterm.KeyCode.Char
                    if char
                      .c() == 'b' && (currentState == "help" || currentState == "config") => {
                  app.changeTab(pastState)
                  val x = pastState
                  pastState = currentState
                  currentState = x
                }
                case char: tui.crossterm.KeyCode.Char
                    if char.c() == 'c' && currentState == "streetview" => {
                  app.changeTab("config")
                  pastState = currentState
                  currentState = "config"
                }
                case char: tui.crossterm.KeyCode.Char if char.c() == 'e' =>
                  app.input_mode = InputMode.Editing
                case _ => ()
              }

            case InputMode.Editing =>
              key.keyEvent.code match {
                case _: tui.crossterm.KeyCode.Enter =>
                  app.messages = app.messages :+ app.input
                  app.input = ""
                case char: tui.crossterm.KeyCode.Char =>
                  app.input = app.input + char.c()
                case _: tui.crossterm.KeyCode.Backspace if app.input.nonEmpty =>
                  app.input = app.input.substring(0, app.input.length - 1)
                case _: tui.crossterm.KeyCode.Esc =>
                  app.input_mode = InputMode.Normal
                case _ => ()
              }
          }
        case _ => ()
      }
    }
  }

  def ui(f: Frame, app: App): Unit = {
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
      selected = app.index,
      style = Style(fg = Some(Color.Gray)),
      highlightStyle =
        Style(addModifier = Modifier.BOLD, bg = Some(Color.LightGreen))
    )
    f.renderWidget(tabs, chunks(0))

    // Render different content based on the selected tab
    app.index match {
      case 0 => renderConfigTab(f, chunks(1), app)
      case 1 => renderStreetViewTab(f, chunks(1), app)
      case 2 => renderHelpTab(f, chunks(1))
      case _ => sys.error("unreachable")
    }
  }

  // Render the Config tab content
  def renderConfigTab(f: Frame, area: Rect, app: App): Unit = {
    // Create a layout with multiple sections for the config tab
    val verticalChunks = Layout(
      direction = Direction.Vertical,
      constraints = Array(
        Constraint.Percentage(80),
        Constraint.Percentage(5),
        Constraint.Percentage(15)
      )
    ).split(area)

    val horizontalChunks = Layout(
      direction = Direction.Horizontal,
      constraints = Array(Constraint.Percentage(50), Constraint.Percentage(50))
    )
      .split(verticalChunks(0))

    val SurroundingBlock = BlockWidget(
      borders = Borders.ALL,
      title = Some(Spans.nostyle("Configuration")),
      titleAlignment = Alignment.Center,
      borderType = BlockWidget.BorderType.Rounded
    )
    f.renderWidget(SurroundingBlock, f.size)

    // Render different widgets for each section
    val generalConfigBlock = BlockWidget(
      title = Some(Spans.nostyle("General configuration")),
      borders = Borders.ALL,
      style = Style(bg = Some(Color.Black), fg = Some(Color.White))
    )
    f.renderWidget(generalConfigBlock, horizontalChunks(0))

    val imageGenerationSettingsBlock = BlockWidget(
      title = Some(Spans.nostyle("Image generation settings")),
      borders = Borders.ALL,
      style = Style(bg = Some(Color.Black), fg = Some(Color.White))
    )
    f.renderWidget(imageGenerationSettingsBlock, horizontalChunks(1))

    //INPUT FIELD HELP COMMENT
    val (msg, style) = app.input_mode match {
      case InputMode.Normal =>
        (
          Text.from(
            Span.nostyle("Press "),
            Span.styled("q", Style.DEFAULT.addModifier(Modifier.BOLD)),
            Span.nostyle(" to exit, "),
            Span.styled("e", Style.DEFAULT.addModifier(Modifier.BOLD)),
            Span.nostyle(" to start editing.")
          ),
          Style.DEFAULT.addModifier(Modifier.RAPID_BLINK)
        )
      case InputMode.Editing =>
        (
          Text.from(
            Span.nostyle("Press "),
            Span.styled("Esc", Style.DEFAULT.addModifier(Modifier.BOLD)),
            Span.nostyle(" to stop editing, "),
            Span.styled("Enter", Style.DEFAULT.addModifier(Modifier.BOLD)),
            Span.nostyle(" to record the message")
          ),
          Style.DEFAULT
        )
    }
    val text = msg.overwrittenStyle(style)

    val help_message = ParagraphWidget(text = text)
    f.renderWidget(help_message, verticalChunks(1))

    //INPUT FIELD
    val input = ParagraphWidget(
      text = Text.nostyle(app.input),
      block = Some(
        BlockWidget(borders = Borders.ALL, title = Some(Spans.nostyle("Input")))
      ),
      style = app.input_mode match {
        case InputMode.Normal  => Style.DEFAULT
        case InputMode.Editing => Style.DEFAULT.fg(Color.Yellow)
      }
    )
    f.renderWidget(input, verticalChunks(2))

    app.input_mode match {
      case InputMode.Normal =>
        // Hide the cursor. `Frame` does this by default, so we don't need to do anything here
        ()

      case InputMode.Editing =>
        // Make the cursor visible and ask tui-rs to put it at the specified coordinates after rendering
        f.setCursor(
          // Put cursor past the end of the input text
          x = verticalChunks(2).x + Grapheme(app.input).width + 1,
          // Move one line down, from the border to the input line
          y = verticalChunks(2).y + 1
        )
    }
  }

  // Render the StreetView tab content
  def renderStreetViewTab(f: Frame, area: Rect, app: App): Unit = {
    val verticalChunks = Layout(
      direction = Direction.Vertical,
      constraints = Array(
        Constraint.Percentage(75),
        Constraint.Percentage(5),
        Constraint.Percentage(20)
      )
    ).split(area)

    val horizontalChunksMid = Layout(
      direction = Direction.Horizontal,
      constraints = Array(Constraint.Percentage(50), Constraint.Percentage(50))
    )
      .split(verticalChunks(1))

    val horizontalChunksBottom = Layout(
      direction = Direction.Horizontal,
      constraints = Array(Constraint.Percentage(50), Constraint.Percentage(50))
    )
      .split(verticalChunks(2))

    val SurroundingBlock = BlockWidget(
      borders = Borders.ALL,
      title = Some(Spans.nostyle("StreetView")),
      titleAlignment = Alignment.Center,
      borderType = BlockWidget.BorderType.Rounded
    )
    f.renderWidget(SurroundingBlock, f.size)

    val streetViewBlock = BlockWidget(
      title = Some(Spans.nostyle("Street View")),
      borders = Borders.ALL,
      style = Style(bg = Some(Color.Black), fg = Some(Color.White))
    )
    f.renderWidget(streetViewBlock, verticalChunks(0))

    val possibleInputBlock = BlockWidget(
      title = Some(Spans.nostyle("Possible input")),
      borders = Borders.ALL,
      style = Style(bg = Some(Color.Black), fg = Some(Color.Green))
    )
    f.renderWidget(possibleInputBlock, horizontalChunksBottom(0))

    //INPUT COMMENT
    val (msg, style) = app.input_mode match {
      case InputMode.Normal =>
        (
          Text.from(
            Span.nostyle("Press "),
            Span.styled("q", Style.DEFAULT.addModifier(Modifier.BOLD)),
            Span.nostyle(" to exit, "),
            Span.styled("e", Style.DEFAULT.addModifier(Modifier.BOLD)),
            Span.nostyle(" to start editing.")
          ),
          Style.DEFAULT.addModifier(Modifier.RAPID_BLINK)
        )
      case InputMode.Editing =>
        (
          Text.from(
            Span.nostyle("Press "),
            Span.styled("Esc", Style.DEFAULT.addModifier(Modifier.BOLD)),
            Span.nostyle(" to stop editing, "),
            Span.styled("Enter", Style.DEFAULT.addModifier(Modifier.BOLD)),
            Span.nostyle(" to record the message")
          ),
          Style.DEFAULT
        )
    }
    val text = msg.overwrittenStyle(style)

    val help_message = ParagraphWidget(text = text)
    f.renderWidget(help_message, horizontalChunksMid(1))

    //INPUT
    val input = ParagraphWidget(
      text = Text.nostyle(app.input),
      block = Some(
        BlockWidget(
          borders = Borders.ALL,
          title = Some(Spans.nostyle("Coordinates & geolocation"))
        )
      ),
      style = app.input_mode match {
        case InputMode.Normal  => Style.DEFAULT
        case InputMode.Editing => Style.DEFAULT.fg(Color.Yellow)
      }
    )
    f.renderWidget(input, horizontalChunksBottom(1))
  }

  // Render the Help tab content
  def renderHelpTab(f: Frame, area: Rect): Unit = {
    // Split the help area into a header and content
    val verticalChunks = Layout(
      direction = Direction.Vertical,
      constraints = Array(
        Constraint.Percentage(80),
        Constraint.Percentage(20)
      )
    ).split(area)

    val helpBlock = BlockWidget(
      title = Some(Spans.nostyle("Help")),
      borders = Borders.ALL,
      style = Style(bg = Some(Color.Black), fg = Some(Color.White))
    )
    f.renderWidget(helpBlock, verticalChunks(0))

    val controlsBlock = BlockWidget(
      title = Some(Spans.nostyle("Controls")),
      borders = Borders.ALL,
      style = Style(bg = Some(Color.Black), fg = Some(Color.Green))
    )
    f.renderWidget(controlsBlock, verticalChunks(1))
  }
}
