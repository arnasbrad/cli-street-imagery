package ui

import tui._
import tui.widgets.{BlockWidget, ParagraphWidget}
import ui.TabsExample.App

object Models {
  sealed trait InputMode
  object InputMode {
    case object Normal extends InputMode
    case object Editing extends InputMode
  }

  sealed trait Tab {
    def name: String
    def index: Int
    def render(f: Frame, area: Rect, app: App): Unit
  }
  case object ConfigTab extends Tab {
    def name = "Config"
    def index = 0
    // Render the Config tab content
    def render(f: Frame, area: Rect, app: App): Unit =
      renderConfigTab(f, area, app)
  }
  case object StreetViewTab extends Tab {
    def name = "StreetView"
    def index = 1
    def render(f: Frame, area: Rect, app: App): Unit =
      renderStreetViewTab(f, area, app)
  }
  case object HelpTab extends Tab {
    def name = "Help"
    def index = 2
    def render(f: Frame, area: Rect, app: App): Unit =
      renderHelpTab(f, area, app)
  }

  private def renderConfigTab(f: Frame, area: Rect, app: App): Unit = {
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
        BlockWidget(
          borders = Borders.ALL,
          title = Some(Spans.nostyle("Input"))
        )
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

  private def renderStreetViewTab(f: Frame, area: Rect, app: App): Unit = {
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

  private def renderHelpTab(f: Frame, area: Rect, app: App): Unit = {
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
