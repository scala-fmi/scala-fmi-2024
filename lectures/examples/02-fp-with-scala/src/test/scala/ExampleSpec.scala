import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ExampleSpec extends AnyFlatSpec with Matchers:
  "+" should "sum two numbers" in {
    2 + 3 shouldBe 5
  }

  "+" should "sum two numbers 2" in {
    2 + 3 shouldBe 5
  }
