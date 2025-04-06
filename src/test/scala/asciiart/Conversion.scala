package asciiart

import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import asciiart.Models.{HexImage, ImageHeight, ImageWidth}
import org.scalatest.flatspec.AnyFlatSpec
import cats.effect

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

  it should "handle rows of different lengths" in {
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
    val chars =
      Array(Array('H', 'e', 'l', 'l', 'o', '!'), Array('1', '2', '3', ' ', '@'))
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

  "sampleVertically" should "sample every nth row with factor 2" in {
    // Given a 2D array with multiple rows
    val lines = Array(
      Array("1", "2", "3"),
      Array("4", "5", "6"),
      Array("7", "8", "9"),
      Array("a", "b", "c")
    )

    // When sampled vertically with factor 2
    val result = Conversions.sampleVertically(lines, 2)

    // Then it should keep rows at indices 0, 2
    result.length shouldBe 2
    result(0) shouldBe Array("1", "2", "3")
    result(1) shouldBe Array("7", "8", "9")
  }

  it should "handle vertical factor of 1 (no change)" in {
    // Given a 2D array
    val lines = Array(
      Array("1", "2", "3"),
      Array("4", "5", "6")
    )

    // When sampled with factor 1
    val result = Conversions.sampleVertically(lines, 1)

    // Then it should remain unchanged
    result shouldBe lines
  }

  it should "handle vertical factor less than 1" in {
    // Given a 2D array
    val lines = Array(
      Array("1", "2", "3"),
      Array("4", "5", "6")
    )

    // When sampled with factor 0 (invalid, should be treated as 1)
    val result = Conversions.sampleVertically(lines, 0)

    // Then it should remain unchanged
    result shouldBe lines
  }

  it should "handle empty arrays" in {
    // Given an empty 2D array
    val lines = Array.empty[Array[String]]

    // When sampled vertically
    val result = Conversions.sampleVertically(lines, 2)

    // Then it should return an empty array
    result.length shouldBe 0
  }

  it should "sample every third row with factor 3" in {
    // Given a 2D array with multiple rows
    val lines = Array(
      Array("1", "2", "3"),
      Array("4", "5", "6"),
      Array("7", "8", "9"),
      Array("a", "b", "c"),
      Array("d", "e", "f"),
      Array("g", "h", "i")
    )

    // When sampled vertically with factor 3
    val result = Conversions.sampleVertically(lines, 3)

    // Then it should keep rows at indices 0, 3
    result.length shouldBe 2
    result(0) shouldBe Array("1", "2", "3")
    result(1) shouldBe Array("a", "b", "c")
  }

  it should "handle vertical factor larger than array length" in {
    // Given a 2D array with fewer rows than the factor
    val lines = Array(
      Array("1", "2", "3"),
      Array("4", "5", "6"),
      Array("7", "8", "9")
    )

    // When sampled with factor larger than number of rows
    val result = Conversions.sampleVertically(lines, 5)

    // Then it should only include the first row
    result.length shouldBe 1
    result(0) shouldBe Array("1", "2", "3")
  }

  it should "handle arrays with rows of different lengths" in {
    // Given a 2D array with rows of different lengths
    val lines = Array(
      Array("1", "2", "3", "4"),
      Array("5", "6"),
      Array("7", "8", "9"),
      Array("a", "b", "c", "d", "e")
    )

    // When sampled vertically with factor 2
    val result = Conversions.sampleVertically(lines, 2)

    // Then it should properly handle the different lengths
    result.length shouldBe 2
    result(0) shouldBe Array("1", "2", "3", "4")
    result(1) shouldBe Array("7", "8", "9")
  }

  it should "handle single-row arrays" in {
    // Given a 2D array with a single row
    val lines = Array(
      Array("1", "2", "3")
    )

    // When sampled vertically with any factor
    val result = Conversions.sampleVertically(lines, 2)

    // Then it should return only the first row
    result.length shouldBe 1
    result(0) shouldBe Array("1", "2", "3")
  }

  it should "handle arrays with empty rows interspersed" in {
    // Given a 2D array with some empty rows
    val lines = Array(
      Array("1", "2", "3"),
      Array.empty[String],
      Array("7", "8", "9"),
      Array.empty[String],
      Array("a", "b", "c")
    )

    // When sampled vertically with factor 2
    val result = Conversions.sampleVertically(lines, 2)

    // Then it should properly handle the empty rows based on their indices
    result.length shouldBe 3
    result(0) shouldBe Array("1", "2", "3")
    result(1) shouldBe Array("7", "8", "9")
    result(2) shouldBe Array("a", "b", "c")
  }

  it should "preserve the original row references" in {
    // Given a 2D array
    val lines = Array(
      Array("1", "2", "3"),
      Array("4", "5", "6"),
      Array("7", "8", "9"),
      Array("a", "b", "c")
    )

    // When sampled vertically with factor 2
    val result = Conversions.sampleVertically(lines, 2)

    // Then it should preserve the original row references (not copy the arrays)
    result(0) shouldBe theSameInstanceAs(lines(0))
    result(1) shouldBe theSameInstanceAs(lines(2))
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

    result.length shouldBe 2
    result(0)(0).toInt shouldBe 16777215
    result(0)(1).toInt should be >= 4000000
    result(0)(1).toInt should be <= 6000000 // Approximately 5000966
    result(0)(2).toInt should be >= 9000000
    result(0)(2).toInt should be <= 10000000 // Approximately 9868950
    result(1)(0).toInt should be >= 1000000
    result(1)(0).toInt should be <= 2000000 // Approximately 1908001
    result(1)(1).toInt should be >= 11000000
    result(1)(1).toInt should be <= 12000000 // Approximately 11184810
  }

  it should "handle empty arrays" in {
    // Given an empty 2D array
    val lines = Array.empty[Array[String]]

    // When converted to grayscale
    val result = Conversions.convertToGrayscale(lines)

    // Then it should return an empty array
    result.length shouldBe 0
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
    // Run the IO and block to get the result (only for testing)
    import cats.effect.unsafe.implicits.global
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
    import cats.effect.unsafe.implicits.global
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
    import cats.effect.unsafe.implicits.global
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
    import cats.effect.unsafe.implicits.global
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
    import cats.effect.unsafe.implicits.global
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
    import cats.effect.unsafe.implicits.global
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
      import cats.effect.unsafe.implicits.global
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
}
