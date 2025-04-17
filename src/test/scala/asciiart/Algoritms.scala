package asciiart

import com.streetascii.asciiart.Algorithms.LuminanceAlgorithm
import com.streetascii.asciiart.Charset
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Algoritms extends AnyFlatSpec with Matchers {
  "LuminanceAlgorithm" should "convert grayscale values to ASCII using Default charset" in {
    // Input grayscale values (as strings)
    val input = Array(
      Array("0", "167", "255"), // Black, Mid-gray, White
      Array("50", "150", "240") // Various gray levels
    )

    val result = LuminanceAlgorithm.generate(Charset.Default, input)

    // Expected output based on Default charset: ".:-=+*#%@"
    // For 0 -> '.', for 127 -> '*', for 255 -> '@'
    result(0)(0) should be(' ') // Black maps to first char
    result(0)(1) should be('+') // Mid-gray maps to middle char
    result(0)(2) should be('@') // White maps to last char

    // Additional assertions for the second row
    result(1)(0) should be('.') // 50 maps to a character near the beginning
    result(1)(1) should be('+') // 150 maps to a character past the middle
    result(1)(2) should be('%') // 200 maps to a character near the end
  }

  it should "convert grayscale values to ASCII using Extended charset" in {
    val input = Array(
      Array("0", "127", "255"),
      Array("30", "180", "220")
    )

    val result = LuminanceAlgorithm.generate(Charset.Extended, input)

    val extendedChars = Charset.Extended.value

    // For Extended charset with more granularity
    result(0)(0) should be(
      extendedChars.head
    ) // Black maps to first char (space)
    result(0)(1) should be(
      extendedChars(extendedChars.length / 2 - 1)
    )                                          // Mid-gray maps to middle
    result(0)(2) should be(extendedChars.last) // White maps to last char (@)

    // Check proportional mapping is working
    val expectedIndex30  = ((30.0 * (extendedChars.length - 1)) / 255.0).toInt
    val expectedIndex180 = ((180.0 * (extendedChars.length - 1)) / 255.0).toInt
    val expectedIndex220 = ((220.0 * (extendedChars.length - 1)) / 255.0).toInt

    result(1)(0) should be(extendedChars(expectedIndex30))
    result(1)(1) should be(extendedChars(expectedIndex180))
    result(1)(2) should be(extendedChars(expectedIndex220))
  }

  it should "convert grayscale values to ASCII using Braille charset" in {
    val input = Array(
      Array("0", "127", "255")
    )

    val result = LuminanceAlgorithm.generate(Charset.Braille, input)

    val brailleChars = Charset.Braille.value

    result(0)(0) should be(
      brailleChars.head
    ) // Black maps to first Braille char
    result(0)(1) should be(
      brailleChars(brailleChars.length / 2 - 1)
    )                                         // Mid-gray maps to middle
    result(0)(2) should be(brailleChars.last) // White maps to last Braille char
  }

  it should "handle packed RGB values correctly" in {
    // Packed RGB values (all channels the same for grayscale)
    val input = Array(
      Array(
        "0",
        "8421504",
        "16777215"
      ) // RGB for black (0,0,0), gray (128,128,128), white (255,255,255)
    )

    val result = LuminanceAlgorithm.generate(Charset.Default, input)

    // The algorithm should extract one channel (& 0xff)
    // 0 & 0xff = 0, 8421504 & 0xff = 0, 16777215 & 0xff = 255
    result(0)(0) should be(' ') // Black -> first char
    result(0)(1) should be('=') // Should be 0 after extracting the lowest byte
    result(0)(2) should be('@') // White -> last char
  }

  it should "handle invalid input gracefully" in {
    val input = Array(
      Array("0", "invalid", "255")
    )

    val result = LuminanceAlgorithm.generate(Charset.Default, input)

    // Should use the first character for invalid input
    result(0)(0) should be(' ') // Valid input
    result(0)(1) should be(' ') // Invalid input defaults to first char
    result(0)(2) should be('@') // Valid input
  }

  it should "handle edge cases for index calculation" in {
    // Test with very large numbers that might cause integer overflow
    val input = Array(
      Array("2147483647") // Int.MaxValue
    )

    val result = LuminanceAlgorithm.generate(Charset.Default, input)

    // The algorithm should handle this by extracting the lowest byte (& 0xff)
    // 2147483647 & 0xff = 255
    result(0)(0) should be('@') // Should map to the last char
  }

  it should "handle empty input arrays" in {
    val input = Array.empty[Array[String]]

    val result = LuminanceAlgorithm.generate(Charset.Default, input)

    result.length should be(0)
  }

  it should "handle empty rows" in {
    val input = Array(
      Array.empty[String],
      Array("0", "127", "255")
    )

    val result = LuminanceAlgorithm.generate(Charset.Default, input)

    result.length should be(2)
    result(0).length should be(0)
    result(1).length should be(3)
  }
}
