package equality

import cats.implicits._
import equality.all._
import org.scalatest.FreeSpec

/*
There are two kind of checks in this spec.
The ones using a simple `=><=`, and the inspecting the whole Ctree object.
The latter is more robust by implies more effort.
 */
class InstancesSpec extends FreeSpec {
    "Tuple" - {
      "should compare 2-arity tuples" in {
        val x: (Long, Boolean) = (10L, true)
        val y: (Long, Boolean) = (11L, false)

        assert((x =><= x).isEqual)
        assert((y =><= y).isEqual)
        assert(!(x =><= y).isEqual)
        assert(!(y =><= x).isEqual)
      }
      "should compare 3-arity tuples" in {
        val x: (Long, Boolean, String) = (10L, true, "Abc")
        val y: (Long, Boolean, String) = (11L, false, "Abd")

        assert((x =><= x).isEqual)
        assert((y =><= y).isEqual)
        assert(!(x =><= y).isEqual)
        assert(!(y =><= x).isEqual)
      }
      "should compare 4-arity tuples" in {
        val x: (Long, Boolean, String, Unit) = (10L, true, "Abc", ())
        val y: (Long, Boolean, String, Unit) = (11L, false, "Abd", ())

        assert((x =><= x).isEqual)
        assert((y =><= y).isEqual)
        assert(!(x =><= y).isEqual)
        assert(!(y =><= x).isEqual)
      }
    }
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

    "Either" - {
      "should compare instances of Either" in {
        val left: Either[String, Int]  = Left("y")
        val right: Either[String, Int] = Right(0)

        assert((left =><= left).isEqual)
        assert((right =><= right).isEqual)
        assert(!(left =><= right).isEqual)
        assert(!(right =><= left).isEqual)
      }
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
            "3" -> Mismatch("None$ expected but Some found")
          )
        )
        assert(result === expected)
      }
    }

    "Set" - {
      "should compare an empty set with a singleton set" in {
        val set0 = Set.empty[Char]
        val set1 = Set('a')
        // commutativity
        assert((set0 =><= set0).isEqual)
        assert(!(set0 =><= set1).isEqual)
        assert(!(set1 =><= set0).isEqual)
      }
      "should compare sets of different size" in {
        def generate(size: Int) = List.iterate(0, size)(_ + 1).toSet
        val set0 = generate(10)
        val set1 = generate(100)

        assert((set0 =><= set0).isEqual)
        assert((set1 =><= set1).isEqual)

        assert(!(set0 =><= set1).isEqual)
        assert(!(set1 =><= set0).isEqual)
      }
    }
    "Map" - {
      "should compare an empty Map with a singleton Map" in {
        val map0 = Map.empty[String, Int]
        val map1 = Map("x" -> 0)
        // commutativity
        assert((map0 =><= map0).isEqual)
        assert(!(map0 =><= map1).isEqual)
        assert(!(map1 =><= map0).isEqual)
      }
      "should compare sets of different size" in {
        def generate(size: Int) = List.iterate(0, size)(_ + 1).map(i => i.toString -> i).toMap
        val map0 = generate(10)
        val map1 = generate(100)

        assert((map0 =><= map0).isEqual)
        assert((map1 =><= map1).isEqual)

        assert(!(map0 =><= map1).isEqual)
        assert(!(map1 =><= map0).isEqual)
      }
    }

    "Vector" - {
      "should compare vectors of different sizes" in {
        val vector1: Vector[Int] = Vector.empty[Int]
        val vector2: Vector[Int] = Vector(0)

        assert((vector1 =><= vector1).isEqual)
        assert((vector2 =><= vector2).isEqual)
        assert(!(vector1 =><= vector2).isEqual)
        assert(!(vector2 =><= vector1).isEqual)
      }

      "should compare vectors of the same size" in {
        val vector1: Vector[Option[Int]] = Vector(1.some, none, 2.some, none)
        val vector2: Vector[Option[Int]] = Vector(1.some, none, 3.some, 4.some)

        assert((vector1 =><= vector1).isEqual)
        assert((vector2 =><= vector2).isEqual)
        assert(!(vector1 =><= vector2).isEqual)
        assert(!(vector2 =><= vector1).isEqual)
      }
    }
  }
}
