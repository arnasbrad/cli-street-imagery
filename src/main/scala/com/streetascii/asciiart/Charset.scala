package com.streetascii.asciiart

sealed trait Charset {
  def value: String
}

object Charset {
  case object Default extends Charset {
    def value = " .:-=+*#%@"
  }

  case object Blocks extends Charset {
    def value = " ░▒▓█"
  }

  case object BlocksExtended extends Charset {
    def value = " ·░▒▓▄▌▐▀█"
  }

  case object Extended extends Charset {
    def value =
      " .'`^\\\",:;Il!i~+_-?][}{1)(|\\\\/*tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$"
  }

  case object Braille extends Charset {
    def value =
      " ⠁⠉⠋⠛⠟⠿⡿⢿⣻⣽⣾⣷⣟⣯⣿"
  }

  case object Blank extends Charset {
    def value =
      "█"
  }

  case object BraillePatterns extends Charset {
    def value: String = {
      // Generate all 256 possible Braille patterns (2^8 combinations)
      (0 until 256)
        .map(pattern => {
          val codePoint = 0x2800 + pattern
          new String(Character.toChars(codePoint))
        })
        .mkString
    }
  }
}
