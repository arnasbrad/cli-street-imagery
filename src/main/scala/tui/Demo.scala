package tuiexamples

import tui._
import tui.crossterm.CrosstermJni
import tui.widgets.tabs.TabsWidget
import tui.widgets.BlockWidget

object TabsExample {
  case class App(
                  titles: Array[String],
                  var index: Int = 1
                ) {

    def changeTab(nextTab : String) : Unit =
      nextTab match {
        case "config" => index = 0
        case "streetview" => index = 1
        case "help" => index = 2
      }
  }
  def main(args: Array[String]): Unit = withTerminal { (jni, terminal) =>

    // create app and run it
    val app = App(titles = Array("Config", "StreetView", "Help"))
    run_app(terminal, app, jni);
  }

  def run_app(terminal: Terminal, app: App, jni: CrosstermJni): Unit = {
    var curentState = "streetview"
    var pastState = ""
    while (true) {
      terminal.draw(f => ui(f, app))

      jni.read() match {
        case key: tui.crossterm.Event.Key =>
          key.keyEvent.code match {
            case char: tui.crossterm.KeyCode.Char if char.c() == 'q' => return
            case char: tui.crossterm.KeyCode.Char if char.c() == 'h' && curentState == "streetview"=> {
              app.changeTab("help")
              pastState = curentState
              curentState = "help"
            }
            case char: tui.crossterm.KeyCode.Char if char.c() == 'b' && curentState == "help" || curentState == "config" => {
              app.changeTab(pastState)
              val x = pastState
              pastState = curentState
              curentState = x
            }
            case char: tui.crossterm.KeyCode.Char if char.c() == 'c' && curentState == "streetview"=> {
              app.changeTab("config")
              pastState = curentState
              curentState = "config"
            }
            case _                                                   => ()
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

    val block = BlockWidget(style = Style(bg = Some(Color.White), fg = Some(Color.Black)))
    f.renderWidget(block, f.size)
    val titles = app.titles
      .map { t =>
        val (first, rest) = t.splitAt(1)
        Spans.from(
          Span.styled(first, Style(fg = Some(Color.Yellow))),
          Span.styled(rest, Style(fg = Some(Color.Green)))
        )
      }

    val tabs = TabsWidget(
      titles = titles,
      block = Some(BlockWidget(borders = Borders.ALL, title = Some(Spans.nostyle("Tabs")))),
      selected = app.index,
      style = Style(fg = Some(Color.Cyan)),
      highlightStyle = Style(addModifier = Modifier.BOLD, bg = Some(Color.Black))
    )
    f.renderWidget(tabs, chunks(0))

    // Render different content based on the selected tab
    app.index match {
      case 0 => renderConfigTab(f, chunks(1))
      case 1 => renderStreetViewTab(f, chunks(1))
      case 2 => renderHelpTab(f, chunks(1))
      case _ => sys.error("unreachable")
    }
  }

  // Render the Config tab content
  def renderConfigTab(f: Frame, area: Rect): Unit = {
    // Create a layout with multiple sections for the config tab
    val verticalChunks = Layout(
      direction = Direction.Vertical,
      constraints = Array(
        Constraint.Percentage(80),
        Constraint.Percentage(20)
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

    val commandLineBlock = BlockWidget(
      title = Some(Spans.nostyle("Command line")),
      borders = Borders.ALL,
      style = Style(bg = Some(Color.Black), fg = Some(Color.Green))
    )
    f.renderWidget(commandLineBlock, verticalChunks(1))
  }

  // Render the StreetView tab content
  def renderStreetViewTab(f: Frame, area: Rect): Unit = {
    val verticalChunks = Layout(
      direction = Direction.Vertical,
      constraints = Array(
        Constraint.Percentage(80),
        Constraint.Percentage(20)
      )
    ).split(area)

    val horizontalChunks = Layout(
      direction = Direction.Horizontal,
      constraints = Array(Constraint.Percentage(50), Constraint.Percentage(50))
    )
      .split(verticalChunks(1))

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
    f.renderWidget(possibleInputBlock, horizontalChunks(0))

    val coordinatesBlock = BlockWidget(
      title = Some(Spans.nostyle("Coordinates & geolocation")),
      borders = Borders.ALL,
      style = Style(bg = Some(Color.Black), fg = Some(Color.Green))
    )
    f.renderWidget(coordinatesBlock, horizontalChunks(1))
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