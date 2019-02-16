TODO
TODO
TODO
TODO
TODO
TODO
TODO
TODO
TODO
TODO
TODO
TODO
TODO
# Equality 
[![Travis CI](https://travis-ci.org/monadplus/equality.svg?branch=master)](https://travis-ci.org/monadplus/equality) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/f01edd87fcfe45fd9c7bd6e44b64e5ae)](https://app.codacy.com/app/monadplus/equality?utm_source=github.com&utm_medium=referral&utm_content=monadplus/equality&utm_campaign=Badge_Grade_Dashboard) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/554261fd76634affb7f40b54f8b8583a)](https://www.codacy.com/app/monadplus/equality?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=monadplus/equality&amp;utm_campaign=Badge_Coverage)

## Project Goals
In the past, comparing instances with scalatest `===` has been a nuisance. Triple equal output must be compared by hand with an external tool which slows down your project development.

Equality brings a better triple equals in the form of `===!` which prints, in case of error, a tree representation of the divergence between the compared values. 
## Quick Start
To use equality in an existing SBT project with Scala 2.12, add the following dependency to your `build.sbt`:
```scala
resolvers += Resolver.bintrayRepo("io-monadplus", "maven")

libraryDependencies += "io.monadplus" %% "equality-core" % "0.0.1"
```
## Example
Equality is thought to be used with [scalatest](http://www.scalatest.org/) for testing
```scala
import org.scalatest.FreeSpec
import core.all._

class Example extends FreeSpec {
  "Given an arbitrary ADT" - {
    "should compare two instances" in {
      
      sealed trait XY
      case class X(x: Option[List[Int]]) extends XY
      case class Y(bool: Boolean) extends XY
      case class Z(z: XY)

      val z0 = Z(z = X(Some(List(1,2,3))))
      val z1 = Z(z = X(Some(List(1,2,2))))

      z0 ===! z1
    }
  }
```
## Create your own instances
Equality is powered by [shapeless](https://github.com/milessabin/shapeless) for type class derivation of arbitrary ADTs. Equality can derivate instances of products (case classes) and coproducts (sealed traits + subclasses) of any combination of primitive type. It also supports scala's std collection like Option, List, Vector, Map, Set, et cetera.

Furthermore, a user can define its own instances in case of need. 

In this example we are going to create an instance for [NonEmptyList](https://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/data/NonEmptyList.scala)*
```scala
import core.all._

implicit def nonEmptyListEq[A: Eq]: Eq[NonEmptyList[A]] = new Eq[NonEmptyList[A]] {
    override def compare(x: NonEmptyList[A], y: NonEmptyList[A]) = {
      val head = (x.head ==== y.head).prependField("head")
      val tail = (x.tail ==== y.tail).prependField("tail")
      head.combine(tail)
    }
}
```
*Actually, there is no need for this particular instance as equality is clever enough to auto-derivate it.
## Releases**

__Release 0.1__  
 - Microsite
 - Tree visualization
 
 __Release 0.2__
 - Mima check for binary BC
 - Matryoshka for a faster compile time  