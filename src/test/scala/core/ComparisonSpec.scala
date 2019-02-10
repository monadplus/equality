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
          fail("TODO")
        }
    }
}