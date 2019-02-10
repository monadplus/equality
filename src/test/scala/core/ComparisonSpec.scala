package core

import org.scalatest.FreeSpec

class ComparisonSpec extends FreeSpec {
    "Comparison" - {
        "should transform Equal to String" in {
            val eq = Equal
            assert(eq.toString == "[✔]  Equals")
        }

        "should transform NotEqualPrimitive to String" in {
            val msg = "A not equal to B"
            val eq = NotEqualPrimitive(msg)
            assert(eq.toString == s"[✕]︎ $msg")
        }

        "should transform NotEqual to String" in {
          val comp: Comparison = NotEqual(Map(
            Field("x1", Choice("A", Field("xs", Index(0, End)))) -> "1 did not equal to 0"
          ))
          val expected: String = s"""root
          |  └── x1 (A)
          |          └── xs (0) FAILED
          |
          |1 did not equal to 0""".stripMargin
          assert(comp.toString === expected)

          val comp2: Comparison = NotEqual(Map(
            Field("x1", End) -> "1 did not equal to 0",
            Field("x2", Choice("A", Field("a1", End))) -> "C expected but found D",
            Field("x3", Index(0, Field("y1", End))) -> "asdf did not equal asd"
          ))
          val expected2: String = s"""root
          |  └── x1 FAILED
          |
          |1 did not equal to 0
          |
          |root
          |  └── x2 (A)
          |          └── a1 FAILED
          |
          |C expected but found D
          |
          |root
          |  └── x3 (0)
          |          └── y1 FAILED
          |
          |asdf did not equal asd""".stripMargin
          assert(comp2.toString === expected2)
        }
    }
}