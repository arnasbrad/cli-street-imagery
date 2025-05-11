package appconfig

import cats.effect.unsafe.implicits.global
import com.streetascii.AppConfig
import com.streetascii.asciiart.Algorithms.{
  BlankFilledAlgorithm,
  BrailleAlgorithm,
  EdgeDetectionCannyAlgorithm,
  EdgeDetectionSobelAlgorithm,
  LuminanceAlgorithm
}
import com.streetascii.asciiart.Charset
import com.streetascii.clients.imgur.Models.ClientId
import com.streetascii.clients.mapillary.Models.ApiKey
import com.streetascii.clients.traveltime.Models.{
  ApiKey => TravelTimeApiKey,
  AppId => TravelTimeAppId
}
import com.streetascii.colorfilters.ColorFilter
import com.streetascii.navigation.Models.NavigationType
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource

import java.io.{File, FileWriter}

class AppConfigSpec extends AnyFlatSpec with Matchers {

  "AppConfig.load" should "successfully load a valid configuration" in {
    // Given a valid config string
    val mockConfig = ConfigSource.string("""
        |api {
        |  mapillary-key = "test-mapillary-key"
        |  imgur-client-id = "test-imgur-client-id"
        |  traveltime-app-id = "test-traveltime-app-id"
        |  traveltime-key = "test-traveltime-key"
        |}
        |processing {
        |  navigation-type = "Proximity Navigation"
        |  algorithm = "Luminance"
        |  charset = "Default"
        |  down-sampling-rate = 2
        |}
        |colors {
        |  color = true
        |  color-filter = "Contrast"
        |}
        |""".stripMargin)

    // When loading the config
    val config = AppConfig.load(mockConfig).unsafeRunSync()

    // Then the config should be correctly parsed
    config.api.mapillaryKey shouldBe ApiKey("test-mapillary-key")
    config.api.imgurClientId shouldBe Some(ClientId("test-imgur-client-id"))
    config.api.traveltimeAppId shouldBe Some(
      TravelTimeAppId("test-traveltime-app-id")
    )
    config.api.traveltimeKey shouldBe Some(
      TravelTimeApiKey("test-traveltime-key")
    )
    config.processing.navigationType shouldBe NavigationType.RadiusBased
    config.processing.algorithm shouldBe LuminanceAlgorithm
    config.processing.charset shouldBe Charset.Default
    config.processing.downSamplingRate shouldBe 2
    config.colors.color shouldBe true
    config.colors.colorFilter shouldBe ColorFilter.EnhancedContrast
  }

  it should "handle disabled optional values correctly" in {
    // Given a config with disabled optional values
    val mockConfig = ConfigSource.string("""
        |api {
        |  mapillary-key = "test-mapillary-key"
        |  imgur-client-id = "Disabled"
        |  traveltime-app-id = "Disabled"
        |  traveltime-key = "Disabled"
        |}
        |processing {
        |  navigation-type = "Proximity Navigation"
        |  algorithm = "Luminance"
        |  charset = "Default"
        |  down-sampling-rate = 2
        |}
        |colors {
        |  color = false
        |  color-filter = "No Filter"
        |}
        |""".stripMargin)

    // When loading the config
    val config = AppConfig.load(mockConfig).unsafeRunSync()

    // Then optional values should be None
    config.api.imgurClientId shouldBe None
    config.api.traveltimeAppId shouldBe None
    config.api.traveltimeKey shouldBe None
    config.colors.color shouldBe false
    config.colors.colorFilter shouldBe ColorFilter.NoFilter
  }

  it should "correctly parse each navigation type" in {
    // Test Sequence Navigation
    val seqNavConfig = ConfigSource.string("""
        |api {
        |  mapillary-key = "test-key"
        |  imgur-client-id = "Disabled"
        |  traveltime-app-id = "Disabled"
        |  traveltime-key = "Disabled"
        |}
        |processing {
        |  navigation-type = "Sequence Navigation"
        |  algorithm = "Luminance"
        |  charset = "Default"
        |  down-sampling-rate = 2
        |}
        |colors {
        |  color = true
        |  color-filter = "No Filter"
        |}
        |""".stripMargin)

    val seqNav = AppConfig.load(seqNavConfig).unsafeRunSync()
    seqNav.processing.navigationType shouldBe NavigationType.SequenceBased

    // Test Sequence Fast Navigation
    val seqFastNavConfig = ConfigSource.string("""
        |api {
        |  mapillary-key = "test-key"
        |  imgur-client-id = "Disabled"
        |  traveltime-app-id = "Disabled"
        |  traveltime-key = "Disabled"
        |}
        |processing {
        |  navigation-type = "Sequence Fast Navigation"
        |  algorithm = "Luminance"
        |  charset = "Default"
        |  down-sampling-rate = 2
        |}
        |colors {
        |  color = true
        |  color-filter = "No Filter"
        |}
        |""".stripMargin)

    val seqFastNav = AppConfig.load(seqFastNavConfig).unsafeRunSync()
    seqFastNav.processing.navigationType shouldBe NavigationType.SequenceBasedFast

    // Test Proximity Navigation
    val proximityNavConfig = ConfigSource.string("""
        |api {
        |  mapillary-key = "test-key"
        |  imgur-client-id = "Disabled"
        |  traveltime-app-id = "Disabled"
        |  traveltime-key = "Disabled"
        |}
        |processing {
        |  navigation-type = "Proximity Navigation"
        |  algorithm = "Luminance"
        |  charset = "Default"
        |  down-sampling-rate = 2
        |}
        |colors {
        |  color = true
        |  color-filter = "No Filter"
        |}
        |""".stripMargin)

    val proximityNav = AppConfig.load(proximityNavConfig).unsafeRunSync()
    proximityNav.processing.navigationType shouldBe NavigationType.RadiusBased
  }

