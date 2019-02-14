package equality.util

object TestHelper {
  // Product
  case class AllPrimitives(
    a: Double,
    a1: Float,
    a2: Long,
    a3: Int,
    a4: Short,
    a5: Byte,
    a6: Unit,
    a7: Boolean,
    a8: Char
  )

  // Coproduct
  sealed trait Shape
  final case class Circle(radius: Long)                 extends Shape
  final case class Rectangle(width: Long, height: Long) extends Shape
  case object Amorph                                    extends Shape

  // ADT1
  case class Person(name: String, contact: ContactInfo, dog: Dog)
  sealed trait ContactInfo
  case class Phone(number: String)                extends ContactInfo
  case class Address(number: Int, street: String) extends ContactInfo
  case class Dog(name: String, age: Int)

  // ADT2
  case class A(a: B, a1: Boolean)
  case class B(a: C, a1: String)
  case class C(a: DE, a2: Boolean, a3: Long)
  sealed trait DE
  case class D(a: FG, a1: String, a2: String) extends DE
  case object E                               extends DE
  sealed trait FG
  case class F(a: String) extends FG
  case class G(a: H)      extends FG
  case class H(a: Boolean)

  // Value class
  case class Book(name: String) extends AnyVal
  // List
  case class Dish(name: String, price: Double)
  case class Restaurant(name: String, menu: List[Dish])
}
