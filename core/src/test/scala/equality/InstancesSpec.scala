package equality

import equality.all._
import org.scalatest.FreeSpec

class InstancesSpec extends FreeSpec {
  "Option" - {
    "should compare None" in {
      val result = None =><= None
      assert(result === CUnit)
    }

    "should compare Some" in {
      val some: Option[Int]  = Some(1)
      val some2: Option[Int] = Some(2)

      val result0 = some =><= some
      assert(result0 === Coproduct("Some", Unnamed(List("value" -> Primitive("Integer", isEqual = true, "1")))))

      val result1 = some =><= some2
      assert(
        result1 === Coproduct("Some",
                              Unnamed(List("value" -> Primitive("Integer", isEqual = false, "1 not equal to 2"))))
      )
    }

    "List" - {
      "should compare list of different sizes" in {
        val list1: List[Int] = Nil
        val list2: List[Int] = (1 to 100).toList

        val result   = list1 =><= list2
        val error    = "Left contains 0 elements and right contains 100"
        val expected = equality.Primitive("List", isEqual = false, content = error)
        assert(result === expected)
      }

      "should compare list of the same size" in {
        import cats.implicits._

        val list1: List[Option[Int]] = List(1.some, none, 2.some, none)
        val list2: List[Option[Int]] = List(1.some, none, 3.some, 4.some)

        val result = list1 =><= list2
        val expected = Named(
          "List",
          List(
            "0" -> Coproduct("Some", Unnamed(List("value" -> Primitive("Integer", isEqual = true, content = "1")))),
            "1" -> Coproduct("None", CUnit),
            "2" -> Coproduct(
              "Some",
              Unnamed(List("value" -> Primitive("Integer", isEqual = false, content = "2 not equal to 3")))
            ),
            "3" -> Mismatch("None$ expected but Some found"),
          )
        )
        assert(result === expected)
      }
    }
//  TODO
//
//  "Vector" - {
//    "should compare vectors of different sizes" in {
//      val vector1: Vector[Int] = Vector.empty[Int]
//      val vector2: Vector[Int] = Vector(0)
//
//      val result               = vector1 ==== vector2
//      assert(result === NotEqualPrimitive("0 elements expected but 1 found"))
//    }
//
//    "should compare vectors of the same size" in {
//      import cats.implicits._
//
//      val vector1: Vector[Option[Int]] = Vector(1.some, none, 2.some, none)
//      val vector2: Vector[Option[Int]] = Vector(1.some, none, 3.some, 4.some)
//
//      val result                       = vector1 ==== vector2
//      assert(
//        result === NotEqual(
//          Map(
//            Index(2, Choice("Some", End)) -> "2 not equal to 3",
//            Index(3, End)                 -> "None not equal to Some(4)"
//          )
//        )
//      )
//    }
//  }
//
//  "NonEmptyList" - {
//    "should compare nonEmptyList" in {
//      import cats.data.NonEmptyList
//
//      val nel1: NonEmptyList[Char] = NonEmptyList.of('a', 'b', 'c')
//      val nel2: NonEmptyList[Char] = NonEmptyList.of('a', 'b', 'd')
//
//      val result1 = nel1 ==== nel1
//      assert(result1 === Equal)
//
//      val result2 = nel1 ==== nel2
//      assert(result2 === NotEqual(Map(Field("tail", Index(1, End)) -> "c not equal to d")))
//    }
//  }
//
//  "Tuple" - {
//    // TupleN it's just an ordinary case class
//    "should resole an instance of Eq for 2-arity tuples" in {
//      val x: Tuple2[Long, Boolean] = (10L, true)
//      val y: Tuple2[Long, Boolean] = (11L, false)
//
//      val result0                  = x ==== x
//      assert(result0 === Equal)
//
//      val result1                  = x ==== y
//      assert(
//        result1 === NotEqual(
//          Map(
//            Field("_1", End) -> "10 not equal to 11",
//            Field("_2", End) -> "true not equal to false"
//          )
//        )
//      )
//    }
//  }
//
//  "Either" - {
//    "should resolve the instance for Either" in {
//      val left: Either[String, Int]  = Left("y")
//      val right: Either[String, Int] = Right(0)
//
//      val result0                    = left ==== left
//      assert(result0 === Equal)
//
//      val result1                    = right ==== right
//      assert(result1 === Equal)
//
//      val result2                    = left ==== right
//      assert(result2 === NotEqualPrimitive("Left expected but Right found"))
//    }
//  }
//
//  "Set" - {
//    "should resolve the instance for Set[A]" in {
//      val set0: Set[Char] = Set('a', 'b')
//      val set1: Set[Char] = Set('a', 'b', 'c')
//
//      val result0         = set0 ==== set0
//      assert(result0 === Equal)
//
//      val result1         = set0 ==== set1
//      assert(result1 === NotEqualPrimitive("Left set does not contain: c"))
//
//      val result2         = set1 ==== set0
//      assert(result2 === NotEqualPrimitive("Right set does not contain: c"))
//    }
//  }
//
//  "Map" - {
//    "should resolve the instance for Map[K, V]" in {
//      val map0: Map[String, Int] = Map("cat" -> 3, "dog" -> 4)
//      val map1: Map[String, Int] = Map("cat" -> 3)
//      val map2: Map[String, Int] = Map("cat" -> 3, "dog" -> 5)
//
//      val result0 = map0 ==== map0
//      assert(result0 === Equal)
//
//      val result1 = map0 ==== map1
//      assert(result1 === NotEqualPrimitive("Right map does not contain keys: dog"))
//
//      val result2 = map1 ==== map0
//      assert(result2 === NotEqualPrimitive("Left map does not contain keys: dog"))
//
//      val result3 = map0 ==== map2
//      assert(result3 === NotEqual(Map(
//          Key("dog", End) -> "4 not equal to 5"
//      )))
//    }
  }
}
