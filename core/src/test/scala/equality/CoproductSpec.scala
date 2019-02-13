package equality

import equality.all._
import org.scalatest.FreeSpec

sealed trait Shape
final case class Circle(radius: Long)                 extends Shape
final case class Rectangle(width: Long, height: Long) extends Shape
case object Amorph                                    extends Shape

class CoproductSpec extends FreeSpec {

  "Eq on a coproduct data type" - {
    "returns Equal when both instances are equal" in {
      val circle: Shape = Circle(10)
      val result        = circle ==== circle
      assert(
        result === Coproduct(
          "Circle",
          Unnamed(List("radius" -> Primitive("Long", isEqual = true)))
        )
      )

      val result2 = (Amorph: Shape) ==== (Amorph: Shape)
      assert(result2 === Coproduct("Amorph", CUnit))
    }

    "returns NotEqual when coproducts are not the same terminal object" in {
      val circle: Shape    = Circle(10)
      val rectangle: Shape = Rectangle(3, 4)
      val amorph: Shape    = Amorph

      val result0 = circle ==== rectangle
      assert(
        result0 === Mismatch("Circle expected but Rectangle found")
      )

      val result1 = rectangle ==== circle
      assert(
        result1 === Mismatch("Rectangle expected but Circle found")
      )

      val result2 = circle ==== amorph
      assert(
        result2 === Mismatch("Circle expected but Amorph$ found")
      )
    }

    "returns NotEqual when coproducts are the same terminal object but different instances" in {
      val circle0: Shape = Circle(5)
      val circle1: Shape = Circle(10)
      val result0        = circle0 ==== circle1
      val result1        = circle1 ==== circle0
      assert(
        result0 === Coproduct(
          "Circle",
          Unnamed(List("radius" -> Primitive("Long", isEqual = false, error = Some("5 not equal to 10"))))
        )
      )
      assert(
        result1 === Coproduct(
          "Circle",
          Unnamed(List("radius" -> Primitive("Long", isEqual = false, error = Some("10 not equal to 5"))))
        )
      )

      val rectangle0: Shape = Rectangle(width = 1, height = 1)
      val rectangle1: Shape = Rectangle(width = 1, height = 2)
      val result2           = rectangle0 ==== rectangle1
      assert(
        result2 === Coproduct(
          "Rectangle",
          Unnamed(
            List(
              "width"  -> Primitive("Long", isEqual = true),
              "height" -> Primitive("Long", isEqual = false, error = Some("1 not equal to 2")),
            )
          )
        )
      )
    }
  }
}
