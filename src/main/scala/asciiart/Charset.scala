package asciiart

sealed trait Charset {
  def value: String
}

object Charset {
  case object Default extends Charset {
    def value = ".:-=+*#%@"
  }

  case object Extended extends Charset {
    def value =
      " .'`^\\\",:;Il!i~+_-?][}{1)(|\\\\/*tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$"
  }

  case object Braille extends Charset {
    def value =
      "⠁⠉⠋⠛⠟⠿⡿⢿⣻⣽⣾⣷⣟⣯⣿"
  }
}
