package equality

import org.scalatest.FreeSpec
import equality.all._
import equality.util.TestHelper._

class ProductSpec extends FreeSpec {

  "Eq on a product data type" - {
    "returns Equal when both instances are equal" in {
      val dog    = Dog("Max", 10)
      val result = dog ==== dog
      assert(result === Named("Dog", List(
        "name" -> Primitive("String", isEqual = true, content = "Max"),
        "age" -> Primitive("Integer", isEqual = true, content = "10"),
      )))
    }

    "returns NotEqual when both instances are not equal" in {
      val dog0   = Dog("Max", 10)
      val dog1   = Dog("Bella", 8)
      val result = dog0 ==== dog1
      assert(result === Named("Dog", List(
        "name" -> Primitive("String", isEqual = false, content = "Max not equal to Bella"),
        "age" -> Primitive("Integer", isEqual = false, content = "10 not equal to 8"),
      )))
    }

    "resolve an instance for all primitive types" in {
      AllPrimitives(1.0, 1.0f, 1L, 1, 1, 0x00, (), true, 'a')
    }
  }
}