  it should "correctly parse each algorithm type" in {
    // Test Luminance algorithm
    val luminanceConfig = ConfigSource.string("""
        |api {
        |  mapillary-key = "test-key"
        |  imgur-client-id = "Disabled"
        |  traveltime-app-id = "Disabled"
        |  traveltime-key = "Disabled"
        |}
        |processing {
        |  navigation-type = "Proximity Navigation"
        |  algorithm = "Luminance"
        |  charset = "Default"
        |  down-sampling-rate = 2
        |}
        |colors {
        |  color = true
        |  color-filter = "No Filter"
        |}
        |""".stripMargin)

    val luminance = AppConfig.load(luminanceConfig).unsafeRunSync()
    luminance.processing.algorithm shouldBe LuminanceAlgorithm

    // Test Edge Detection Sobel algorithm
    val sobelConfig = ConfigSource.string("""
        |api {
        |  mapillary-key = "test-key"
        |  imgur-client-id = "Disabled"
        |  traveltime-app-id = "Disabled"
        |  traveltime-key = "Disabled"
        |}
        |processing {
        |  navigation-type = "Proximity Navigation"
        |  algorithm = "Edge Detection Sobel"
        |  charset = "Default"
        |  down-sampling-rate = 2
        |}
        |colors {
        |  color = true
        |  color-filter = "No Filter"
        |}
        |""".stripMargin)

    val sobel = AppConfig.load(sobelConfig).unsafeRunSync()
    sobel.processing.algorithm shouldBe EdgeDetectionSobelAlgorithm

    // Test Edge Detection Canny algorithm
    val cannyConfig = ConfigSource.string("""
        |api {
        |  mapillary-key = "test-key"
        |  imgur-client-id = "Disabled"
        |  traveltime-app-id = "Disabled"
        |  traveltime-key = "Disabled"
        |}
        |processing {
        |  navigation-type = "Proximity Navigation"
        |  algorithm = "Edge Detection Canny"
        |  charset = "Default"
        |  down-sampling-rate = 2
        |}
        |colors {
        |  color = true
        |  color-filter = "No Filter"
        |}
        |""".stripMargin)

    val canny = AppConfig.load(cannyConfig).unsafeRunSync()
    canny.processing.algorithm shouldBe EdgeDetectionCannyAlgorithm

    // Test Braille algorithm
    val brailleConfig = ConfigSource.string("""
        |api {
        |  mapillary-key = "test-key"
        |  imgur-client-id = "Disabled"
        |  traveltime-app-id = "Disabled"
        |  traveltime-key = "Disabled"
        |}
        |processing {
        |  navigation-type = "Proximity Navigation"
        |  algorithm = "Braille"
        |  charset = "Default"
        |  down-sampling-rate = 2
        |}
        |colors {
        |  color = true
        |  color-filter = "No Filter"
        |}
        |""".stripMargin)

    val braille = AppConfig.load(brailleConfig).unsafeRunSync()
    braille.processing.algorithm shouldBe BrailleAlgorithm

    // Test No Algorithm
    val noAlgConfig = ConfigSource.string("""
        |api {
        |  mapillary-key = "test-key"
        |  imgur-client-id = "Disabled"
        |  traveltime-app-id = "Disabled"
        |  traveltime-key = "Disabled"
        |}
        |processing {
        |  navigation-type = "Proximity Navigation"
        |  algorithm = "No Algorithm"
        |  charset = "Default"
        |  down-sampling-rate = 2
        |}
        |colors {
        |  color = true
        |  color-filter = "No Filter"
        |}
        |""".stripMargin)

    val noAlg = AppConfig.load(noAlgConfig).unsafeRunSync()
    noAlg.processing.algorithm shouldBe BlankFilledAlgorithm
  }

