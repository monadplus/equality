package core

import org.scalatest.FreeSpec

import instances.eq._
import syntax.eq._

sealed trait Shape
final case class Circle(radius: Long)                 extends Shape
final case class Rectangle(width: Long, height: Long) extends Shape
case object Amorph                                 extends Shape

class CoproductSpec extends FreeSpec {

  "Eq on a coproduct data type" - {
    "returns Equal when both instances are equal" in {
      val circle: Shape = Circle(10)
      val result        = circle ==== circle
      assert(result === Equal)

      val result2 = (Amorph: Shape) ==== (Amorph: Shape)
      assert(result2 === Equal)
    }

    "returns NotEqual when coproducts are not the same terminal object" in {
      val circle: Shape    = Circle(10)
      val rectangle: Shape = Rectangle(3, 4)
      val amorph: Shape = Amorph

      val result0          = circle ==== rectangle
      assert(result0 === NotEqualPrimitive("Circle expected but Rectangle found"))

      val result1          = rectangle ==== circle
      assert(result1 === NotEqualPrimitive("Rectangle expected but Circle found"))

      val result2 = circle ==== amorph
      assert(result2 === NotEqualPrimitive("Circle expected but Amorph$ found"))
    }

    "returns NotEqual when coproducts are the same terminal object but different instances" in {
      val circle0: Shape = Circle(5)
      val circle1: Shape = Circle(10)
      val result0        = circle0 ==== circle1
      val result1        = circle1 ==== circle0
      assert(result0 === NotEqual(Map(Choice("Circle", Field("radius", End)) -> "5 not equal to 10")))
      assert(result1 === NotEqual(Map(Choice("Circle", Field("radius", End)) -> "10 not equal to 5")))
    }
  }
}
