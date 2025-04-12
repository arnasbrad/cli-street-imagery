package navigation

import cats.data.EitherT
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.streetascii.clients.mapillary.Errors.MapillaryError
import com.streetascii.clients.mapillary.Models.{
  ImageData,
  ImagesResponse,
  MapillaryImageId,
  MapillarySequenceId,
  RequestField,
  SequenceImagesResponse
}
import com.streetascii.clients.mapillary.MapillaryClient
import com.streetascii.navigation.Navigation
import com.streetascii.common.Models.{Coordinates, Radius}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NavigationSpec extends AnyFlatSpec with Matchers with MockFactory {

  "Navigation.findNearbyImages" should "return images sorted by distance" in {
    // Setup test data
    val currentImageId = MapillaryImageId("current-id")
    val currentCoords  = Coordinates.unsafeCreate(51.5074, -0.1278) // London
    val radius         = Radius.unsafeCreate(100)

    // Create mock image data
    val nearbyImage1 = ImageData(
      id = MapillaryImageId("nearby-1"),
      coordinates =
        Coordinates.unsafeCreate(51.5075, -0.1279) // Very close to current
    )

    val nearbyImage2 = ImageData(
      id = MapillaryImageId("nearby-2"),
      coordinates =
        Coordinates.unsafeCreate(51.5080, -0.1280) // Farther from current
    )

    val nearbyImage3 = ImageData(
      id = MapillaryImageId("nearby-3"),
      coordinates = Coordinates.unsafeCreate(
        51.5076,
        -0.1277
      ) // Medium distance from current
    )

    // Create the current image data (should be filtered out)
    val currentImage = ImageData(
      id = currentImageId,
      coordinates = currentCoords
    )

    // Setup mock response
    val mockResponse = ImagesResponse(
      data = List(currentImage, nearbyImage1, nearbyImage2, nearbyImage3)
    )

    // Create the mock client
    val mockClient = mock[MapillaryClient]
    (mockClient
      .getImagesInfoByLocation(
        _: Coordinates,
        _: Radius,
        _: List[RequestField]
      ))
      .expects(currentCoords, radius, *)
      .returning(EitherT.rightT[IO, MapillaryError](mockResponse))

    // Call the method under test
    val result = Navigation
      .findNearbyImages(
        currentImageId,
        currentCoords,
        radius,
        maxAmount = 3
      )(mockClient)
      .value
      .unsafeRunSync()

    // Assertions
    result.isRight shouldBe true
    val images = result.getOrElse(fail("Expected Right but got Left"))

    // Should exclude the current image
    images.map(_.id) should not contain currentImageId

    // Should return images sorted by distance (closest first)
    images.length shouldBe 3
    images.head.id shouldBe nearbyImage1.id // Closest
    images(1).id shouldBe nearbyImage3.id   // Medium distance
    images(2).id shouldBe nearbyImage2.id   // Farthest
  }

  it should "limit the number of results to maxAmount" in {
    // Setup test data
    val currentImageId = MapillaryImageId("current-id")
    val currentCoords  = Coordinates.unsafeCreate(51.5074, -0.1278) // London
    val radius         = Radius.unsafeCreate(100)

    // Create test image data with 5 nearby images
    val nearbyImages = (1 to 5).map { i =>
      // Create images at increasing distances
      ImageData(
        id = MapillaryImageId(s"nearby-$i"),
        coordinates = Coordinates.unsafeCreate(
          51.5074 + (i * 0.0001),
          -0.1278 + (i * 0.0001)
        )
      )
    }.toList

    // Setup mock response
    val mockResponse = ImagesResponse(data = nearbyImages)

    // Create the mock client
    val mockClient = mock[MapillaryClient]
    (mockClient
      .getImagesInfoByLocation(
        _: Coordinates,
        _: Radius,
        _: List[RequestField]
      ))
      .expects(currentCoords, radius, *)
      .returning(EitherT.rightT[IO, MapillaryError](mockResponse))

    // Call the method with maxAmount = 2
    val result = Navigation
      .findNearbyImages(
        currentImageId,
        currentCoords,
        radius,
        maxAmount = 2
      )(mockClient)
      .value
      .unsafeRunSync()

    // Assertions
    result.isRight shouldBe true
    val images = result.getOrElse(fail("Expected Right but got Left"))

    // Should limit to 2 images
    images.length shouldBe 2

    // Should be the closest 2 images
    images.map(_.id) should contain theSameElementsAs List(
      MapillaryImageId("nearby-1"),
      MapillaryImageId("nearby-2")
    )
  }

  it should "return an empty list when no images are found" in {
    val currentImageId = MapillaryImageId("current-id")
    val currentCoords  = Coordinates.unsafeCreate(51.5074, -0.1278)
    val radius         = Radius.unsafeCreate(100)

    // Only the current image in the response
    val currentImage = ImageData(
      id = currentImageId,
      coordinates = currentCoords
    )

    // Setup mock response
    val mockResponse = ImagesResponse(data = List(currentImage))

    // Create the mock client
    val mockClient = mock[MapillaryClient]
    (mockClient
      .getImagesInfoByLocation(
        _: Coordinates,
        _: Radius,
        _: List[RequestField]
      ))
      .expects(currentCoords, radius, *)
      .returning(EitherT.rightT[IO, MapillaryError](mockResponse))

    val result = Navigation
      .findNearbyImages(
        currentImageId,
        currentCoords,
        radius,
        maxAmount = 3
      )(mockClient)
      .value
      .unsafeRunSync()

    result.isRight shouldBe true
    val images = result.getOrElse(fail("Expected Right but got Left"))

    // Should be empty because the only image was filtered out
    images shouldBe empty
  }

  it should "handle when no images match the current ID" in {
    val currentImageId = MapillaryImageId("current-id")
    val currentCoords  = Coordinates.unsafeCreate(51.5074, -0.1278)
    val radius         = Radius.unsafeCreate(100)

    // Images with different IDs
    val otherImages = (1 to 3).map { i =>
      ImageData(
        id = MapillaryImageId(s"other-$i"),
        coordinates = Coordinates.unsafeCreate(
          51.5074 + (i * 0.0001),
          -0.1278 + (i * 0.0001)
        )
      )
    }.toList

    // Setup mock response
    val mockResponse = ImagesResponse(data = otherImages)

    // Create the mock client
    val mockClient = mock[MapillaryClient]
    (mockClient
      .getImagesInfoByLocation(
        _: Coordinates,
        _: Radius,
        _: List[RequestField]
      ))
      .expects(currentCoords, radius, *)
      .returning(EitherT.rightT[IO, MapillaryError](mockResponse))

    val result = Navigation
      .findNearbyImages(
        currentImageId,
        currentCoords,
        radius,
        maxAmount = 3
      )(mockClient)
      .value
      .unsafeRunSync()

    result.isRight shouldBe true
    val images = result.getOrElse(fail("Expected Right but got Left"))

    // Should return all the images, sorted by distance
    images.length shouldBe 3
    images.map(_.id) should contain theSameElementsInOrderAs List(
      MapillaryImageId("other-1"),
      MapillaryImageId("other-2"),
      MapillaryImageId("other-3")
    )
  }

  it should "handle API errors properly" in {
    val currentImageId = MapillaryImageId("current-id")
    val currentCoords  = Coordinates.unsafeCreate(51.5074, -0.1278)
    val radius         = Radius.unsafeCreate(100)

    // Create a mock API error
    val apiError = MapillaryError.NetworkError("Connection timeout")

    // Create the mock client that returns an error
    val mockClient = mock[MapillaryClient]
    (mockClient
      .getImagesInfoByLocation(
        _: Coordinates,
        _: Radius,
        _: List[RequestField]
      ))
      .expects(currentCoords, radius, *)
      .returning(EitherT.leftT[IO, ImagesResponse](apiError))

    val result = Navigation
      .findNearbyImages(
        currentImageId,
        currentCoords,
        radius,
        maxAmount = 3
      )(mockClient)
      .value
      .unsafeRunSync()

    result.isLeft shouldBe true
    val error = result.left.getOrElse(fail("Expected Left but got Right"))
    error shouldBe apiError
  }

  // New tests for sequence-based navigation
  "Navigation.findSequenceNeighbors" should "return previous and next images in a sequence" in {
    val currentImageId = MapillaryImageId("image-2")
    val sequenceId     = MapillarySequenceId("sequence-123")

    // Create a sequence with 5 images
    val sequenceImages = List(
      MapillaryImageId("image-1"),
      MapillaryImageId("image-2"), // Current image
      MapillaryImageId("image-3"),
      MapillaryImageId("image-4"),
      MapillaryImageId("image-5")
    )

    // Create the mock client
    val mockClient = mock[MapillaryClient]
    (mockClient
      .getImageIdsBySequence(
        _: MapillarySequenceId
      ))
      .expects(sequenceId)
      .returning(EitherT.rightT[IO, MapillaryError](sequenceImages))

    // Call the method under test
    val result = Navigation
      .findSequenceNeighbors(
        currentImageId,
        sequenceId
      )(mockClient)
      .value
      .unsafeRunSync()

    // Assertions
    result.isRight shouldBe true
    val (prev, next) = result.getOrElse(fail("Expected Right but got Left"))

    prev shouldBe Some(MapillaryImageId("image-1"))
    next shouldBe Some(MapillaryImageId("image-3"))
  }

  it should "handle the case when current image is at the start of sequence" in {
    val currentImageId = MapillaryImageId("image-1") // First image
    val sequenceId     = MapillarySequenceId("sequence-123")

    // Create a sequence with 3 images
    val sequenceImages = List(
      MapillaryImageId("image-1"), // Current image (first)
      MapillaryImageId("image-2"),
      MapillaryImageId("image-3")
    )

    // Create the mock client
    val mockClient = mock[MapillaryClient]
    (mockClient
      .getImageIdsBySequence(
        _: MapillarySequenceId
      ))
      .expects(sequenceId)
      .returning(EitherT.rightT[IO, MapillaryError](sequenceImages))

    // Call the method under test
    val result = Navigation
      .findSequenceNeighbors(
        currentImageId,
        sequenceId
      )(mockClient)
      .value
      .unsafeRunSync()

    // Assertions
    result.isRight shouldBe true
    val (prev, next) = result.getOrElse(fail("Expected Right but got Left"))

    prev shouldBe None // No previous image
    next shouldBe Some(MapillaryImageId("image-2"))
  }

  it should "handle the case when current image is at the end of sequence" in {
    val currentImageId = MapillaryImageId("image-3") // Last image
    val sequenceId     = MapillarySequenceId("sequence-123")

    // Create a sequence with 3 images
    val sequenceImages = List(
      MapillaryImageId("image-1"),
      MapillaryImageId("image-2"),
      MapillaryImageId("image-3") // Current image (last)
    )

    // Create the mock client
    val mockClient = mock[MapillaryClient]
    (mockClient
      .getImageIdsBySequence(
        _: MapillarySequenceId
      ))
      .expects(sequenceId)
      .returning(EitherT.rightT[IO, MapillaryError](sequenceImages))

    // Call the method under test
    val result = Navigation
      .findSequenceNeighbors(
        currentImageId,
        sequenceId
      )(mockClient)
      .value
      .unsafeRunSync()

    // Assertions
    result.isRight shouldBe true
    val (prev, next) = result.getOrElse(fail("Expected Right but got Left"))

    prev shouldBe Some(MapillaryImageId("image-2"))
    next shouldBe None // No next image
  }

  it should "handle the case when current image is not in the sequence" in {
    val currentImageId = MapillaryImageId("image-not-in-sequence")
    val sequenceId     = MapillarySequenceId("sequence-123")

    // Create a sequence without the current image
    val sequenceImages = List(
      MapillaryImageId("image-1"),
      MapillaryImageId("image-2"),
      MapillaryImageId("image-3")
    )

    // Create the mock client
    val mockClient = mock[MapillaryClient]
    (mockClient
      .getImageIdsBySequence(
        _: MapillarySequenceId
      ))
      .expects(sequenceId)
      .returning(EitherT.rightT[IO, MapillaryError](sequenceImages))

    // Call the method under test
    val result = Navigation
      .findSequenceNeighbors(
        currentImageId,
        sequenceId
      )(mockClient)
      .value
      .unsafeRunSync()

    // Assertions
    result.isRight shouldBe true
    val (prev, next) = result.getOrElse(fail("Expected Right but got Left"))

    prev shouldBe None
    next shouldBe None
  }

  it should "handle empty sequences" in {
    val currentImageId = MapillaryImageId("image-1")
    val sequenceId     = MapillarySequenceId("empty-sequence")

    // Empty sequence
    val sequenceImages = List.empty[MapillaryImageId]

    // Create the mock client
    val mockClient = mock[MapillaryClient]
    (mockClient
      .getImageIdsBySequence(
        _: MapillarySequenceId
      ))
      .expects(sequenceId)
      .returning(EitherT.rightT[IO, MapillaryError](sequenceImages))

    // Call the method under test
    val result = Navigation
      .findSequenceNeighbors(
        currentImageId,
        sequenceId
      )(mockClient)
      .value
      .unsafeRunSync()

    // Assertions
    result.isRight shouldBe true
    val (prev, next) = result.getOrElse(fail("Expected Right but got Left"))

    prev shouldBe None
    next shouldBe None
  }

  it should "handle API errors properly" in {
    val currentImageId = MapillaryImageId("image-1")
    val sequenceId     = MapillarySequenceId("sequence-123")

    // Create a mock API error
    val apiError = MapillaryError.NetworkError("Connection timeout")

    // Create the mock client that returns an error
    val mockClient = mock[MapillaryClient]
    (mockClient
      .getImageIdsBySequence(
        _: MapillarySequenceId
      ))
      .expects(sequenceId)
      .returning(EitherT.leftT[IO, List[MapillaryImageId]](apiError))

    // Call the method under test
    val result = Navigation
      .findSequenceNeighbors(
        currentImageId,
        sequenceId
      )(mockClient)
      .value
      .unsafeRunSync()

    // Assertions
    result.isLeft shouldBe true
    val error = result.left.getOrElse(fail("Expected Left but got Right"))
    error shouldBe apiError
  }

  // Helper function test
  "Navigation.listNeighbors" should "find previous and next elements in a list" in {
    val list = List(
      MapillaryImageId("image-1"),
      MapillaryImageId("image-2"),
      MapillaryImageId("image-3"),
      MapillaryImageId("image-4"),
      MapillaryImageId("image-5")
    )

    // Test middle element
    val (prevMid, nextMid) =
      Navigation.listNeighbors(list, MapillaryImageId("image-3"))
    prevMid shouldBe Some(MapillaryImageId("image-2"))
    nextMid shouldBe Some(MapillaryImageId("image-4"))

    // Test first element
    val (prevFirst, nextFirst) =
      Navigation.listNeighbors(list, MapillaryImageId("image-1"))
    prevFirst shouldBe None
    nextFirst shouldBe Some(MapillaryImageId("image-2"))

    // Test last element
    val (prevLast, nextLast) =
      Navigation.listNeighbors(list, MapillaryImageId("image-5"))
    prevLast shouldBe Some(MapillaryImageId("image-4"))
    nextLast shouldBe None

    // Test element not in list
    val (prevNone, nextNone) =
      Navigation.listNeighbors(list, MapillaryImageId("not-in-list"))
    prevNone shouldBe None
    nextNone shouldBe None

    // Test empty list
    val (prevEmpty, nextEmpty) =
      Navigation.listNeighbors(List.empty, MapillaryImageId("image-1"))
    prevEmpty shouldBe None
    nextEmpty shouldBe None
  }

  // This test verifies the distance calculation logic
  "Navigation.calculateDistance" should "correctly calculate distances between coordinates" in {
    // Test with known coordinates and distances
    val london = Coordinates.unsafeCreate(51.5074, -0.1278)
    val paris  = Coordinates.unsafeCreate(48.8566, 2.3522)

    // Invoke the method
    val distance = Navigation.calculateDistance(
      london,
      paris
    )

    // The distance between London and Paris should be approximately 344 km
    (distance / 1000).round shouldBe 344L
  }
}
