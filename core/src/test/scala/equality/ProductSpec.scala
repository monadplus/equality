package equality

import org.scalatest.FreeSpec
import equality.all._

class ProductSpec extends FreeSpec {

  case class AllPrimitives(
    x: Double,
    x1: Float,
    x2: Long,
    x3: Int,
    x4: Short,
    x5: Byte,
    x6: Unit,
    x7: Boolean,
    x8: Char
  )

  case class Dog(name: String, age: Int)

  "Eq on a product data type" - {
    "returns Equal when both instances are equal" in {
      val dog    = Dog("Max", 10)
      val result = dog ==== dog
      assert(result === Named("Dog", List(
        "name" -> Primitive("String", isEqual = true),
        "age" -> Primitive("Integer", isEqual = true),
      )))
    }

    "returns NotEqual when both instances are not equal" in {
      val dog0   = Dog("Max", 10)
      val dog1   = Dog("Bella", 8)
      val result = dog0 ==== dog1
      assert(result === Named("Dog", List(
        "name" -> Primitive("String", isEqual = false, Some("Max not equal to Bella")),
        "age" -> Primitive("Integer", isEqual = false, Some("10 not equal to 8")),
      )))
    }

    "resolve an instance for all primitive types" in {
      AllPrimitives(1.0, 1.0f, 1L, 1, 1, 0x00, (), true, 'a')
    }
  }
}