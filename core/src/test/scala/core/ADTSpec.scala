package core

import org.scalatest.FreeSpec
import all._

sealed trait GH
final case class G(double: Double) extends GH
final case class H(float: Float)   extends GH

sealed trait IJK
case class I(long: Long) extends IJK
case class J(long: Long) extends IJK
case class K(long: Long) extends IJK

sealed trait DE
final case class D(ijk: IJK)         extends DE
final case class E(gh: GH, int: Int) extends DE

case class ABC(bool: Boolean, de: DE)

class ADTSpec extends FreeSpec {

  val instance0 = ABC(
    bool = true,
    de = E(
      gh = G(double = 2.0),
      int = 1
    )
  )

  val instance1 = ABC(
    bool = false,
    de = E(
      gh = H(float = 2.0f),
      int = 2
    )
  )

  val instance2 = ABC(
    bool = true,
    de = E(
      gh = G(double = 1.0),
      int = 1
    )
  )

  val instance3 = ABC(
    bool = true,
    de = D(
      ijk = I(long = 1L)
    )
  )

  val instance4 = ABC(
    bool = true,
    de = D(
      ijk = J(long = 1L)
    )
  )

  "An ADT" - {
    "comparing equal instances" - {
      "should return Equal" in {
        val result0 = instance0 ==== instance0
        val result1 = instance1 ==== instance1
        val result2 = instance2 ==== instance2
        val result3 = instance3 ==== instance3

        assert(result0 === Equal)
        assert(result1 === Equal)
        assert(result2 === Equal)
        assert(result3 === Equal)
      }
    }

    "comparing not equal instances" - {
      "should return NotEqual" in {
        val result0 = instance0 ==== instance1
        val result1 = instance0 ==== instance2
        val result2 = instance3 ==== instance4
        val expected0 = NotEqual(
          Map(
            Field("bool", End)                          -> "true not equal to false",
            Field("de", Choice("E", Field("gh", End)))  -> "G expected but H found",
            Field("de", Choice("E", Field("int", End))) -> "1 not equal to 2"
          )
        )

        val expected1 = NotEqual(
          Map(
            Field("de", Choice("E", Field("gh", Choice("G", Field("double", End))))) -> "2.0 not equal to 1.0"
          )
        )

        val expected2 = NotEqual(
          Map(
            Field("de", Choice("D", Field("ijk", End))) -> "I expected but J found"
          )
        )

        assert(result0 === expected0)
        assert(result1 === expected1)
        assert(result2 === expected2)
      }
    }
  }

  case class ADT(s1: Long, s2: Vector[Map[Int, Option[String]]])

  "Another ADT" - {
    "composed by scala's std data structures" - {
      "should compare two given instances" in {
        val map1: Map[Int, Option[String]] =
          Map(
            0 -> None,
            1 -> Some("aaa"),
            2 -> Some("bbb")
          )
          val map2: Map[Int, Option[String]] =
          Map(
            0 -> None,
            1 -> Some("ccc"),
            2 -> Some("bbb")
          )
          val map3: Map[Int, Option[String]] =
          Map(
            0 -> None,
            1 -> Some("aaa"),
          )

        val adt1 = ADT(100L, Vector(map1, map1, map3))
        val adt2 = ADT(100L, Vector(map1, map2, map3))

        val result = adt1 ==== adt2
        val path: Path = Field("s2", Index(1, Key("1", Choice("Some", End))))
        assert(result === NotEqual(Map(
          path -> "aaa not equal to ccc"
        )))
      }
    }
  }
}
