package runner

import cats.data.EitherT
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.streetascii.asciiart.Conversions
import com.streetascii.asciiart.Models.{
  HexImage,
  ImageHeight,
  ImageInfo,
  ImageWidth
}
import com.streetascii.clients.imgur
import com.streetascii.clients.imgur.ImgurClient
import com.streetascii.clients.imgur.Models.ClientId
import com.streetascii.clients.mapillary.Models._
import com.streetascii.clients.mapillary.{MapillaryClient, Models}
import com.streetascii.common.Models.{Coordinates, Radius}
import com.streetascii.runner.RunnerImpl
import com.streetascii.socialmedia.SocialMedia
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RunnerSpec extends AnyFlatSpec with Matchers with MockFactory {
  "Runner.getHexStringsFromLocation" should "return imageInfo when images are found" in {
    // Setup test data
    val coordinates = Coordinates.unsafeCreate(51.5074, -0.1278) // London
    val radius      = Radius.unsafeCreate(3)

    val imageId    = MapillaryImageId("test-image-id")
    val sequenceId = MapillarySequenceId("test-sequence-id")
    val imageData = ImageData(
      id = imageId,
      coordinates = coordinates,
      compassAngle = 90.5
    )

    val imagesResponse = ImagesResponse(data = List(imageData))
    val imageByteArray = Array[Byte](1, 2, 3, 4)
    val testWidth      = ImageWidth(10)
    val testHeight     = ImageHeight(5)

    // Create an array of hex strings (one per row)
    // Each string represents a row of hex characters
    val hexStrings = Array(
      "0123456789",
      "ABCDEFGHIJ",
      "KLMNOPQRST",
      "UVWXYZ0123",
      "4567890ABC"
    )

    // Create the HexImage instance
    val hexImage = HexImage(hexStrings, testWidth, testHeight)
    val imageInfo =
      ImageInfo(
        hexImage,
        imageId,
        90.5,
        sequenceId,
        Coordinates.unsafeCreate(50, 50)
      )

    // Create mocks
    val mockMapillaryClient = mock[MapillaryClient]
    val mockImgurClient     = mock[ImgurClient]

    // Setup expectations
    val mockConversions = mock[Conversions]
    (mockConversions.convertBytesToHexImage _)
      .expects(imageByteArray)
      .returning(IO.pure(hexImage))

    (mockMapillaryClient
      .getImagesInfoByLocation(
        _: Coordinates,
        _: Radius,
        _: List[Models.RequestField]
      ))
      .expects(coordinates, radius, *)
      .returning(EitherT.rightT(imagesResponse))

    (mockMapillaryClient
      .getImage(
        _: MapillaryImageId,
        _: List[Models.RequestField]
      ))
      .expects(imageId, *)
      .returning(
        EitherT.rightT(
          imageByteArray,
          MapillaryImageDetails(
            id = imageId,
            sequenceId = sequenceId,
            coordinates = Coordinates.unsafeCreate(50, 50),
            compassAngle = 90.5,
            thumb1024Url = Some("url"),
            thumbOriginalUrl = Some("url")
          )
        )
      )

    // Mock the conversion function to return our test hex image
    // Create an instance of Runner with mocked dependencies
    val runner =
      RunnerImpl(mockMapillaryClient, mockImgurClient, mockConversions)

    // Call the method under test
    val result = runner
      .getHexStringsFromLocation(coordinates, radius)
      .value
      .unsafeRunSync()

    // Assertions
    result.isRight shouldBe true
    result.getOrElse(fail("Expected Right but got Left")) shouldBe imageInfo
  }

  "Runner.generateSocialMediaLinks" should "return social media links with the uploaded image URL" in {
    // Setup test data
    val text       = "Check out this cool image!"
    val imageBytes = Array[Byte](1, 2, 3, 4)

    val imgurLink = "https://imgur.com/test-image"
    val imgurData = imgur.Models.ImgurData(
      link = imgurLink,
      deletehash = "test-delete-hash",
      id = "test-id"
    )
    val imgurResponse = imgur.Models.ImgurResponse(
      data = imgurData,
      success = true,
      status = 200
    )

    // Create mocks
    val mockMapillaryClient = mock[MapillaryClient]
    val mockImgurClient     = mock[ImgurClient]

    // Create a mock response for the Imgur upload
    // Setup expectations
    (mockImgurClient
      .uploadImage(_: Array[Byte]))
      .expects(imageBytes)
      .returning(EitherT.rightT(imgurResponse))

    // Create an instance of Runner with mocked dependencies
    val runner = RunnerImpl(mockMapillaryClient, mockImgurClient)

    // Call the method under test
    val result =
      runner
        .generateSocialMediaLinks(imageBytes)
        .value
        .unsafeRunSync()

    // Assertions
    result.isRight shouldBe true
    val links = result.getOrElse(fail("Expected Right but got Left"))

    links.length shouldBe 2

    // First link should be X (Twitter)
    links.head shouldBe a[SocialMedia.X]
    val xLink = links.head.asInstanceOf[SocialMedia.X]
    xLink.text shouldBe text
    xLink.imgurLink shouldBe imgurLink

    // Second link should be Facebook
    links(1) shouldBe a[SocialMedia.FaceBook]
    val fbLink = links(1).asInstanceOf[SocialMedia.FaceBook]
    fbLink.imgurLink shouldBe imgurLink
  }
}
