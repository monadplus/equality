package equality

import org.scalatest.FreeSpec
import equality.all._

class CTreePrinterSpec extends FreeSpec {

  case class Person(name: String, contact: ContactInfo, dog: Dog)

  sealed trait ContactInfo
  case class Phone(number: String) extends ContactInfo
  case class Address(number: Int, street: String) extends ContactInfo

  case class Dog(name: String, age: Int)

  val dog = Dog(name = "Max", age = 1)
  val dog2 = Dog(name = "Bella", age = 1)

  val address: ContactInfo = Address(24, "Wailmore")
  val address2: ContactInfo = Address(23, "Wailmore")
  val phone: ContactInfo = Phone("12391391")

  val person = Person(name = "John", contact = address, dog = dog)
  val person2 = Person(name = "Adam", contact = address2, dog = dog2)

  "A CTreePrinter" - {
    "should print a coproduct when they are the same object" in {
      val result = (address ==== address2).toString
      val expected =
        s"""✕ Address
           |     ├── ✕ number: Integer [24 not equal to 23]
           |     └── ✔ street: String""".stripMargin
      assert(result === expected)
    }

    "should print a coproduct when they are not the same element" in {
      val result = (phone ==== address2).toString
      val expected = s"""Phone expected but Address found"""
      assert(result === expected)
    }

    "should print a product" in {
      val result = (dog ==== dog2).toString
      val expected: String =
        s"""✕ Dog
           |   ├── ✕ name: String [Max not equal to Bella]
           |   └── ✔ age: Integer""".stripMargin
      assert(result === expected)
    }

    "should print an arbitrary ADT" in {
      val result = (person ==== person2).toString
      val expected: String =
        s"""✕ Person
           |     ├── ✕ name: String [John not equal to Adam]
           |     ├── ✕ contact: Address
           |     │                 ├── ✕ number: Integer [24 not equal to 23]
           |     │                 └── ✔ street: String
           |     └── ✕ dog: Dog
           |                 ├── ✕ name: String [Max not equal to Bella]
           |                 └── ✔ age: Integer""".stripMargin
      assert(result === expected)
    }
  }

  case class A(name)
}


