---
layout: home

---
# Equality 
[![Travis CI](https://travis-ci.org/monadplus/equality.svg?branch=master)](https://travis-ci.org/monadplus/equality) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/f01edd87fcfe45fd9c7bd6e44b64e5ae)](https://app.codacy.com/app/monadplus/equality?utm_source=github.com&utm_medium=referral&utm_content=monadplus/equality&utm_campaign=Badge_Grade_Dashboard) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/554261fd76634affb7f40b54f8b8583a)](https://www.codacy.com/app/monadplus/equality?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=monadplus/equality&amp;utm_campaign=Badge_Coverage)

## Project Goals
Equality brings a better equals in the form of `====` which prints, in case of error, a tree representation of the divergence between the 
compared values. 
## Quick Start
To use equality in an existing SBT project with Scala 2.12, add the following dependency to your `build.sbt`:
```scala
resolvers += Resolver.bintrayRepo("io-monadplus", "maven")

libraryDependencies += "io.monadplus" %% "equality-core" % "0.0.3"
```
## Example
Equality is thought to be used with [scalatest](http://www.scalatest.org/).
```tut:silent
import org.scalatest.FreeSpec
import cats.implicits._
import equality.all._

class Example extends FreeSpec {
  "Given an arbitrary ADT" - {
    "should compare two instances" in {
      
      sealed trait Establishment
      case class Pub(name: String) extends Establishment
      case class Restaurant(name: String, clientsPerTable: List[Option[Int]]) extends Establishment
      case class Owner(name: String, establishment: Establishment)

      val toscana = Restaurant("Toscana", List(none, 2.some, 3.some))
      val andrea = Owner(name = "Andrea", establishment = toscana)
      
      val piazza = Restaurant("Piazza", List(none, 2.some, 4.some))
      val mario = Owner(name = "Mario", establishment = piazza)

      andrea ==== mario
    }
  }
}
```
```tut
(new Example()).execute()
```
## Create your own instances
Equality is powered by [shapeless](https://github.com/milessabin/shapeless) for type class derivation of arbitrary ADTs. Equality can derive 
instances of products (case classes) and coproducts (sealed traits + subclasses) of any combination of primitive type. It also supports scala's std collection like Option, List, Vector, Map, Set, et cetera.

Furthermore, a user can define its own instances in case of need. 

In this example we are going to create an instance for [NonEmptyList](https://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/data/NonEmptyList.scala)
```tut:silent
import equality._
import equality.all._
import cats.data.NonEmptyList

implicit def nonEmptyListEq[A: Eq]: Eq[NonEmptyList[A]] = 
  new Eq[NonEmptyList[A]] {
    override def compare(x: NonEmptyList[A], y: NonEmptyList[A]) =
      Named(className = "NonEmptyList", fields = List(
        "head" -> (x.head =><= y.head),
        "tail" -> (x.tail =><= y.tail)
      ))
  }
```

## Alternatives
- [diff](https://github.com/xdotai/diff)
