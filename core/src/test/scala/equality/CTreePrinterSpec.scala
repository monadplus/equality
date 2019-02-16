package equality

import org.scalatest.FreeSpec
import equality.all._
import equality.util.TestHelper._

class CTreePrinterSpec extends FreeSpec {

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

    "should print properly a large ADT" in {
      val a = A(B(C(D(G(H(false)), "world", "!"), true, 100L), "hello"), true)
      val a2 = A(B(C(D(G(H(true)), "world", "!!"), true, 101L), "hello"), false)

      val result = (a ==== a2).toString
      val expected: String =
        s"""✕ A
           |  ├── ✕ a: B
           |  │        ├── ✕ a: C
           |  │        │        ├── ✕ a: D
           |  │        │        │        ├── ✕ a: G
           |  │        │        │        │        └── ✕ a: H
           |  │        │        │        │                 └── ✕ a: Boolean [false not equal to true]
           |  │        │        │        ├── ✔ a1: String
           |  │        │        │        └── ✕ a2: String [! not equal to !!]
           |  │        │        ├── ✔ a2: Boolean
           |  │        │        └── ✕ a3: Long [100 not equal to 101]
           |  │        └── ✔ a1: String
           |  └── ✕ a1: Boolean [true not equal to false]""".stripMargin
      assert(result === expected)
    }

    // Value classes are not boxed so the underlying representation it is just
    // the value they are wrapping (weird behaviour with class name though)
    "should print value classes" in {
      val book = Book("Green Book")
      val book2 = Book("Moby-Dick")
      val result = (book ==== book2).toString
      val expected = "Book [Book(Green Book) not equal to Book(Moby-Dick)]"
      assert(result === expected)

    }
    "should print a List" in {
      val rest0 = Restaurant("Mamma mia", List("canelloni", "pizza", "gnoqui", "spaghetti").map(Dish(_, 10.0)))
      val rest1 = Restaurant("Piazza", List("canelloni", "pizza", "gnoqui", "farfalle").map(Dish(_, 10.0)))
      val rest2 = Restaurant("Mamma mia", List("sandwitch", "hotdog").map(Dish(_, 2.0)))
      val rest3 = Restaurant("Large", ('a' to 'z').toList.map(_.toString).map(Dish(_, 3.0)))
      val rest4 = Restaurant("Large", ('a' to 'z').toList.reverse.map(_.toString).map(Dish(_, 3.0)))

      val result = (rest0 ==== rest0).toString
      val expected =
        s"""✔ Restaurant
           |       ├── ✔ name: String
           |       └── ✔ menu: List
           |                     ├── ✔ 0: Dish
           |                     │          ├── ✔ name: String
           |                     │          └── ✔ price: Double
           |                     ├── ✔ 1: Dish
           |                     │          ├── ✔ name: String
           |                     │          └── ✔ price: Double
           |                     ├── ✔ 2: Dish
           |                     │          ├── ✔ name: String
           |                     │          └── ✔ price: Double
           |                     └── ✔ 3: Dish
           |                                ├── ✔ name: String
           |                                └── ✔ price: Double""".stripMargin
      assert(result === expected)

      val result1 = (rest0 ==== rest1).toString
      val expected1 =
        s"""✕ Restaurant
           |       ├── ✕ name: String [Mamma mia not equal to Piazza]
           |       └── ✕ menu: List
           |                     ├── ✔ 0: Dish
           |                     │          ├── ✔ name: String
           |                     │          └── ✔ price: Double
           |                     ├── ✔ 1: Dish
           |                     │          ├── ✔ name: String
           |                     │          └── ✔ price: Double
           |                     ├── ✔ 2: Dish
           |                     │          ├── ✔ name: String
           |                     │          └── ✔ price: Double
           |                     └── ✕ 3: Dish
           |                                ├── ✕ name: String [spaghetti not equal to farfalle]
           |                                └── ✔ price: Double""".stripMargin
      assert(result1 === expected1)

      val result2 = (rest0 ==== rest2).toString
      val expected2 =
        s"""✕ Restaurant
           |       ├── ✔ name: String
           |       └── ✕ menu: List [Left contains 4 elements and right contains 2]""".stripMargin
      assert(result2 === expected2)

      val result3 = (rest3 ==== rest3).toString
      val expected3 =
        s"""✔ Restaurant
           |       ├── ✔ name: String
           |       └── ✔ menu: List (too large)""".stripMargin
      assert(result3 === expected3)

      val result4 = (rest3 ==== rest4).toString
      val expected4 =
        s"""✕ Restaurant
           |       ├── ✔ name: String
           |       └── ✕ menu: List (too large: 26 elements are not equal)""".stripMargin
      assert(result4 === expected4)
    }

    "should print a Vector" in {
      val conference0 = Conference(assistants = Vector("John", "Adam", "Sam"))
      val conference1 = Conference(assistants = Vector("John", "Adam", "Amanda"))
      val result = (conference0 ==== conference1).toString
      val expected =
        s"""✕ Conference
           |       └── ✕ assistants: Vector
           |                            ├── ✔ 0: String
           |                            ├── ✔ 1: String
           |                            └── ✕ 2: String [Sam not equal to Amanda]""".stripMargin
      assert(result === expected)
    }

    "should print a Set" in {
      val runner0 = Runner("Alice", "UK")
      val runner1 = Runner("Annabel", "South Africa")
      val runner2 = Runner("Lauren", "USA")
      val race0 = Race(OneHundredMeters, runners = Set(runner0, runner1, runner2))
      val race1 = Race(OneHundredMeters, runners = Set(runner0, runner1))
      val race2 = Race(TwoHundredMeters, runners = ('a' to 'z').toSet.map((c: Char) => Runner(c.toString, "Dreamland")))

      val result = (race0 ==== race0).toString
      val expected =
        s"""✔ Race
           |    ├── ✔ category: OneHundredMeters
           |    └── ✔ runners: Set
           |                    ├── ✔ : Runner
           |                    │          ├── ✔ name: String
           |                    │          └── ✔ country: String
           |                    ├── ✔ : Runner
           |                    │          ├── ✔ name: String
           |                    │          └── ✔ country: String
           |                    └── ✔ : Runner
           |                               ├── ✔ name: String
           |                               └── ✔ country: String""".stripMargin
      assert(result === expected)

      val result1 = (race0 ==== race1).toString
      val expected1 =
        """✔ Race
          |    ├── ✔ category: OneHundredMeters
          |    └── ✔ runners: Set [missing elements]
          |                              └── ✔ : Runner
          |                                         ├── ✔ name: String
          |                                         └── ✔ country: String""".stripMargin
      assert(result1 === expected1)

      val result2 = (race0 ==== race2).toString
      val expected2 =
        """✕ Race
          |    ├── ✕ category: [OneHundredMeters$ expected but TwoHundredMeters$ found]
          |    └── ✕ runners: Set (too large: 29 elements are not equal)""".stripMargin
      assert(result2 === expected2)
    }
  }
}


