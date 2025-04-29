package com.streetascii.asciiart

sealed trait Charset {
  val value: String
  val textPrintingValue: String

  def getValue(isText: Boolean): String =
    if (isText) textPrintingValue else value
}

object Charset {
  case object Default extends Charset {
    val value             = ".:-=+*#%@"
    val textPrintingValue = s" $value"
  }

  case object Blocks extends Charset {
    val value             = "░▒▓█"
    val textPrintingValue = s" $value"
  }

  case object BlocksExtended extends Charset {
    val value             = "·░▒▓▄▌▐▀█"
    val textPrintingValue = s" $value"
  }

  case object Extended extends Charset {
    val value =
      ".'`^\\\",:;Il!i~+_-?][}{1)(|\\\\/*tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$"
    val textPrintingValue = s" $value"
  }

  case object Braille extends Charset {
    val value =
      "⠁⠉⠋⠛⠟⠿⡿⢿⣻⣽⣾⣷⣟⣯⣿"
    val textPrintingValue = s" $value"
  }

  case object Blank extends Charset {
    val value =
      "█"
    val textPrintingValue = s" $value"
  }

  case object BraillePatterns extends Charset {
    val value: String = {
      // Generate all 256 possible Braille patterns (2^8 combinations)
      (0 until 256)
        .map(pattern => {
          val codePoint = 0x2800 + pattern
          new String(Character.toChars(codePoint))
        })
        .mkString
    }
    val textPrintingValue: String = value
  }
}
