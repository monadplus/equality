package core

import org.scalatest.FreeSpec
import syntax.eq._
import instances.eq._

class InstancesSpec extends FreeSpec {
  "Option" - {
    "should compare None" in {
      val result = None ==== None
      assert(result === Equal)
    }

    "should compare Some" in {
      val some: Option[Int]  = Some(1)
      val some2: Option[Int] = Some(2)
      val result0            = some ==== some
      val result1            = some ==== some2
      assert(result0 === Equal)
      assert(result1 === NotEqual(Map(Choice("Some", End) -> "1 not equal to 2")))
    }

    "should compare Some and None" in {
      val some: Option[Char] = Some('z')
      val none: Option[Char] = None
      val result0            = some ==== none
      val result1            = none ==== some
      assert(result0 === NotEqualPrimitive("Some(z) not equal to None"))
      assert(result1 === NotEqualPrimitive("None not equal to Some(z)"))
    }
  }

  "List" - {
    "should compare list of different sizes" in {
      val list1: List[Int] = Nil
      val list2: List[Int] = List(0)
      val result           = list1 ==== list2
      assert(result === NotEqualPrimitive("0 elements expected but 1 found"))
    }

    "should compare list of the same size" in {
      import cats.implicits._

      val list1: List[Option[Int]] = List(1.some, none, 2.some, none)
      val list2: List[Option[Int]] = List(1.some, none, 3.some, 4.some)
      val result                   = list1 ==== list2
      assert(
        result === NotEqual(
          Map(
            Index(2, Choice("Some", End)) -> "2 not equal to 3",
            Index(3, End)                 -> "None not equal to Some(4)"
          )
        )
      )
    }
  }

  "Vector" - {
    "should compare vectors of different sizes" in {
      val vector1: Vector[Int] = Vector.empty[Int]
      val vector2: Vector[Int] = Vector(0)
      val result               = vector1 ==== vector2
      assert(result === NotEqualPrimitive("0 elements expected but 1 found"))
    }

    "should compare vectors of the same size" in {
      import cats.implicits._

      val vector1: Vector[Option[Int]] = Vector(1.some, none, 2.some, none)
      val vector2: Vector[Option[Int]] = Vector(1.some, none, 3.some, 4.some)
      val result                       = vector1 ==== vector2
      assert(
        result === NotEqual(
          Map(
            Index(2, Choice("Some", End)) -> "2 not equal to 3",
            Index(3, End)                 -> "None not equal to Some(4)"
          )
        )
      )
    }
  }

  "Tuple" - {
    // TupleN it's just an ordinary case class
    "should resole an instance of Eq for 2-arity tuples" in {
      val x: Tuple2[Long, Boolean] = (10L, true)
      val y: Tuple2[Long, Boolean] = (11L, false)
      val result0                  = x ==== x
      val result1                  = x ==== y
      assert(result0 === Equal)
      assert(
        result1 === NotEqual(
          Map(
            Field("_1", End) -> "10 not equal to 11",
            Field("_2", End) -> "true not equal to false"
          )
        )
      )
    }
  }

  "Either" - {
    "should resolve the instance for Either" in {
      val left: Either[String, Int]  = Left("y")
      val right: Either[String, Int] = Right(0)
      val result0                    = left ==== left
      val result1                    = right ==== right
      val result2                    = left ==== right
      assert(result0 === Equal)
      assert(result1 === Equal)
      assert(result2 === NotEqualPrimitive("Left expected but Right found"))
    }
  }

  "Set" - {
    "should resolve the instance for Set[A]" in {
      val set0: Set[Char] = Set('a', 'b')
      val set1: Set[Char] = Set('a', 'b', 'c')
      val result0         = set0 ==== set0
      val result1         = set0 ==== set1
      val result2         = set1 ==== set0
      assert(result0 === Equal)
      assert(result1 === NotEqualPrimitive("Left set does not contain: c"))
      assert(result2 === NotEqualPrimitive("Right set does not contain: c"))
    }
  }

  "Map" - {
    "should resolve the instance for Map[K, V]" in {
      val map0: Map[String, Int] = Map("cat" -> 3, "dog" -> 4)
      val map1: Map[String, Int] = Map("cat" -> 3)
      val map2: Map[String, Int] = Map("cat" -> 3, "dog" -> 5)
      val result0                = map0 ==== map0
      val result1                = map0 ==== map1
      val result2                = map0 ==== map2
      assert(result0 === Equal)
      assert(result1 === Equal) // TODO
      assert(result2 === Equal) // TODO
    }
  }
}
