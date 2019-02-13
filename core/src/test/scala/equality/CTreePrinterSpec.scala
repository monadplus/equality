package equality

import com.sun.corba.se.pept.transport.ContactInfo
import org.scalatest.FreeSpec
import equality.all._

class CTreePrinterSpec extends FreeSpec {

  case class Person(name: String, contact: ContactInfo, dog: Dog)

  sealed trait ContactInfo
  case class Phone(number: String) extends ContactInfo
  case class Address(number: Int, street: String) extends ContactInfo

  case class Dog(name: String, age: Int)

  "A CTreePrinter" - {
    "should print a CTree[Person]" in {
      val dog = Dog(name = "Max", age = 1)
      val dog2 = Dog(name = "Bella", age = 1)

      val address: ContactInfo = Address(24, "Wailmore")
      val address2: ContactInfo = Address(23, "Wailmore")

      val person = Person(name = "John", contact = address, dog = dog)
      val person2 = Person(name = "Adam", contact = address2, dog = dog2)

      person ===! person2
//      val result = (person ==== person2).toString
//      val expected: String =
//        s""" ✕ Person
//           |     ├── ✕ name: String [John not equal to Adam]
//           |     └── ✕ dog: Dog
//           |                 ├── ✕ name: String [Max not equal to Bella]
//           |                 └── ✔ age: Integer""".stripMargin
//      assert(result === expected)
      person ===! person2
    }
  }
}


