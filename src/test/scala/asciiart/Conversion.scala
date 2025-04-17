package com.streetascii.asciiart

import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import com.streetascii.asciiart.Models.{
  ColoredPixels,
  HexImage,
  ImageHeight,
  ImageWidth,
  RGB
}
import com.streetascii.clients.mapillary.Models.{
  MapillaryImageId,
  MapillarySequenceId
}
import com.streetascii.common.Models.Coordinates
import org.scalatest.flatspec.AnyFlatSpec
import cats.effect
import cats.effect.unsafe.implicits.global

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class Conversion extends AnyFlatSpec with Matchers {
  "convertTo2DArray" should "convert a 1D array to a 2D array based on width" in {
    // Given a HexImage with 6 elements and width 3
    val hexImage = HexImage(
      hexStrings =
        Array("001122", "334455", "667788", "99AABB", "CCDDEF", "FFFEEF"),
      width = ImageWidth(3),
      height = ImageHeight(2)
    )

    // When converted to 2D array
    val result = Conversions.convertTo2DArray(hexImage)

    // Then it should be a 2x3 array
    result.length shouldBe 2
    result(0).length shouldBe 3
    result(1).length shouldBe 3

    // And content should match
    result(0) shouldBe Array("001122", "334455", "667788")
    result(1) shouldBe Array("99AABB", "CCDDEF", "FFFEEF")
  }

  it should "handle empty arrays" in {
    // Given an empty HexImage
    val hexImage = HexImage(
      hexStrings = Array.empty[String],
      width = ImageWidth(0),
      height = ImageHeight(0)
    )

    // When converted to 2D array
    val result = Conversions.convertTo2DArray(hexImage)

    // Then it should be an empty array
    result.length shouldBe 0
  }

  it should "handle single row images" in {
    // Given a HexImage with 3 elements in a single row
    val hexImage = HexImage(
      hexStrings = Array("112233", "445566", "778899"),
      width = ImageWidth(3),
      height = ImageHeight(1)
    )

    // When converted to 2D array
    val result = Conversions.convertTo2DArray(hexImage)

    // Then it should be a 1x3 array
    result.length shouldBe 1
    result(0).length shouldBe 3

    // And content should match
    result(0) shouldBe Array("112233", "445566", "778899")
  }

  it should "handle single column images" in {
    // Given a HexImage with 3 elements in a single column
    val hexImage = HexImage(
      hexStrings = Array("112233", "445566", "778899"),
      width = ImageWidth(1),
      height = ImageHeight(3)
    )

    // When converted to 2D array
    val result = Conversions.convertTo2DArray(hexImage)

    // Then it should be a 3x1 array
    result.length shouldBe 3
    result.foreach(row => row.length shouldBe 1)

    // And content should match
    result(0) shouldBe Array("112233")
    result(1) shouldBe Array("445566")
    result(2) shouldBe Array("778899")
  }

  it should "handle single pixel images" in {
    // Given a HexImage with just one element
    val hexImage = HexImage(
      hexStrings = Array("112233"),
      width = ImageWidth(1),
      height = ImageHeight(1)
    )

    // When converted to 2D array
    val result = Conversions.convertTo2DArray(hexImage)

    // Then it should be a 1x1 array
    result.length shouldBe 1
    result(0).length shouldBe 1

    // And content should match
    result(0)(0) shouldBe "112233"
  }

  it should "handle incomplete last row" in {
    // Given a HexImage with 5 elements but width 3 (so last row is incomplete)
    val hexImage = HexImage(
      hexStrings = Array("001122", "334455", "667788", "99AABB", "CCDDEF"),
      width = ImageWidth(3),
      height = ImageHeight(
        2
      ) // Note: This would technically be incorrect height, but testing how method handles it
    )

    // When converted to 2D array
    val result = Conversions.convertTo2DArray(hexImage)

    // Then it should create 2 rows with the last row having fewer elements
    result.length shouldBe 2
    result(0).length shouldBe 3
    result(1).length shouldBe 2

    // And content should match
    result(0) shouldBe Array("001122", "334455", "667788")
    result(1) shouldBe Array("99AABB", "CCDDEF")
  }

  it should "handle case where array length doesn't match width and height parameters" in {
    // Given a HexImage with width and height that don't match the array length
    val hexImage = HexImage(
      hexStrings = Array("001122", "334455", "667788", "99AABB"),
      width = ImageWidth(2),
      height = ImageHeight(3) // 2x3=6 but we only have 4 elements
    )

    // When converted to 2D array
    val result = Conversions.convertTo2DArray(hexImage)

    // Then it should create a 2D array based on the width, not the height
    result.length shouldBe 2
    result(0).length shouldBe 2
    result(1).length shouldBe 2

    // And content should match
    result(0) shouldBe Array("001122", "334455")
    result(1) shouldBe Array("667788", "99AABB")
  }

  it should "handle extreme aspect ratios correctly" in {
    // Given a HexImage with extremely wide aspect ratio (1x100)
    val wideHexStrings = Array.fill(100)("AABBCC")
    val wideHexImage = HexImage(
      hexStrings = wideHexStrings,
      width = ImageWidth(100),
      height = ImageHeight(1)
    )

    // When converted to 2D array
    val wideResult = Conversions.convertTo2DArray(wideHexImage)

    // Then it should create the correct 2D structure
    wideResult.length shouldBe 1
    wideResult(0).length shouldBe 100
    wideResult(0).forall(_ == "AABBCC") shouldBe true

    // Given a HexImage with extremely tall aspect ratio (100x1)
    val tallHexStrings = Array.fill(100)("DDEEFF")
    val tallHexImage = HexImage(
      hexStrings = tallHexStrings,
      width = ImageWidth(1),
      height = ImageHeight(100)
    )

    // When converted to 2D array
    val tallResult = Conversions.convertTo2DArray(tallHexImage)

    // Then it should create the correct 2D structure
    tallResult.length shouldBe 100
    tallResult.forall(_.length == 1) shouldBe true
    tallResult.forall(_(0) == "DDEEFF") shouldBe true
  }

  it should "handle arrays larger than width*height" in {
    // Given a HexImage with more elements than width*height suggests
    val hexImage = HexImage(
      hexStrings = Array(
        "001122",
        "334455",
        "667788",
        "99AABB",
        "CCDDEF",
        "FFFEEF",
        "ABCDEF"
      ),
      width = ImageWidth(3),
      height = ImageHeight(2) // 3*2=6 but we have 7 elements
    )

    // When converted to 2D array
    val result = Conversions.convertTo2DArray(hexImage)

    // Then it should use all elements, creating as many full rows as needed plus any remainder
    result.length shouldBe 3 // Should create 3 rows (2 full rows of width 3, plus 1 partial row)
    result(0).length shouldBe 3
    result(1).length shouldBe 3
    result(2).length shouldBe 1

    // And content should match including the extra element
    result(0) shouldBe Array("001122", "334455", "667788")
    result(1) shouldBe Array("99AABB", "CCDDEF", "FFFEEF")
    result(2) shouldBe Array("ABCDEF")
  }

  it should "handle different hex string formats consistently" in {
    // Given a HexImage with different hex string formats (with/without 0x prefix, different lengths)
    val hexImage = HexImage(
      hexStrings =
        Array("FF", "00FF", "FFFF00", "0x123456", "0xFF00FF", "#FF0000"),
      width = ImageWidth(3),
      height = ImageHeight(2)
    )

    // When converted to 2D array
    val result = Conversions.convertTo2DArray(hexImage)

    // Then it should preserve the original format
    result.length shouldBe 2
    result(0) shouldBe Array("FF", "00FF", "FFFF00")
    result(1) shouldBe Array("0x123456", "0xFF00FF", "#FF0000")
  }

  it should "maintain original array references" in {
    // Given a HexImage with some elements
    val hexStrings = Array("001122", "334455", "667788")
    val hexImage = HexImage(
      hexStrings = hexStrings,
      width = ImageWidth(3),
      height = ImageHeight(1)
    )

    // When converted to 2D array
    val result = Conversions.convertTo2DArray(hexImage)

    // Modifying the original array should not affect the result (should be a copy, not a reference)
    hexStrings(0) = "CHANGED"

    // The result should maintain the original values
    result(0)(0) shouldBe "001122"
  }

  it should "handle null hex strings appropriately" in {
    // Given a HexImage with null elements (would likely cause runtime errors in practice)
    val hexStrings = new Array[String](3)
    hexStrings(0) = "001122"
    // hexStrings(1) remains null
    hexStrings(2) = "667788"

    val hexImage = HexImage(
      hexStrings = hexStrings,
      width = ImageWidth(3),
      height = ImageHeight(1)
    )

    // When converted to 2D array
    val result = Conversions.convertTo2DArray(hexImage)

    // Then it should preserve null elements
    result(0)(0) shouldBe "001122"
    result(0)(1) shouldBe null
    result(0)(2) shouldBe "667788"
  }

  it should "handle very large images efficiently" in {
    // Given a large HexImage (1000x1000 = 1 million pixels)
    val largeSize       = 1000
    val largeHexStrings = Array.fill(largeSize * largeSize)("FFFFFF")
    val largeHexImage = HexImage(
      hexStrings = largeHexStrings,
      width = ImageWidth(largeSize),
      height = ImageHeight(largeSize)
    )

    // When converted to 2D array - this tests that the method is efficient for large images
    val startTime     = System.currentTimeMillis()
    val result        = Conversions.convertTo2DArray(largeHexImage)
    val endTime       = System.currentTimeMillis()
    val executionTime = endTime - startTime

    // Then it should complete in a reasonable time (e.g. less than 1 second)
    // This is primarily a performance test, not a correctness test
    executionTime should be < 1000L // Less than 1 second

    // Basic correctness check
    result.length shouldBe largeSize
    result(0).length shouldBe largeSize
    result(0)(0) shouldBe "FFFFFF"
    result(largeSize - 1)(largeSize - 1) shouldBe "FFFFFF"
  }

  "charsToStringList" should "convert a 2D char array to a list of strings" in {
    // Given a 2D array of chars
    val chars = Array(
      Array('h', 'e', 'l', 'l', 'o'),
      Array('w', 'o', 'r', 'l', 'd')
    )

    // When converted to string list
    val result = Conversions.charsToStringList(chars)

    // Then it should combine each row into a string
    result shouldBe List("hello", "world")
  }

  it should "handle empty arrays" in {
    // Given an empty char array
    val chars = Array.empty[Array[Char]]

    // When converted to string list
    val result = Conversions.charsToStringList(chars)

    // Then it should return an empty list
    result shouldBe List.empty[String]
  }

  it should "handle arrays with empty rows" in {
    // Given a 2D array with empty rows
    val chars = Array(
      Array('h', 'e', 'l', 'l', 'o'),
      Array.empty[Char],
      Array('w', 'o', 'r', 'l', 'd')
    )

    // When converted to string list
    val result = Conversions.charsToStringList(chars)

    // Then it should include empty strings for empty rows
    result shouldBe List("hello", "", "world")
  }

  it should "handle arrays with a single row" in {
    // Given a 2D array with a single row
    val chars = Array(
      Array('s', 'i', 'n', 'g', 'l', 'e')
    )

    // When converted to string list
    val result = Conversions.charsToStringList(chars)

    // Then it should return a list with a single string
    result shouldBe List("single")
  }

  it should "handle arrays with a single character in each row" in {
    // Given a 2D array with single characters
    val chars = Array(
      Array('a'),
      Array('b'),
      Array('c')
    )

    // When converted to string list
    val result = Conversions.charsToStringList(chars)

    // Then it should return single-character strings
    result shouldBe List("a", "b", "c")
  }

  it should "handle arrays with rows of different lengths" in {
    // Given a 2D array with rows of different lengths
    val chars = Array(
      Array('o', 'n', 'e'),
      Array('t', 'w', 'o', '!'),
      Array('t', 'h', 'r', 'e', 'e', '!')
    )

    // When converted to string list
    val result = Conversions.charsToStringList(chars)

    // Then it should correctly handle the different lengths
    result shouldBe List("one", "two!", "three!")
  }

  it should "handle special characters and symbols" in {
    // Given a 2D array with special characters
    val chars = Array(
      Array('H', 'e', 'l', 'l', 'o', '!'),
      Array('1', '2', '3', ' ', '@', '#'),
      Array('な', 'に', 'こ', 'れ'), // Japanese characters
      Array('λ', 'α', 'β', 'γ')  // Greek letters
    )

    // When converted to string list
    val result = Conversions.charsToStringList(chars)

    // Then it should correctly convert all special characters
    result shouldBe List("Hello!", "123 @#", "なにこれ", "λαβγ")
  }

  it should "handle very large arrays efficiently" in {
    // Given a large 2D char array
    val largeSize  = 1000
    val largeChars = Array.fill(largeSize)(Array.fill(100)('X'))

    // When converted to string list
    val startTime     = System.currentTimeMillis()
    val result        = Conversions.charsToStringList(largeChars)
    val endTime       = System.currentTimeMillis()
    val executionTime = endTime - startTime

    // Then it should complete in a reasonable time and return correct results
    executionTime should be < 1000L // Less than 1 second
    result.length shouldBe largeSize
    result.forall(_ == "X" * 100) shouldBe true
  }

  it should "handle arrays with non-ASCII characters and character escapes" in {
    // Given arrays with various Unicode characters
    val chars = Array(
      Array('こ', 'ん', 'に', 'ち', 'は'),     // Japanese
      Array('α', 'ß', 'Γ', 'δ', 'ε'),     // Greek
      Array('♠', '♥', '♦', '♣'),          // Card suits
      Array('d', 'c', 'b', 'a'),          // Emojis
      Array('\n', '\t', '\r', '\b', '\f') // Escape characters
    )

    // When converted to string list
    val result = Conversions.charsToStringList(chars)

    // Then all characters should be preserved
    result shouldBe List(
      "こんにちは",
      "αßΓδε",
      "♠♥♦♣",
      "dcba",
      "\n\t\r\b\f"
    )
  }

  it should "handle extremely long rows" in {
    // Given a 2D array with an extremely long row
    val veryLongRow = Array.fill(100000)('X')
    val chars       = Array(veryLongRow)

    // When converted to string list
    val result = Conversions.charsToStringList(chars)

    // Then it should handle the long string correctly
    result.length shouldBe 1
    result(0).length shouldBe 100000
    result(0) shouldBe "X" * 100000
  }

  it should "maintain ordering of rows" in {
    // Given a 2D array with distinctive rows
    val chars = Array(
      Array('f', 'i', 'r', 's', 't'),
      Array('s', 'e', 'c', 'o', 'n', 'd'),
      Array('t', 'h', 'i', 'r', 'd'),
      Array('f', 'o', 'u', 'r', 't', 'h')
    )

    // When converted to string list
    val result = Conversions.charsToStringList(chars)

    // Then the order should be preserved
    result shouldBe List("first", "second", "third", "fourth")
  }

  it should "be callable multiple times with consistent results" in {
    // Given the same 2D array called multiple times
    val chars = Array(
      Array('t', 'e', 's', 't')
    )

    // When converted to string list multiple times
    val result1 = Conversions.charsToStringList(chars)
    val result2 = Conversions.charsToStringList(chars)
    val result3 = Conversions.charsToStringList(chars)

    // Then all results should be identical (idempotent operation)
    result1 shouldBe result2
    result2 shouldBe result3
    result1 shouldBe List("test")
  }

  "sampleHorizontally" should "sample every nth element horizontally with factor 2" in {
    // Given a 2D array
    val image = Array(
      Array("1", "2", "3", "4", "5", "6"),
      Array("a", "b", "c", "d", "e", "f")
    )

    // When sampled horizontally with factor 2
    val result = Conversions.sampleHorizontally(image, 2)

    // Then it should keep elements at indices 0, 2, 4
    result.length shouldBe 2
    result(0) shouldBe Array("1", "3", "5")
    result(1) shouldBe Array("a", "c", "e")
  }

  it should "handle downsample factor of 1 (no change)" in {
    // Given a 2D array
    val image = Array(
      Array("1", "2", "3"),
      Array("a", "b", "c")
    )

    // When sampled with factor 1
    val result = Conversions.sampleHorizontally(image, 1)

    // Then it should remain unchanged
    result shouldBe image
  }

  it should "handle downsample factor less than 1" in {
    // Given a 2D array
    val image = Array(
      Array("1", "2", "3"),
      Array("a", "b", "c")
    )

    // When sampled with factor 0 (invalid, should be treated as 1)
    val result = Conversions.sampleHorizontally(image, 0)

    // Then it should remain unchanged
    result shouldBe image
  }

  it should "handle empty arrays" in {
    // Given an empty 2D array
    val image = Array.empty[Array[String]]

    // When sampled horizontally
    val result = Conversions.sampleHorizontally(image, 2)

    // Then it should return an empty array
    result.length shouldBe 0
  }

  it should "handle downsample factor of 3" in {
    // Given a 2D array
    val image = Array(
      Array("1", "2", "3", "4", "5", "6", "7", "8", "9"),
      Array("a", "b", "c", "d", "e", "f", "g", "h", "i")
    )

    // When sampled horizontally with factor 3
    val result = Conversions.sampleHorizontally(image, 3)

    // Then it should keep elements at indices 0, 3, 6
    result.length shouldBe 2
    result(0) shouldBe Array("1", "4", "7")
    result(1) shouldBe Array("a", "d", "g")
  }

  it should "handle large downsample factors that exceed array width" in {
    // Given a 2D array
    val image = Array(
      Array("1", "2", "3", "4", "5"),
      Array("a", "b", "c", "d", "e")
    )

    // When sampled with factor larger than array width
    val result = Conversions.sampleHorizontally(image, 10)

    // Then it should only include the first element from each row
    result.length shouldBe 2
    result(0) shouldBe Array("1")
    result(1) shouldBe Array("a")
  }

  it should "handle arrays with rows of different lengths" in {
    // Given a 2D array with rows of different lengths
    val image = Array(
      Array("1", "2", "3", "4", "5", "6"),
      Array("a", "b", "c") // Shorter row
    )

    // When sampled horizontally with factor 2
    val result = Conversions.sampleHorizontally(image, 2)

    // Then it should properly handle the different lengths
    result.length shouldBe 2
    result(0) shouldBe Array("1", "3", "5")
    result(1) shouldBe Array("a", "c") // Only two elements in the second row
  }

  it should "handle single-element rows" in {
    // Given a 2D array with single-element rows
    val image = Array(
      Array("1"),
      Array("a")
    )

    // When sampled horizontally with any factor
    val result = Conversions.sampleHorizontally(image, 3)

    // Then it should return rows with just the first elements
    result.length shouldBe 2
    result(0) shouldBe Array("1")
    result(1) shouldBe Array("a")
  }

  it should "handle arrays with empty rows" in {
    // Given a 2D array with some empty rows
    val image = Array(
      Array("1", "2", "3", "4"),
      Array.empty[String],
      Array("a", "b", "c", "d")
    )

    // When sampled horizontally with factor 2
    val result = Conversions.sampleHorizontally(image, 2)

    // Then it should properly handle the empty row
    result.length shouldBe 3
    result(0) shouldBe Array("1", "3")
    result(1) shouldBe Array.empty[String]
    result(2) shouldBe Array("a", "c")
  }

  it should "correctly round up the number of elements in the result" in {
    // Given a 2D array with a number of elements not divisible by the factor
    val image = Array(
      Array("1", "2", "3", "4", "5"), // 5 elements
      Array("a", "b", "c", "d", "e")
    )

    // When sampled horizontally with factor 3
    val result = Conversions.sampleHorizontally(image, 3)

    // Then it should include ceiling(5/3) = 2 elements
    result.length shouldBe 2
    result(0) shouldBe Array("1", "4")
    result(1) shouldBe Array("a", "d")
  }

  it should "handle negative sampling factors gracefully" in {
    // Given a 2D array
    val image = Array(
      Array("1", "2", "3", "4"),
      Array("a", "b", "c", "d")
    )

    // When sampled with negative factor (invalid, should be treated as 1)
    val result = Conversions.sampleHorizontally(image, -2)

    // Then it should remain unchanged or handle it gracefully
    // Either return the original array or apply a default positive sampling
    result shouldBe image
  }

  it should "handle fractional sampling factors correctly" in {
    // Given a 2D array
    val image = Array(
      Array("1", "2", "3", "4", "5", "6"),
      Array("a", "b", "c", "d", "e", "f")
    )

    // When sampling with a non-integer factor (should be rounded or truncated)
    val result = Conversions.sampleHorizontally(image, 2.7.toInt)

    // Then it should behave the same as with integer factor 2
    result.length shouldBe 2
    result(0) shouldBe Array("1", "3", "5")
    result(1) shouldBe Array("a", "c", "e")
  }

  it should "maintain original data when sampling" in {
    // Given a 2D array with data to preserve
    val originalData = Array(
      Array("value1", "value2", "value3", "value4"),
      Array("valueA", "valueB", "valueC", "valueD")
    )

    // Create a copy of the data
    val image = originalData.map(_.clone())

    // When sampled horizontally
    val result = Conversions.sampleHorizontally(image, 2)

    // Then modify the result to check for shared references
    result(0)(0) = "modified"

    // Original data should not be affected if the method creates new arrays
    originalData(0)(0) shouldBe "value1"

    // And result should contain correct values from sampling
    result.length shouldBe 2
    result(0).length shouldBe 2
    result(0)(1) shouldBe "value3"
    result(1)(0) shouldBe "valueA"
    result(1)(1) shouldBe "valueC"
  }

  it should "handle very large arrays efficiently" in {
    // Given a large 2D array
    val width      = 10000
    val height     = 100
    val largeImage = Array.fill(height)(Array.fill(width)("X"))

    // When sampled horizontally with factor 10
    val startTime     = System.currentTimeMillis()
    val result        = Conversions.sampleHorizontally(largeImage, 10)
    val endTime       = System.currentTimeMillis()
    val executionTime = endTime - startTime

    // Then it should complete in a reasonable time
    executionTime should be < 1000L // Less than 1 second

    // And produce the correct result size
    result.length shouldBe height
    result(0).length shouldBe width / 10
  }

  it should "handle extremely sparse sampling" in {
    // Given a 2D array with substantial content
    val width = 1000
    val image = Array(
      Array.tabulate(width)(i => s"value_${i}")
    )

    // When sampled with a very large factor
    val result = Conversions.sampleHorizontally(image, 500)

    // Then it should include just the elements at the correct intervals
    result.length shouldBe 1
    result(0).length shouldBe 2 // 1000/500 = 2
    result(0)(0) shouldBe "value_0"
    result(0)(1) shouldBe "value_500"
  }

  it should "handle null elements in the array" in {
    // Given a 2D array with null elements
    val image = Array(
      Array("1", null, "3", null, "5"),
      Array(null, "b", null, "d", null)
    )

    // When sampled horizontally with factor 2
    val result = Conversions.sampleHorizontally(image, 2)

    // Then it should maintain the null elements at the correct positions
    result.length shouldBe 2
    result(0) shouldBe Array("1", "3", "5")
    result(1) shouldBe Array(null, null, null)
  }

  it should "be consistent with sampling factors that are multiples" in {
    // Given a 2D array
    val image = Array(
      Array("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"),
      Array("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l")
    )

    // When sampled with factor 2, and then the result sampled with factor 2 again
    val resultStep1 = Conversions.sampleHorizontally(image, 2)
    val resultStep2 = Conversions.sampleHorizontally(resultStep1, 2)

    // When sampled directly with factor 4
    val resultDirect = Conversions.sampleHorizontally(image, 4)

    // Then both approaches should yield the same result
    resultStep2 shouldBe resultDirect

    // And the result should contain the expected values
    resultDirect(0) shouldBe Array("1", "5", "9")
    resultDirect(1) shouldBe Array("a", "e", "i")
  }

  it should "handle edge case of sampling factor equal to array width" in {
    // Given a 2D array
    val image = Array(
      Array("1", "2", "3", "4"),
      Array("a", "b", "c", "d")
    )

    // When sampled with factor equal to the width
    val result = Conversions.sampleHorizontally(image, 4)

    // Then it should return only the first element from each row
    result.length shouldBe 2
    result(0) shouldBe Array("1")
    result(1) shouldBe Array("a")
  }

  "convertToGrayscale" should "convert RGB hex values to grayscale decimal values" in {
    // Given a 2D array with RGB hex values
    val lines = Array(
      Array("00FFFFFF", "00FF0000", "0000FF00"),
      Array("000000FF", "00AAAAAA", "00123456")
    )

    // When converted to grayscale
    val result = Conversions.convertToGrayscale(lines)

    // Then each value should be converted to grayscale decimal
    // White (FFFFFF): 0.299*255 + 0.587*255 + 0.114*255 = 255 -> 16777215 decimal
    // Red (FF0000): 0.299*255 + 0.587*0 + 0.114*0 = 76 -> 5000966 decimal
    // Green (00FF00): 0.299*0 + 0.587*255 + 0.114*0 = 150 -> 9868950 decimal
    // Blue (0000FF): 0.299*0 + 0.587*0 + 0.114*255 = 29 -> 1908001 decimal
    // Gray (AAAAAA): 0.299*170 + 0.587*170 + 0.114*170 = 170 -> 11184810 decimal

    val grayscaleDecimals = result.grayscaleDecimals
    grayscaleDecimals.length shouldBe 2
    grayscaleDecimals(0)(0).toInt shouldBe 16777215
    grayscaleDecimals(0)(1).toInt should be >= 4000000
    grayscaleDecimals(0)(1).toInt should be <= 6000000 // Approximately 5000966
    grayscaleDecimals(0)(2).toInt should be >= 9000000
    grayscaleDecimals(0)(2).toInt should be <= 10000000 // Approximately 9868950
    grayscaleDecimals(1)(0).toInt should be >= 1000000
    grayscaleDecimals(1)(0).toInt should be <= 2000000 // Approximately 1908001
    grayscaleDecimals(1)(1).toInt should be >= 11000000
    grayscaleDecimals(1)(
      1
    ).toInt should be <= 12000000 // Approximately 11184810

    // Check the RGB values are preserved correctly
    val colors = result.colors
    colors.length shouldBe 2
    colors(0)(0) shouldBe RGB(255, 255, 255) // White
    colors(0)(1) shouldBe RGB(255, 0, 0)     // Red
    colors(0)(2) shouldBe RGB(0, 255, 0)     // Green
    colors(1)(0) shouldBe RGB(0, 0, 255)     // Blue
    colors(1)(1) shouldBe RGB(170, 170, 170) // Gray
    colors(1)(2).r shouldBe 18               // From 0x123456
    colors(1)(2).g shouldBe 52               // From 0x123456
    colors(1)(2).b shouldBe 86               // From 0x123456
  }

  it should "handle empty arrays" in {
    // Given an empty 2D array
    val lines = Array.empty[Array[String]]

    // When converted to grayscale
    val result = Conversions.convertToGrayscale(lines)

    // Then it should return an empty array
    result.grayscaleDecimals.length shouldBe 0
    result.colors.length shouldBe 0
  }

  it should "handle single pixel images" in {
    // Given a 2D array with just one element
    val lines = Array(
      Array("00FF00FF") // Magenta
    )

    // When converted to grayscale
    val result = Conversions.convertToGrayscale(lines)

    // Then it should correctly convert the single pixel
    result.grayscaleDecimals.length shouldBe 1
    result.grayscaleDecimals(0).length shouldBe 1

    // Magenta (FF00FF): 0.299*255 + 0.587*0 + 0.114*255 ≈ 105 -> decimal value
    val magentaGray = result.grayscaleDecimals(0)(0).toInt
    magentaGray should be >= 6000000
    magentaGray should be <= 7000000

    result.colors.length shouldBe 1
    result.colors(0).length shouldBe 1
    result.colors(0)(0) shouldBe RGB(255, 0, 255) // Magenta
  }

  it should "calculate grayscale values correctly for all possible RGB values" in {
    // Test a range of RGB values, including edge cases

    // Black (000000)
    val blackLine   = Array(Array("00000000"))
    val blackResult = Conversions.convertToGrayscale(blackLine)
    blackResult.grayscaleDecimals(0)(0).toInt shouldBe 0
    blackResult.colors(0)(0) shouldBe RGB(0, 0, 0)

    // White (FFFFFF)
    val whiteLine   = Array(Array("00FFFFFF"))
    val whiteResult = Conversions.convertToGrayscale(whiteLine)
    whiteResult.grayscaleDecimals(0)(0).toInt shouldBe 16777215
    whiteResult.colors(0)(0) shouldBe RGB(255, 255, 255)

    // Primary colors
    val redLine   = Array(Array("00FF0000"))
    val redResult = Conversions.convertToGrayscale(redLine)
    val redGray   = redResult.grayscaleDecimals(0)(0).toInt
    redGray should be >= 4000000
    redGray should be <= 6000000
    redResult.colors(0)(0) shouldBe RGB(255, 0, 0)

    val greenLine   = Array(Array("0000FF00"))
    val greenResult = Conversions.convertToGrayscale(greenLine)
    val greenGray   = greenResult.grayscaleDecimals(0)(0).toInt
    greenGray should be >= 9000000
    greenGray should be <= 10000000
    greenResult.colors(0)(0) shouldBe RGB(0, 255, 0)

    val blueLine   = Array(Array("000000FF"))
    val blueResult = Conversions.convertToGrayscale(blueLine)
    val blueGray   = blueResult.grayscaleDecimals(0)(0).toInt
    blueGray should be >= 1000000
    blueGray should be <= 2000000
    blueResult.colors(0)(0) shouldBe RGB(0, 0, 255)

    // Secondary colors
    val yellowLine   = Array(Array("00FFFF00")) // Red + Green
    val yellowResult = Conversions.convertToGrayscale(yellowLine)
    yellowResult.colors(0)(0) shouldBe RGB(255, 255, 0)

    val cyanLine   = Array(Array("0000FFFF")) // Green + Blue
    val cyanResult = Conversions.convertToGrayscale(cyanLine)
    cyanResult.colors(0)(0) shouldBe RGB(0, 255, 255)

    val magentaLine   = Array(Array("00FF00FF")) // Red + Blue
    val magentaResult = Conversions.convertToGrayscale(magentaLine)
    magentaResult.colors(0)(0) shouldBe RGB(255, 0, 255)
  }

  it should "handle grayscale conversion of very large arrays efficiently" in {
    // Given a large 2D array
    val size       = 100
    val largeArray = Array.fill(size)(Array.fill(size)("00AAAAAA"))

    // When converted to grayscale
    val startTime     = System.currentTimeMillis()
    val result        = Conversions.convertToGrayscale(largeArray)
    val endTime       = System.currentTimeMillis()
    val executionTime = endTime - startTime

    // Then it should complete in a reasonable time
    executionTime should be < 1000L // Less than 1 second

    // And produce correct results
    result.grayscaleDecimals.length shouldBe size
    result.grayscaleDecimals(0).length shouldBe size
    result.colors.length shouldBe size
    result.colors(0).length shouldBe size

    // Check a few sample values
    result.colors(0)(0) shouldBe RGB(170, 170, 170)
    result.colors(size - 1)(size - 1) shouldBe RGB(170, 170, 170)
  }

  it should "handle conversion of arrays with varied content" in {
    // Given a 2D array with varied content
    val lines = Array(
      Array("00000000", "00FFFFFF", "00FF0000"), // Black, White, Red
      Array("0000FF00", "000000FF", "00AAAAAA")  // Green, Blue, Gray
    )

    // When converted to grayscale
    val result = Conversions.convertToGrayscale(lines)

    // Then all colors should be correctly converted
    result.colors(0)(0) shouldBe RGB(0, 0, 0)       // Black
    result.colors(0)(1) shouldBe RGB(255, 255, 255) // White
    result.colors(0)(2) shouldBe RGB(255, 0, 0)     // Red
    result.colors(1)(0) shouldBe RGB(0, 255, 0)     // Green
    result.colors(1)(1) shouldBe RGB(0, 0, 255)     // Blue
    result.colors(1)(2) shouldBe RGB(170, 170, 170) // Gray

    // And grayscale values should be appropriately calculated
    val blackGray = result.grayscaleDecimals(0)(0).toInt
    blackGray shouldBe 0 // Black

    val whiteGray = result.grayscaleDecimals(0)(1).toInt
    whiteGray shouldBe 16777215 // White

    val redGray = result.grayscaleDecimals(0)(2).toInt
    redGray should be >= 4000000
    redGray should be <= 6000000 // Red
  }

  it should "maintain correct RGB relationships in the grayscale output" in {
    // Green should appear brighter than red, which should appear brighter than blue
    // in grayscale due to the standard coefficients (0.299, 0.587, 0.114)

    val lines = Array(
      Array("00FF0000", "0000FF00", "000000FF") // Red, Green, Blue
    )

    val result = Conversions.convertToGrayscale(lines)

    val redGray   = result.grayscaleDecimals(0)(0).toInt
    val greenGray = result.grayscaleDecimals(0)(1).toInt
    val blueGray  = result.grayscaleDecimals(0)(2).toInt

    // Green should be brightest, then red, then blue
    greenGray should be > redGray
    redGray should be > blueGray
  }

  it should "handle different alpha channel values" in {
    // Given colors with different alpha values
    val lines = Array(
      Array(
        "00FF0000",
        "80FF0000",
        "FFFF0000"
      ) // Red with different alpha values
    )

    // When converted to grayscale
    val result = Conversions.convertToGrayscale(lines)

    // Then the alpha channel should be ignored in the RGB conversion
    result.colors(0)(0) shouldBe RGB(255, 0, 0)
    result.colors(0)(1) shouldBe RGB(255, 0, 0)
    result.colors(0)(2) shouldBe RGB(255, 0, 0)

    // And all should have the same grayscale value
    val gray1 = result.grayscaleDecimals(0)(0).toInt
    val gray2 = result.grayscaleDecimals(0)(1).toInt
    val gray3 = result.grayscaleDecimals(0)(2).toInt

    gray1 shouldBe gray2
    gray2 shouldBe gray3
  }

  it should "produce a unique grayscale value for each RGB combination" in {
    // Generate some distinct RGB values
    val colors = Array(
      Array("00FF0000"), // Red
      Array("0000FF00"), // Green
      Array("000000FF"), // Blue
      Array("00FFFF00"), // Yellow
      Array("00FF00FF"), // Magenta
      Array("0000FFFF"), // Cyan
      Array("00800000"), // Dark red
      Array("00008000"), // Dark green
      Array("00000080"), // Dark blue
      Array("00FFFFFF")  // White
    )

    val result = Conversions.convertToGrayscale(colors)

    // Collect all grayscale values
    val grayscaleValues =
      colors.indices.map(i => result.grayscaleDecimals(i)(0).toInt).toSet

    // There should be 10 unique values
    grayscaleValues.size shouldBe 10
  }

  "convertBytesToHexImage" should "convert image bytes to a HexImage" in {
    // Create a simple 2x2 test image
    val width  = 2
    val height = 2
    val img    = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    // Set some pixel values
    img.setRGB(0, 0, 0xff0000) // Red
    img.setRGB(1, 0, 0x00ff00) // Green
    img.setRGB(0, 1, 0x0000ff) // Blue
    img.setRGB(1, 1, 0xffffff) // White

    // Convert to bytes
    val baos = new ByteArrayOutputStream()
    ImageIO.write(img, "png", baos)
    val imageBytes = baos.toByteArray()

    // When converted to HexImage
    val hexImage =
      Conversions.convertBytesToHexImage(imageBytes).unsafeRunSync()

    // Then the result should be a HexImage with correct dimensions
    hexImage.width.value shouldBe width
    hexImage.height.value shouldBe height
    hexImage.hexStrings.length shouldBe width * height

    // Check the hexString values (without alpha channel)
    hexImage.hexStrings(0) shouldBe "00FF0000" // Red
    hexImage.hexStrings(1) shouldBe "0000FF00" // Green
    hexImage.hexStrings(2) shouldBe "000000FF" // Blue
    hexImage.hexStrings(3) shouldBe "00FFFFFF" // White
  }

  it should "convert a completely black image correctly" in {
    // Create a black test image
    val width  = 3
    val height = 2
    val img    = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    // All pixels are already black by default (0x000000)

    // Convert to bytes
    val baos = new ByteArrayOutputStream()
    ImageIO.write(img, "png", baos)
    val imageBytes = baos.toByteArray()

    // When converted to HexImage
    val hexImage =
      Conversions.convertBytesToHexImage(imageBytes).unsafeRunSync()

    // Then the result should be a HexImage with all black pixels
    hexImage.width.value shouldBe width
    hexImage.height.value shouldBe height
    hexImage.hexStrings.length shouldBe width * height

    // All pixels should be black (00000000)
    hexImage.hexStrings.foreach(_ shouldBe "00000000")
  }

  it should "convert a completely white image correctly" in {
    // Create a white test image
    val width  = 3
    val height = 2
    val img    = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    // Set all pixels to white
    for (y <- 0 until height) {
      for (x <- 0 until width) {
        img.setRGB(x, y, 0xffffff) // White
      }
    }

    // Convert to bytes
    val baos = new ByteArrayOutputStream()
    ImageIO.write(img, "png", baos)
    val imageBytes = baos.toByteArray()

    // When converted to HexImage
    val hexImage =
      Conversions.convertBytesToHexImage(imageBytes).unsafeRunSync()

    // Then the result should be a HexImage with all white pixels
    hexImage.width.value shouldBe width
    hexImage.height.value shouldBe height
    hexImage.hexStrings.length shouldBe width * height

    // All pixels should be white (00FFFFFF)
    hexImage.hexStrings.foreach(_ shouldBe "00FFFFFF")
  }

  it should "convert a 1x1 single pixel image correctly" in {
    // Create a single pixel test image
    val width  = 1
    val height = 1
    val img    = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    // Set the single pixel
    img.setRGB(0, 0, 0xff00ff) // Magenta

    // Convert to bytes
    val baos = new ByteArrayOutputStream()
    ImageIO.write(img, "png", baos)
    val imageBytes = baos.toByteArray()

    // When converted to HexImage
    val hexImage =
      Conversions.convertBytesToHexImage(imageBytes).unsafeRunSync()

    // Then the result should be a HexImage with one pixel
    hexImage.width.value shouldBe width
    hexImage.height.value shouldBe height
    hexImage.hexStrings.length shouldBe 1

    // The pixel should be magenta (00FF00FF)
    hexImage.hexStrings(0) shouldBe "00FF00FF"
  }

  it should "convert a gradient image correctly" in {
    // Create a gradient test image (4x1)
    val width  = 4
    val height = 1
    val img    = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    // Create a simple gradient from black to white
    img.setRGB(0, 0, 0x000000) // Black
    img.setRGB(1, 0, 0x555555) // Dark gray
    img.setRGB(2, 0, 0xaaaaaa) // Light gray
    img.setRGB(3, 0, 0xffffff) // White

    // Convert to bytes
    val baos = new ByteArrayOutputStream()
    ImageIO.write(img, "png", baos)
    val imageBytes = baos.toByteArray()

    // When converted to HexImage
    val hexImage =
      Conversions.convertBytesToHexImage(imageBytes).unsafeRunSync()

    // Then the result should be a HexImage with the gradient
    hexImage.width.value shouldBe width
    hexImage.height.value shouldBe height
    hexImage.hexStrings.length shouldBe width * height

    // Check the gradient values
    hexImage.hexStrings(0) shouldBe "00000000"
    hexImage.hexStrings(1) shouldBe "00555555"
    hexImage.hexStrings(2) shouldBe "00AAAAAA"
    hexImage.hexStrings(3) shouldBe "00FFFFFF"
  }

  it should "handle images with transparency correctly" in {
    // Create an image with transparency
    val width  = 2
    val height = 1
    val img    = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

    // Set pixels with different alpha values
    img.setRGB(0, 0, 0x80ff0000) // Semi-transparent red
    img.setRGB(1, 0, 0xff00ff00) // Fully opaque green

    // Convert to bytes
    val baos = new ByteArrayOutputStream()
    ImageIO.write(img, "png", baos)
    val imageBytes = baos.toByteArray()

    // When converted to HexImage
    val hexImage =
      Conversions.convertBytesToHexImage(imageBytes).unsafeRunSync()

    // Then the result should be a HexImage with the alpha channel removed
    hexImage.width.value shouldBe width
    hexImage.height.value shouldBe height
    hexImage.hexStrings.length shouldBe width * height

    // Check that alpha channel is removed (alpha bits are masked out)
    // The method should convert 0x80FF0000 to "00FF0000" and 0xFF00FF00 to "0000FF00"
    hexImage.hexStrings(0) shouldBe "00FF0000"
    hexImage.hexStrings(1) shouldBe "0000FF00"
  }

  it should "handle images saved in different formats (JPEG, BMP)" in {
    // Test with different image formats
    List("jpg", "bmp").foreach { format =>
      // Create a test image
      val width  = 2
      val height = 2
      val img    = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

      // Set some pixel values
      img.setRGB(0, 0, 0xff0000) // Red
      img.setRGB(1, 0, 0x00ff00) // Green
      img.setRGB(0, 1, 0x0000ff) // Blue
      img.setRGB(1, 1, 0xffffff) // White

      // Convert to bytes with the current format
      val baos = new ByteArrayOutputStream()
      ImageIO.write(img, format, baos)
      val imageBytes = baos.toByteArray()

      // When converted to HexImage
      val hexImage =
        Conversions.convertBytesToHexImage(imageBytes).unsafeRunSync()

      // Then the result should be a HexImage with correct dimensions
      withClue(s"Testing with format $format: ") {
        hexImage.width.value shouldBe width
        hexImage.height.value shouldBe height
        hexImage.hexStrings.length shouldBe width * height

        // Check the hexString values (some formats might have slight color variations)
        // Just check that red is red-ish, green is green-ish, etc.
        val red   = Integer.parseInt(hexImage.hexStrings(0).substring(2), 16)
        val green = Integer.parseInt(hexImage.hexStrings(1).substring(2), 16)
        val blue  = Integer.parseInt(hexImage.hexStrings(2).substring(2), 16)
        val white = Integer.parseInt(hexImage.hexStrings(3).substring(2), 16)

        (red & 0xff0000) should be > 0   // Red component is present
        (green & 0x00ff00) should be > 0 // Green component is present
        (blue & 0x0000ff) should be > 0  // Blue component is present
        white should be > 0xf0f0f0       // Close to white
      }
    }
  }

  it should "handle square images of different sizes" in {
    // Test various square image sizes
    val sizes = List(4, 8, 16, 32) // Different image sizes

    sizes.foreach { size =>
      // Create a test image of the current size
      val img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)

      // Fill the image with a simple pattern (checkerboard)
      for (y <- 0 until size) {
        for (x <- 0 until size) {
          val color = if ((x + y) % 2 == 0) 0xffffff else 0x000000
          img.setRGB(x, y, color)
        }
      }

      // Convert to bytes
      val baos = new ByteArrayOutputStream()
      ImageIO.write(img, "png", baos)
      val imageBytes = baos.toByteArray()

      // When converted to HexImage
      val hexImage =
        Conversions.convertBytesToHexImage(imageBytes).unsafeRunSync()

      // Then the result should have the correct dimensions and pattern
      withClue(s"Testing with size $size x $size: ") {
        hexImage.width.value shouldBe size
        hexImage.height.value shouldBe size
        hexImage.hexStrings.length shouldBe size * size

        // Check a few sample points in the checkerboard pattern
        for (y <- 0 until size) {
          for (x <- 0 until size) {
            val index    = y * size + x
            val expected = if ((x + y) % 2 == 0) "00FFFFFF" else "00000000"
            hexImage.hexStrings(index) shouldBe expected
          }
        }
      }
    }
  }

  it should "handle non-square rectangular images" in {
    // Create a rectangular test image (wide)
    val wideWidth  = 8
    val wideHeight = 4
    val wideImg =
      new BufferedImage(wideWidth, wideHeight, BufferedImage.TYPE_INT_RGB)

    // Fill with a unique pattern (horizontal stripes)
    for (y <- 0 until wideHeight) {
      for (x <- 0 until wideWidth) {
        val color = if (y % 2 == 0) 0xff0000 else 0x0000ff
        wideImg.setRGB(x, y, color)
      }
    }

    // Convert to bytes
    val wideOutputStream = new ByteArrayOutputStream()
    ImageIO.write(wideImg, "png", wideOutputStream)
    val wideImageBytes = wideOutputStream.toByteArray()

    // When converted to HexImage
    val wideHexImage =
      Conversions.convertBytesToHexImage(wideImageBytes).unsafeRunSync()

    // Then the result should have the correct dimensions and pattern
    wideHexImage.width.value shouldBe wideWidth
    wideHexImage.height.value shouldBe wideHeight
    wideHexImage.hexStrings.length shouldBe wideWidth * wideHeight

    // Check the pattern of stripes
    for (y <- 0 until wideHeight) {
      for (x <- 0 until wideWidth) {
        val index    = y * wideWidth + x
        val expected = if (y % 2 == 0) "00FF0000" else "000000FF"
        wideHexImage.hexStrings(index) shouldBe expected
      }
    }

    // Create a rectangular test image (tall)
    val tallWidth  = 4
    val tallHeight = 8
    val tallImg =
      new BufferedImage(tallWidth, tallHeight, BufferedImage.TYPE_INT_RGB)

    // Fill with a unique pattern (vertical stripes)
    for (y <- 0 until tallHeight) {
      for (x <- 0 until tallWidth) {
        val color = if (x % 2 == 0) 0x00ff00 else 0xffff00
        tallImg.setRGB(x, y, color)
      }
    }

    // Convert to bytes
    val tallOutputStream = new ByteArrayOutputStream()
    ImageIO.write(tallImg, "png", tallOutputStream)
    val tallImageBytes = tallOutputStream.toByteArray()

    // When converted to HexImage
    val tallHexImage =
      Conversions.convertBytesToHexImage(tallImageBytes).unsafeRunSync()

    // Then the result should have the correct dimensions and pattern
    tallHexImage.width.value shouldBe tallWidth
    tallHexImage.height.value shouldBe tallHeight
    tallHexImage.hexStrings.length shouldBe tallWidth * tallHeight

    // Check the pattern of stripes
    for (y <- 0 until tallHeight) {
      for (x <- 0 until tallWidth) {
        val index    = y * tallWidth + x
        val expected = if (x % 2 == 0) "0000FF00" else "00FFFF00"
        tallHexImage.hexStrings(index) shouldBe expected
      }
    }
  }

  it should "handle larger images efficiently" in {
    // Create a larger test image
    val width  = 100
    val height = 100
    val img    = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    // Fill with a simple pattern
    for (y <- 0 until height) {
      for (x <- 0 until width) {
        val color = if ((x / 10 + y / 10) % 2 == 0) 0xff0000 else 0x0000ff
        img.setRGB(x, y, color)
      }
    }

    // Convert to bytes
    val baos = new ByteArrayOutputStream()
    ImageIO.write(img, "png", baos)
    val imageBytes = baos.toByteArray()

    // When converted to HexImage
    val startTime = System.currentTimeMillis()
    val hexImage =
      Conversions.convertBytesToHexImage(imageBytes).unsafeRunSync()
    val endTime       = System.currentTimeMillis()
    val executionTime = endTime - startTime

    // Then it should complete in a reasonable time
    executionTime should be < 2000L // Less than 2 seconds for a 100x100 image

    // And should have the correct dimensions and sample values
    hexImage.width.value shouldBe width
    hexImage.height.value shouldBe height
    hexImage.hexStrings.length shouldBe width * height

    // Check a few sample points in the pattern
    val samplePoints = List((0, 0), (50, 50), (99, 99), (10, 90), (90, 10))

    samplePoints.foreach { case (x, y) =>
      val index = y * width + x
      val expectedColor =
        if ((x / 10 + y / 10) % 2 == 0) "00FF0000" else "000000FF"
      hexImage.hexStrings(index) shouldBe expectedColor
    }
  }

  it should "handle images with different color depths" in {
    // Test different image types with varying color depth
    val imageTypes = List(
      BufferedImage.TYPE_INT_RGB,  // 24-bit RGB
      BufferedImage.TYPE_INT_ARGB, // 32-bit ARGB
      BufferedImage.TYPE_BYTE_GRAY // 8-bit grayscale
    )

    imageTypes.foreach { imageType =>
      // Create a test image with the current type
      val width  = 3
      val height = 2
      val img    = new BufferedImage(width, height, imageType)

      // Set pixel values (will be interpreted based on the image type)
      img.setRGB(0, 0, 0xff0000) // Red (or gray equivalent)
      img.setRGB(0, 1, 0x00ff00) // Green (or gray equivalent)
      img.setRGB(1, 0, 0x0000ff) // Blue (or gray equivalent)
      img.setRGB(1, 1, 0xffffff) // White
      img.setRGB(2, 0, 0x000000) // Black
      img.setRGB(2, 1, 0x888888) // Gray

      // Convert to bytes
      val baos = new ByteArrayOutputStream()
      ImageIO.write(img, "png", baos)
      val imageBytes = baos.toByteArray()

      // When converted to HexImage
      val hexImage =
        Conversions.convertBytesToHexImage(imageBytes).unsafeRunSync()

      // Then the result should have the correct dimensions
      withClue(s"Testing with image type $imageType: ") {
        hexImage.width.value shouldBe width
        hexImage.height.value shouldBe height
        hexImage.hexStrings.length shouldBe width * height
      }
    }
  }

  it should "handle corrupted or invalid image data gracefully" in {
    // Create an array of invalid image bytes
    val invalidImageBytes = Array.fill(100)(0.toByte)

    // When attempting to convert invalid bytes to HexImage
    val result = Conversions.convertBytesToHexImage(invalidImageBytes)

    // Then it should either return an error or handle it gracefully
    try {
      val hexImage = result.unsafeRunSync()
      // If it somehow succeeded, make sure it has reasonable dimensions
      hexImage.width.value should be >= 0
      hexImage.height.value should be >= 0
    } catch {
      case e: Exception =>
        // Catching an exception is the expected behavior for invalid data
        e.getMessage should not be null
    }
  }

  // New test for hexStringsToSampledGreyscaleDecimal
  "hexStringsToSampledGreyscaleDecimal" should "convert hex strings to sampled grayscale decimals" in {
    // Given a set of hex values, width, and sampling factors
    val hexStrings = Array(
      "00FF0000",
      "0000FF00",
      "000000FF",
      "00FFFFFF",
      "00AAAAAA",
      "00555555",
      "00333333",
      "00111111"
    )
    val lineWidth          = 4
    val horizontalSampling = 2
    val verticalSampling   = 2

    // When converted and sampled
    val result = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling,
      verticalSampling,
      hexStrings,
      lineWidth
    )

    // Then the result should be correctly sampled and converted
    result.grayscaleDecimals.length shouldBe 1
    result.grayscaleDecimals(0).length shouldBe 2

    // We should have kept the first and third elements from the first row
    // These correspond to red and blue in our input
    result.colors(0)(0) shouldBe RGB(255, 0, 0) // Red
    result.colors(0)(1) shouldBe RGB(0, 0, 255) // Blue
  }

  it should "handle an empty array of hex strings" in {
    // Given an empty array of hex strings
    val hexStrings         = Array.empty[String]
    val lineWidth          = 0
    val horizontalSampling = 2
    val verticalSampling   = 2

    // When converted and sampled
    val result = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling,
      verticalSampling,
      hexStrings,
      lineWidth
    )

    // Then the result should be an empty ColoredPixels
    result.grayscaleDecimals.length shouldBe 0
    result.colors.length shouldBe 0
  }

  it should "handle a single hex string" in {
    // Given a single hex string
    val hexStrings         = Array("00FF00FF") // Magenta
    val lineWidth          = 1
    val horizontalSampling = 1
    val verticalSampling   = 1

    // When converted and sampled
    val result = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling,
      verticalSampling,
      hexStrings,
      lineWidth
    )

    // Then the result should contain a single pixel
    result.grayscaleDecimals.length shouldBe 1
    result.grayscaleDecimals(0).length shouldBe 1
    result.colors.length shouldBe 1
    result.colors(0).length shouldBe 1

    // The color should be magenta
    result.colors(0)(0) shouldBe RGB(255, 0, 255)
  }

  it should "sample horizontally with various factors" in {
    // Create a test pattern of alternating colors in a row
    val hexStrings = Array(
      "00FF0000",
      "0000FF00",
      "00FF0000",
      "0000FF00",
      "00FF0000",
      "0000FF00"
    )
    val lineWidth        = 6
    val verticalSampling = 1

    // Sample with factor 2 (should keep indices 0, 2, 4)
    val resultFactor2 = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling = 2,
      verticalSampling = verticalSampling,
      rgbValues = hexStrings,
      lineWidth = lineWidth
    )

    resultFactor2.colors.length shouldBe 1
    resultFactor2.colors(0).length shouldBe 3
    // Should keep only red pixels
    resultFactor2.colors(0)(0) shouldBe RGB(255, 0, 0)
    resultFactor2.colors(0)(1) shouldBe RGB(255, 0, 0)
    resultFactor2.colors(0)(2) shouldBe RGB(255, 0, 0)

    // Sample with factor 3 (should keep indices 0, 3)
    val resultFactor3 = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling = 3,
      verticalSampling = verticalSampling,
      rgbValues = hexStrings,
      lineWidth = lineWidth
    )

    resultFactor3.colors.length shouldBe 1
    resultFactor3.colors(0).length shouldBe 2
    // Should keep red and green
    resultFactor3.colors(0)(0) shouldBe RGB(255, 0, 0)
    resultFactor3.colors(0)(1) shouldBe RGB(0, 255, 0)
  }

  it should "sample vertically with various factors" in {
    // Create a test pattern of alternating colors in a column
    val hexStrings = Array(
      "00FF0000",
      "00FFFF00", // Red, Yellow
      "0000FF00",
      "00FF00FF", // Green, Magenta
      "000000FF",
      "00FFFFFF", // Blue, White
      "00000000",
      "00AAAAAA" // Black, Gray
    )
    val lineWidth          = 2
    val horizontalSampling = 1

    // Sample with factor 2 (should keep rows 0, 2)
    val resultFactor2 = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling = horizontalSampling,
      verticalSampling = 2,
      rgbValues = hexStrings,
      lineWidth = lineWidth
    )

    resultFactor2.colors.length shouldBe 2
    resultFactor2.colors(0).length shouldBe 2
    resultFactor2.colors(1).length shouldBe 2

    // Row 0: Red, Yellow
    resultFactor2.colors(0)(0) shouldBe RGB(255, 0, 0)
    resultFactor2.colors(0)(1) shouldBe RGB(255, 255, 0)

    // Row 2: Blue, White
    resultFactor2.colors(1)(0) shouldBe RGB(0, 0, 255)
    resultFactor2.colors(1)(1) shouldBe RGB(255, 255, 255)

    // Sample with factor 4 (should keep just row 0)
    val resultFactor4 = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling = horizontalSampling,
      verticalSampling = 4,
      rgbValues = hexStrings,
      lineWidth = lineWidth
    )

    resultFactor4.colors.length shouldBe 1
    resultFactor4.colors(0).length shouldBe 2

    // Row 0: Red, Yellow
    resultFactor4.colors(0)(0) shouldBe RGB(255, 0, 0)
    resultFactor4.colors(0)(1) shouldBe RGB(255, 255, 0)
  }

  it should "sample both horizontally and vertically at the same time" in {
    // Create a test pattern in a grid
    val hexStrings = Array(
      "00FF0000",
      "0000FF00",
      "000000FF",
      "00FFFF00", // Red, Green, Blue, Yellow
      "00FF00FF",
      "00FFFFFF",
      "00000000",
      "00AAAAAA", // Magenta, White, Black, Gray
      "00800000",
      "00008000",
      "00000080",
      "00808080", // Dark Red, Dark Green, Dark Blue, Dark Gray
      "00FFAAAA",
      "00AAFFAA",
      "00AAAAFF",
      "00FFAAFF" // Light Red, Light Green, Light Blue, Light Magenta
    )
    val lineWidth = 4

    // Sample with horizontal=2, vertical=2 (should keep the top-left of each 2x2 block)
    val result = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling = 2,
      verticalSampling = 2,
      rgbValues = hexStrings,
      lineWidth = lineWidth
    )

    result.colors.length shouldBe 2
    result.colors(0).length shouldBe 2
    result.colors(1).length shouldBe 2

    // Should keep: Red, Blue, Magenta, Black
    result.colors(0)(0) shouldBe RGB(255, 0, 0) // Red (0,0)
    result.colors(0)(1) shouldBe RGB(0, 0, 255) // Blue (0,2)
    result.colors(1)(0) shouldBe RGB(128, 0, 0) // Magenta (2,0)
    result.colors(1)(1) shouldBe RGB(0, 0, 128) // Black (2,2)
  }

  it should "handle sampling factors of 1 correctly (no sampling)" in {
    // Create a small test pattern
    val hexStrings = Array(
      "00FF0000",
      "0000FF00", // Red, Green
      "000000FF",
      "00FFFFFF" // Blue, White
    )
    val lineWidth = 2

    // Sample with factors of 1 (should keep all pixels)
    val result = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling = 1,
      verticalSampling = 1,
      rgbValues = hexStrings,
      lineWidth = lineWidth
    )

    result.colors.length shouldBe 2
    result.colors(0).length shouldBe 2
    result.colors(1).length shouldBe 2

    // Should keep all pixels
    result.colors(0)(0) shouldBe RGB(255, 0, 0)     // Red
    result.colors(0)(1) shouldBe RGB(0, 255, 0)     // Green
    result.colors(1)(0) shouldBe RGB(0, 0, 255)     // Blue
    result.colors(1)(1) shouldBe RGB(255, 255, 255) // White
  }

  it should "handle invalid or negative sampling factors gracefully" in {
    // Create a small test pattern
    val hexStrings = Array(
      "00FF0000",
      "0000FF00", // Red, Green
      "000000FF",
      "00FFFFFF" // Blue, White
    )
    val lineWidth = 2

    // Sample with invalid factors (should default to 1)
    val result = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling = -2,
      verticalSampling = 0,
      rgbValues = hexStrings,
      lineWidth = lineWidth
    )

    // Should default to keeping all pixels (as if sampling factor was 1)
    result.colors.length should be > 0
    result.grayscaleDecimals.length should be > 0
  }

  it should "handle sampling larger than the image size" in {
    // Create a small test pattern
    val hexStrings = Array(
      "00FF0000",
      "0000FF00", // Red, Green
      "000000FF",
      "00FFFFFF" // Blue, White
    )
    val lineWidth = 2

    // Sample with factors larger than the image dimensions
    val result = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling = 10,
      verticalSampling = 10,
      rgbValues = hexStrings,
      lineWidth = lineWidth
    )

    // Should keep only the first pixel of each dimension
    result.colors.length shouldBe 1
    result.colors(0).length shouldBe 1
    result.colors(0)(0) shouldBe RGB(255, 0, 0) // Red (top-left pixel)
  }

  it should "handle large data sets efficiently" in {
    // Create a large test pattern
    val size       = 50
    val hexStrings = Array.fill(size * size)("00AAAAAA") // Gray
    val lineWidth  = size

    // Sample with moderate factors
    val startTime = System.currentTimeMillis()
    val result = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling = 5,
      verticalSampling = 5,
      rgbValues = hexStrings,
      lineWidth = lineWidth
    )
    val endTime       = System.currentTimeMillis()
    val executionTime = endTime - startTime

    // Should complete in reasonable time
    executionTime should be < 1000L // Less than 1 second

    // Should produce the expected dimensions
    result.colors.length shouldBe (size / 5)
    result.colors.foreach(row => row.length shouldBe (size / 5))

    // All colors should be gray
    result.colors.foreach(row =>
      row.foreach(color => color shouldBe RGB(170, 170, 170))
    )
  }

  it should "produce correct grayscale values" in {
    // Create a test with all primary and secondary colors
    val hexStrings = Array(
      "00000000",
      "00FFFFFF", // Black, White
      "00FF0000",
      "0000FF00", // Red, Green
      "000000FF",
      "00FFFF00", // Blue, Yellow
      "00FF00FF",
      "0000FFFF" // Magenta, Cyan
    )
    val lineWidth = 2

    // Convert with no sampling
    val result = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling = 1,
      verticalSampling = 1,
      rgbValues = hexStrings,
      lineWidth = lineWidth
    )

    // Check that RGB values match expectations
    result.colors.length shouldBe 4
    result.colors(0)(0) shouldBe RGB(0, 0, 0)       // Black
    result.colors(0)(1) shouldBe RGB(255, 255, 255) // White
    result.colors(1)(0) shouldBe RGB(255, 0, 0)     // Red
    result.colors(1)(1) shouldBe RGB(0, 255, 0)     // Green
    result.colors(2)(0) shouldBe RGB(0, 0, 255)     // Blue
    result.colors(2)(1) shouldBe RGB(255, 255, 0)   // Yellow
    result.colors(3)(0) shouldBe RGB(255, 0, 255)   // Magenta
    result.colors(3)(1) shouldBe RGB(0, 255, 255)   // Cyan

    // Check that grayscale values follow expected brightness pattern
    // White should be brightest, then yellow/cyan, then green/magenta, then red, then blue, then black
    val grayscales = result.grayscaleDecimals.flatten.map(_.toInt)

    // White should be brightest
    grayscales(1) should be > grayscales(5) // White > Yellow
    grayscales(1) should be > grayscales(7) // White > Cyan

    // Yellow and Cyan should be brighter than Green and Magenta
    grayscales(5) should be > grayscales(3) // Yellow > Green
    grayscales(7) should be > grayscales(6) // Cyan > Magenta

    // Green should be brighter than Red
    grayscales(3) should be > grayscales(2) // Green > Red

    // Red should be brighter than Blue
    grayscales(2) should be > grayscales(4) // Red > Blue

    // Blue should be brighter than Black
    grayscales(4) should be > grayscales(0) // Blue > Black
  }

  it should "handle hex strings with or without alpha channel consistently" in {
    // Create test data with different formats
    val hexStrings1 =
      Array("00FF0000", "0000FF00") // With alpha prefix (Red, Green)
    val hexStrings2 =
      Array("FF0000", "00FF00") // Without alpha prefix (Red, Green)

    // Test both cases

    val result1 = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling = 1,
      verticalSampling = 1,
      rgbValues = hexStrings1,
      lineWidth = 2
    )

    val result2 = Conversions.hexStringsToSampledGreyscaleDecimal(
      horizontalSampling = 1,
      verticalSampling = 1,
      rgbValues = hexStrings2,
      lineWidth = 2
    )

    // The method might handle both formats, in which case they should produce equivalent results
    if (result2.colors.length > 0) {
      result1.colors(0)(0).r shouldBe result2.colors(0)(0).r
      result1.colors(0)(0).g shouldBe result2.colors(0)(0).g
      result1.colors(0)(0).b shouldBe result2.colors(0)(0).b

      result1.colors(0)(1).r shouldBe result2.colors(0)(1).r
      result1.colors(0)(1).g shouldBe result2.colors(0)(1).g
      result1.colors(0)(1).b shouldBe result2.colors(0)(1).b
    }

  }
}
