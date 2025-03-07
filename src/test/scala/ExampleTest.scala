import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ExampleTest extends AnyFlatSpec with Matchers {
  "something" should "do shit" in {
    3 shouldEqual 3
  }

  it should "do shit againn" in {
    3 shouldEqual 3
  }
}
