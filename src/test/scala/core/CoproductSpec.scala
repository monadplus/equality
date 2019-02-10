package core

import org.scalatest.FreeSpec

import instances.eq._
import syntax.eq._

sealed trait Shape
final case class Circle(radius: Long) extends Shape
final case class Rectangle(width: Long, height: Long) extends Shape

class CoroductSpec extends FreeSpec {

  "Eq on a coproduct data type" - {
    "returns Equal when both instances are equal" in {
      val circle: Shape = Circle(10)
      val result = circle ==== circle
      assert(result === Equal)
    }

    "returns NotEqual when coproducts are not the same terminal object" in {
      val circle: Shape = Circle(10)
      val rectangle: Shape = Rectangle(3, 4)
      val result0 = circle ==== rectangle
      val result1 = rectangle ==== circle
      assert(result0 === NotEqualPrimitive(s"Circle expected but found Rectangle"))
      assert(result1 === NotEqualPrimitive(s"Rectangle expected but found Circle"))
    }

    "returns NotEqual when coproducts are the same terminal object but different instances" in {
      val circle0: Shape = Circle(5)
      val circle1: Shape = Circle(10)
      val result = circle0 ==== circle1
      assert(result === NotEqual(Map(
        "radius" -> "5 not equal to 10"
      )))
    }
  }
}
