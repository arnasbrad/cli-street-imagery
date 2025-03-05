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
    val inner = app.index match {
      case 0 => BlockWidget(title = Some(Spans.nostyle("Inner 0")), borders = Borders.ALL)
      case 1 => BlockWidget(title = Some(Spans.nostyle("Inner 1")), borders = Borders.ALL)
      case 2 => BlockWidget(title = Some(Spans.nostyle("Inner 2")), borders = Borders.ALL)
      case 3 => BlockWidget(title = Some(Spans.nostyle("Inner 3")), borders = Borders.ALL)
      case _ => sys.error("unreachable")
    }
    f.renderWidget(inner, chunks(1))
  }
}