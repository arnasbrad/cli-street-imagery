package navigation

import cats.data.EitherT
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.streetascii.clients.mapillary.Errors.MapillaryError
import com.streetascii.clients.mapillary.MapillaryClient
import com.streetascii.clients.mapillary.Models._
import com.streetascii.common.Models.{Coordinates, Radius}
import com.streetascii.navigation.Navigation
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
        Coordinates.unsafeCreate(51.5075, -0.1279), // Very close to current
      compassAngle = 90.5
    )

    val nearbyImage2 = ImageData(
      id = MapillaryImageId("nearby-2"),
      coordinates =
        Coordinates.unsafeCreate(51.5080, -0.1280), // Farther from current
      compassAngle = 90.5
    )

    val nearbyImage3 = ImageData(
      id = MapillaryImageId("nearby-3"),
      coordinates = Coordinates.unsafeCreate(
        51.5076,
        -0.1277
      ), // Medium distance from current
      compassAngle = 90.5
    )

    // Create the current image data (should be filtered out)
    val currentImage = ImageData(
      id = currentImageId,
      coordinates = currentCoords,
      compassAngle = 90.5
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
        ),
        compassAngle = 90.5
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
      coordinates = currentCoords,
      compassAngle = 90.5
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
        ),
        compassAngle = 90.5
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

  "Navigation.calculateTurnAngle" should "correctly calculate turn angle between current position and target" in {
    // Test scenario: looking north (0 degrees), target is to the east
    val currentCoords = Coordinates.unsafeCreate(51.5074, -0.1278) // London
    val eastTarget =
      Coordinates.unsafeCreate(51.5074, -0.1268) // East of London
    val compassAngleNorth = 0.0 // Facing north

    val turnAngleToEast = Navigation.calculateTurnAngle(
      compassAngleNorth,
      currentCoords,
      eastTarget
    )

    // Should be approximately 90 degrees (turn right/east)
    turnAngleToEast should be(90.0 +- 5.0)

    // Test scenario: looking east (90 degrees), target is to the north
    val northTarget =
      Coordinates.unsafeCreate(51.5084, -0.1278) // North of London
    val compassAngleEast = 90.0 // Facing east

    val turnAngleToNorth = Navigation.calculateTurnAngle(
      compassAngleEast,
      currentCoords,
      northTarget
    )

    // Should be approximately -90 degrees (turn left/north)
    turnAngleToNorth should be(-90.0 +- 5.0)

    // Test scenario: looking south (180 degrees), target is to the south
    val southTarget =
      Coordinates.unsafeCreate(51.5064, -0.1278) // South of London
    val compassAngleSouth = 180.0 // Facing south

    val turnAngleToSouth = Navigation.calculateTurnAngle(
      compassAngleSouth,
      currentCoords,
      southTarget
    )

    // Should be approximately 0 degrees (no turn needed)
    turnAngleToSouth should be(0.0 +- 5.0)

    // Test scenario: looking west (270 degrees), target is to the east
    val compassAngleWest = 270.0 // Facing west

    val turnAngleWestToEast = Navigation.calculateTurnAngle(
      compassAngleWest,
      currentCoords,
      eastTarget
    )

    // Should be approximately 180 degrees (turn around)
    turnAngleWestToEast.abs should be(180.0 +- 5.0)
  }

  it should "handle angle normalization correctly" in {
    val currentCoords = Coordinates.unsafeCreate(51.5074, -0.1278) // London

    // Test with angle that needs to be normalized from > 180 to <= 180
    val targetCoords =
      Coordinates.unsafeCreate(51.5074, -0.1268) // East of London
    val compassAngle = 350.0 // Almost north, slightly west

    val turnAngle = Navigation.calculateTurnAngle(
      compassAngle,
      currentCoords,
      targetCoords
    )

    // The bearing to east is ~90, the compass is at 350
    // So unnormalized would be 90 - 350 = -260
    // Normalized should be 100 (add 360 to get within -180 to 180)
    turnAngle should be(100.0 +- 5.0)

    // Ensure the result is always in the range (-180, 180]
    turnAngle should be <= 180.0
    turnAngle should be > -180.0
  }

  it should "calculate correct angles for locations at different hemispheres" in {
    // Test with coordinates in different hemispheres
    val northernHemisphere =
      Coordinates.unsafeCreate(51.5074, -0.1278) // London
    val southernHemisphere =
      Coordinates.unsafeCreate(-33.9249, 18.4241) // Cape Town
    val compassAngle = 0.0 // Facing north

    val turnAngle = Navigation.calculateTurnAngle(
      compassAngle,
      northernHemisphere,
      southernHemisphere
    )

    // The result should be some angle that's normalized to (-180, 180]
    turnAngle should be <= 180.0
    turnAngle should be > -180.0
  }

  "Navigation.calculateBearing" should "correctly calculate bearing between two coordinates" in {
    // Since calculateBearing is a nested function, we can test it indirectly
    // by creating test cases with known bearings

    // London coordinates
    val london = Coordinates.unsafeCreate(51.5074, -0.1278)

    // Points in cardinal directions (approximately)
    val north = Coordinates.unsafeCreate(51.5174, -0.1278) // North of London
    val east  = Coordinates.unsafeCreate(51.5074, -0.1178) // East of London
    val south = Coordinates.unsafeCreate(51.4974, -0.1278) // South of London
    val west  = Coordinates.unsafeCreate(51.5074, -0.1378) // West of London

    // Test with compass at 0 (north)
    val compassAngle = 0.0

    val northBearing =
      Navigation.calculateTurnAngle(compassAngle, london, north)
    val eastBearing = Navigation.calculateTurnAngle(compassAngle, london, east)
    val southBearing =
      Navigation.calculateTurnAngle(compassAngle, london, south)
    val westBearing = Navigation.calculateTurnAngle(compassAngle, london, west)

    // With compass at 0, the turn angles should approximate the bearings
    northBearing should be(0.0 +- 5.0)   // No turn needed to go north
    eastBearing should be(90.0 +- 5.0)   // 90 degree turn right to go east
    southBearing should be(180.0 +- 5.0) // 180 degree turn to go south
    westBearing should be(-90.0 +- 5.0)  // 90 degree turn left to go west
  }
}