  it should "correctly calculate verticalSampling based on algorithm" in {
    // For BrailleAlgorithm, verticalSampling should equal downSamplingRate
    val brailleConfig = ConfigSource.string("""
        |api {
        |  mapillary-key = "test-key"
        |  imgur-client-id = "Disabled"
        |  traveltime-app-id = "Disabled"
        |  traveltime-key = "Disabled"
        |}
        |processing {
        |  navigation-type = "Proximity Navigation"
        |  algorithm = "Braille"
        |  charset = "Default"
        |  down-sampling-rate = 3
        |}
        |colors {
        |  color = true
        |  color-filter = "No Filter"
        |}
        |""".stripMargin)

    val braille = AppConfig.load(brailleConfig).unsafeRunSync()
    braille.processing.verticalSampling shouldBe 3 // Equal to downSamplingRate

    // For other algorithms, verticalSampling should be downSamplingRate * 2
    val luminanceConfig = ConfigSource.string("""
        |api {
        |  mapillary-key = "test-key"
        |  imgur-client-id = "Disabled"
        |  traveltime-app-id = "Disabled"
        |  traveltime-key = "Disabled"
        |}
        |processing {
        |  navigation-type = "Proximity Navigation"
        |  algorithm = "Luminance"
        |  charset = "Default"
        |  down-sampling-rate = 3
        |}
        |colors {
        |  color = true
        |  color-filter = "No Filter"
        |}
        |""".stripMargin)

    val luminance = AppConfig.load(luminanceConfig).unsafeRunSync()
    luminance.processing.verticalSampling shouldBe 6 // downSamplingRate * 2
  }

  it should "correctly parse each charset" in {
    def testCharset(charsetStr: String, expected: Charset): Unit = {
      val charsetConfig = ConfigSource.string(s"""
           |api {
           |  mapillary-key = "test-key"
           |  imgur-client-id = "Disabled"
           |  traveltime-app-id = "Disabled"
           |  traveltime-key = "Disabled"
           |}
           |processing {
           |  navigation-type = "Proximity Navigation"
           |  algorithm = "Luminance"
           |  charset = "$charsetStr"
           |  down-sampling-rate = 2
           |}
           |colors {
           |  color = true
           |  color-filter = "No Filter"
           |}
           |""".stripMargin)

      val config = AppConfig.load(charsetConfig).unsafeRunSync()
      config.processing.charset shouldBe expected
    }

    testCharset("Default", Charset.Default)
    testCharset("Extended", Charset.Extended)
    testCharset("Braille", Charset.Braille)
    testCharset("Blocks", Charset.Blocks)
    testCharset("Blocks Extended", Charset.BlocksExtended)
    testCharset("Blank", Charset.Blank)
    testCharset("Braille Patterns", Charset.BraillePatterns)
  }

  it should "correctly parse each color filter" in {
    def testColorFilter(filterStr: String, expected: ColorFilter): Unit = {
      val filterConfig = ConfigSource.string(s"""
           |api {
           |  mapillary-key = "test-key"
           |  imgur-client-id = "Disabled"
           |  traveltime-app-id = "Disabled"
           |  traveltime-key = "Disabled"
           |}
           |processing {
           |  navigation-type = "Proximity Navigation"
           |  algorithm = "Luminance"
           |  charset = "Default"
           |  down-sampling-rate = 2
           |}
           |colors {
           |  color = true
           |  color-filter = "$filterStr"
           |}
           |""".stripMargin)

      val config = AppConfig.load(filterConfig).unsafeRunSync()
      config.colors.colorFilter shouldBe expected
    }

    testColorFilter("Contrast", ColorFilter.EnhancedContrast)
    testColorFilter("Tritanopia", ColorFilter.Tritanopia)
    testColorFilter("Protanopia", ColorFilter.Protanopia)
    testColorFilter("Deuteranopia", ColorFilter.Deuteranopia)
    testColorFilter("No Filter", ColorFilter.NoFilter)
  }

  "AppConfig.loadFromPath" should "load configuration from a file path" in {
    // Create a temporary config file
    val tempFile = File.createTempFile("config", ".conf")
    tempFile.deleteOnExit()

    val configContent =
      """
        |api {
        |  mapillary-key = "test-mapillary-key"
        |  imgur-client-id = "test-imgur-client-id"
        |  traveltime-app-id = "test-traveltime-app-id"
        |  traveltime-key = "test-traveltime-key"
        |}
        |processing {
        |  navigation-type = "Proximity Navigation"
        |  algorithm = "Luminance"
        |  charset = "Default"
        |  down-sampling-rate = 2
        |}
        |colors {
        |  color = true
        |  color-filter = "Contrast"
        |}
        |""".stripMargin

    // Write config to the temporary file
    val writer = new FileWriter(tempFile)
    writer.write(configContent)
    writer.close()

    // When loading from the file path
    val config = AppConfig.loadFromPath(tempFile.getPath).unsafeRunSync()

    // Then it should parse correctly
    config.api.mapillaryKey shouldBe ApiKey("test-mapillary-key")
    config.api.imgurClientId shouldBe Some(ClientId("test-imgur-client-id"))
    config.processing.navigationType shouldBe NavigationType.RadiusBased
    config.colors.colorFilter shouldBe ColorFilter.EnhancedContrast
  }
}
