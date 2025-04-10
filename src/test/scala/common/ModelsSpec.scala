package common

import com.streetascii.common.Errors._
import com.streetascii.common.Models._
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ModelsSpec extends AnyFlatSpec with Matchers with EitherValues {
  "Coordinates" should "be created with valid latitude and longitude" in {
    val result = Coordinates.create(45.0, 90.0)
    result.isRight shouldBe true

    val coordinates = result.value
    coordinates.lat shouldBe 45.0
    coordinates.lng shouldBe 90.0
  }

  it should "accept boundary values for latitude" in {
    Coordinates.create(-90.0, 0.0).isRight shouldBe true
    Coordinates.create(90.0, 0.0).isRight shouldBe true
  }

  it should "accept boundary values for longitude" in {
    Coordinates.create(0.0, -180.0).isRight shouldBe true
    Coordinates.create(0.0, 180.0).isRight shouldBe true
  }

  it should "reject latitudes less than -90" in {
    val result = Coordinates.create(-90.1, 0.0)
    result.isLeft shouldBe true
    result.left.value shouldBe a[LatitudeOutOfRangeError]
    result.left.value
      .asInstanceOf[LatitudeOutOfRangeError]
      .value shouldBe -90.1
  }

  it should "reject latitudes greater than 90" in {
    val result = Coordinates.create(90.1, 0.0)
    result.isLeft shouldBe true
    result.left.value shouldBe a[LatitudeOutOfRangeError]
    result.left.value
      .asInstanceOf[LatitudeOutOfRangeError]
      .value shouldBe 90.1
  }

  it should "reject longitudes less than -180" in {
    val result = Coordinates.create(0.0, -180.1)
    result.isLeft shouldBe true
    result.left.value shouldBe a[LongitudeOutOfRangeError]
    result.left.value
      .asInstanceOf[LongitudeOutOfRangeError]
      .value shouldBe -180.1
  }

  it should "reject longitudes greater than 180" in {
    val result = Coordinates.create(0.0, 180.1)
    result.isLeft shouldBe true
    result.left.value shouldBe a[LongitudeOutOfRangeError]
    result.left.value
      .asInstanceOf[LongitudeOutOfRangeError]
      .value shouldBe 180.1
  }

  it should "create instances without validation when using unsafeCreate" in {
    // Note: This is testing the API, not necessarily recommending this usage
    val coordinates = Coordinates.unsafeCreate(100.0, 200.0)
    coordinates.lat shouldBe 100.0
    coordinates.lng shouldBe 200.0
  }

  "Radius" should "be created with a valid positive value" in {
    val result = Radius.create(100)
    result.isRight shouldBe true

    val radius = result.value
    radius.value shouldBe 100
  }

  it should "accept zero as a valid radius" in {
    val result = Radius.create(0)
    result.isRight shouldBe true
    result.value.value shouldBe 0
  }

  it should "reject negative values" in {
    val result = Radius.create(-1)
    result.isLeft shouldBe true
    result.left.value shouldBe a[NegativeRadiusError]
    result.left.value.asInstanceOf[NegativeRadiusError].value shouldBe -1
  }

  it should "create instances without validation when using unsafeCreate" in {
    // Again, testing the API, not recommending this usage
    val radius = Radius.unsafeCreate(-5)
    radius.value shouldBe -5
  }
}
