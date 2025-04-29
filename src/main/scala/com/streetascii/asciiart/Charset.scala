package com.streetascii.asciiart

import com.streetascii.asciiart.Charset.CustomCharset

sealed trait Charset {
  val value: String
  val withSpaceAtStart: Charset = CustomCharset(s" $value")
}

object Charset {
  case class CustomCharset(value: String) extends Charset

  case object Default extends Charset {
    val value = ".:-=+*#%@"
  }

  case object Blocks extends Charset {
    val value = "░▒▓█"
  }

  case object BlocksExtended extends Charset {
    val value = "·░▒▓▄▌▐▀█"
  }

  case object Extended extends Charset {
    val value =
      ".'`^\\\",:;Il!i~+_-?][}{1)(|\\\\/*tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$"
  }

  case object Braille extends Charset {
    val value =
      "⠁⠉⠋⠛⠟⠿⡿⢿⣻⣽⣾⣷⣟⣯⣿"
  }

  case object Blank extends Charset {
    val value =
      "█"
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
    override val withSpaceAtStart: Charset = CustomCharset(value)
  }
}
