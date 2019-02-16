package equality

import java.io.File

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
           |     └── ✔ street: String [Wailmore]""".stripMargin
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
           |   └── ✔ age: Integer [1]""".stripMargin
      assert(result === expected)
    }

    "should print an arbitrary ADT" in {
      val result = (person ==== person2).toString
      val expected: String =
        s"""✕ Person
           |     ├── ✕ name: String [John not equal to Adam]
           |     ├── ✕ contact: Address
           |     │                 ├── ✕ number: Integer [24 not equal to 23]
           |     │                 └── ✔ street: String [Wailmore]
           |     └── ✕ dog: Dog
           |                 ├── ✕ name: String [Max not equal to Bella]
           |                 └── ✔ age: Integer [1]""".stripMargin
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
           |  │        │        │        ├── ✔ a1: String [world]
           |  │        │        │        └── ✕ a2: String [! not equal to !!]
           |  │        │        ├── ✔ a2: Boolean [true]
           |  │        │        └── ✕ a3: Long [100 not equal to 101]
           |  │        └── ✔ a1: String [hello]
           |  └── ✕ a1: Boolean [true not equal to false]""".stripMargin
      assert(result === expected)
    }

    // Value classes are not boxed so the underlying representation it is just the value they are wrapping.
    "should print value classes" in {
      val book = Book("Odisea")
      val book1 = Book("Moby-Dick")
      val book2 = Book(" listen !" * 100)

      val result = (book ==== book1).toString
      val expected = "Book [Book(Odisea) not equal to Book(Moby-Dick)]"
      assert(result === expected)

      val result2 = (book ==== book2).toString
      val expected2 = "Book [[...]]"
      assert(result2 === expected2)
    }
    "should print a List" in {
      val rest0 = Restaurant("Mamma mia", List("canelloni", "pizza", "gnoqui", "spaghetti").map(Dish(_, 10.0)))
      val rest1 = Restaurant("Piazza", List("canelloni", "pizza", "gnoqui", "farfalle").map(Dish(_, 10.0)))
      val rest2 = Restaurant("Mamma mia", List("sandwitch", "hotdog").map(Dish(_, 2.0)))
      val rest3 = Restaurant("Vapiano", ('a' to 'z').toList.map(_.toString).map(Dish(_, 3.0)))
      val rest4 = Restaurant("Vapiano", ('a' to 'z').toList.reverse.map(_.toString).map(Dish(_, 3.0)))

      val result = (rest0 ==== rest0).toString
      val expected =
        s"""✔ Restaurant
           |       ├── ✔ name: String [Mamma mia]
           |       └── ✔ menu: List
           |                     ├── ✔ 0: Dish
           |                     │          ├── ✔ name: String [canelloni]
           |                     │          └── ✔ price: Double [10.0]
           |                     ├── ✔ 1: Dish
           |                     │          ├── ✔ name: String [pizza]
           |                     │          └── ✔ price: Double [10.0]
           |                     ├── ✔ 2: Dish
           |                     │          ├── ✔ name: String [gnoqui]
           |                     │          └── ✔ price: Double [10.0]
           |                     └── ✔ 3: Dish
           |                                ├── ✔ name: String [spaghetti]
           |                                └── ✔ price: Double [10.0]""".stripMargin
      assert(result === expected)

      val result1 = (rest0 ==== rest1).toString
      val expected1 =
        s"""✕ Restaurant
           |       ├── ✕ name: String [Mamma mia not equal to Piazza]
           |       └── ✕ menu: List
           |                     ├── ✔ 0: Dish
           |                     │          ├── ✔ name: String [canelloni]
           |                     │          └── ✔ price: Double [10.0]
           |                     ├── ✔ 1: Dish
           |                     │          ├── ✔ name: String [pizza]
           |                     │          └── ✔ price: Double [10.0]
           |                     ├── ✔ 2: Dish
           |                     │          ├── ✔ name: String [gnoqui]
           |                     │          └── ✔ price: Double [10.0]
           |                     └── ✕ 3: Dish
           |                                ├── ✕ name: String [spaghetti not equal to farfalle]
           |                                └── ✔ price: Double [10.0]""".stripMargin
      assert(result1 === expected1)

      val result2 = (rest0 ==== rest2).toString
      val expected2 =
        s"""✕ Restaurant
           |       ├── ✔ name: String [Mamma mia]
           |       └── ✕ menu: List [Left contains 4 elements and right contains 2]""".stripMargin
      assert(result2 === expected2)

      val result3 = (rest3 ==== rest3).toString
      val expected3 =
        s"""✔ Restaurant
           |       ├── ✔ name: String [Vapiano]
           |       └── ✔ menu: List (too large)""".stripMargin
      assert(result3 === expected3)

      val result4 = (rest3 ==== rest4).toString
      val expected4 =
        s"""✕ Restaurant
           |       ├── ✔ name: String [Vapiano]
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
           |                            ├── ✔ 0: String [John]
           |                            ├── ✔ 1: String [Adam]
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
           |                    │          ├── ✔ name: String [Alice]
           |                    │          └── ✔ country: String [UK]
           |                    ├── ✔ : Runner
           |                    │          ├── ✔ name: String [Annabel]
           |                    │          └── ✔ country: String [South Africa]
           |                    └── ✔ : Runner
           |                               ├── ✔ name: String [Lauren]
           |                               └── ✔ country: String [USA]""".stripMargin
      assert(result === expected)

      val result1 = (race0 ==== race1).toString
      val expected1 =
        """✔ Race
          |    ├── ✔ category: OneHundredMeters
          |    └── ✔ runners: Set [missing elements]
          |                              └── ✔ : Runner
          |                                         ├── ✔ name: String [Lauren]
          |                                         └── ✔ country: String [USA]""".stripMargin
      assert(result1 === expected1)

      val result2 = (race0 ==== race2).toString
      val expected2 =
        """✕ Race
          |    ├── ✕ category: [OneHundredMeters$ expected but TwoHundredMeters$ found]
          |    └── ✕ runners: Set (too large: 29 elements are not equal)""".stripMargin
      assert(result2 === expected2)
    }

    "should print a Map" in {
      val postgresConfig0: Configuration = PostgresConfig(properties = List("A", "B"))
      val postgresConfig1: Configuration = PostgresConfig(properties = List("A", "C"))
      val kafkaConfig0: Configuration = KafkaConfig(properties = List("A", "C"))
      val kafkaConfig1: Configuration = KafkaConfig(properties = List("A", "C"))
      val configurations0 = Configurations(file = new File("/Users/user/.config/configurations.conf"), values = Map(
        "postgres" -> postgresConfig0,
        "kafka" -> kafkaConfig0,
      ))
      val configurations1 = Configurations(file = new File("/Users/user/.config/configurations2.conf"), values = Map(
        "postgres" -> postgresConfig1,
        "kafka" -> kafkaConfig1,
      ))

      val result = (configurations0 ==== configurations0).toString
      val expected =
        """✔ Configurations
          |         ├── ✔ file: File [/Users/user/.config/configurations.conf]
          |         └── ✔ values: Map
          |                        ├── ✔ postgres: PostgresConfig
          |                        │                      └── ✔ properties: List
          |                        │                                          ├── ✔ 0: String [A]
          |                        │                                          └── ✔ 1: String [B]
          |                        └── ✔ kafka: KafkaConfig
          |                                          └── ✔ properties: List
          |                                                              ├── ✔ 0: String [A]
          |                                                              └── ✔ 1: String [C]""".stripMargin
      assert(result === expected)

      val result1 = (configurations0 ==== configurations1).toString
      val expected1 =
        """✕ Configurations
          |         ├── ✕ file: File [/Users/user/.config/configurations.conf not equal to /Users/user/.config/configurations2.conf]
          |         └── ✕ values: Map
          |                        ├── ✕ postgres: PostgresConfig
          |                        │                      └── ✕ properties: List
          |                        │                                          ├── ✔ 0: String [A]
          |                        │                                          └── ✕ 1: String [B not equal to C]
          |                        └── ✔ kafka: KafkaConfig
          |                                          └── ✔ properties: List
          |                                                              ├── ✔ 0: String [A]
          |                                                              └── ✔ 1: String [C]""".stripMargin
      assert(result1 === expected1)
    }
  }
}


