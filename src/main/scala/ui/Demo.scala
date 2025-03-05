package ui

import tui._
import tui.crossterm.CrosstermJni
import tui.widgets.BlockWidget
import tui.widgets.tabs.TabsWidget
import ui.Models._

object TabsExample {
  case class App(
      titles: Array[String],
      var input_mode: InputMode = InputMode.Normal,
      var input: String = "",
      var messages: Array[String] = Array.empty,
      var currentState: Tab = StreetViewTab,
      var pastState: Tab = StreetViewTab
  )

  def main(args: Array[String]): Unit = withTerminal { (jni, terminal) =>
    // create app and run it
    val app =
      App(titles = Array(ConfigTab.name, StreetViewTab.name, HelpTab.name))
    run_app(terminal, app, jni);
  }

  private def run_app(terminal: Terminal, app: App, jni: CrosstermJni): Unit = {
    def changeTab(nextTab: Tab): Unit = {
      app.pastState = app.currentState
      app.currentState = nextTab
    }

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
                    if char.c() == 'h' && app.currentState == StreetViewTab => {
                  changeTab(HelpTab)
                }
                case char: tui.crossterm.KeyCode.Char
                    if char
                      .c() == 'b' && (app.currentState == HelpTab || app.currentState == ConfigTab) => {
                  val temp = app.pastState
                  app.pastState = app.currentState
                  app.currentState = temp
                }
                case char: tui.crossterm.KeyCode.Char
                    if char.c() == 'c' && app.currentState == StreetViewTab => {
                  changeTab(ConfigTab)
                }
                case char: tui.crossterm.KeyCode.Char
                    if char.c() == 'e' && app.currentState != HelpTab =>
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
