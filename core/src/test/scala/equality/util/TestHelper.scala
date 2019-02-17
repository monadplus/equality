package equality.util

import java.io.{File, Serializable}

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

  // ADT1
  case class Person(name: String, contact: ContactInfo, dog: Dog)
  sealed trait ContactInfo
  case class Phone(number: String)                extends ContactInfo
  case class Address(number: Int, street: String) extends ContactInfo
  case class Dog(name: String, age: Int)

  // Value class
  case class Book(name: String) extends AnyVal

  // List
  case class Dish(name: String, price: Double)
  case class Restaurant(name: String, menu: List[Dish])

  // Vector
  case class Conference(assistants: Vector[String])

  // Set
  sealed trait Category        extends Serializable
  case object OneHundredMeters extends Category
  case object TwoHundredMeters extends Category
  case class Runner(name: String, country: String)
  case class Race(category: Category, runners: Set[Runner])

  // Map
  sealed trait Configuration extends Serializable {
    val properties: List[String]
  }
  case class PostgresConfig(properties: List[String]) extends Configuration
  case class KafkaConfig(properties: List[String])    extends Configuration
  case class Configurations(file: File, values: Map[String, Configuration])
}
