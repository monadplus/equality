package core

import org.scalatest.FreeSpec

class ProductSpec extends FreeSpec {

  case class AllPrimitives(x: Double,
                           x1: Float,
                           x2: Long,
                           x3: Int,
                           x4: Short,
                           x5: Byte,
                           x6: Unit,
                           x7: Boolean,
                           x8: Char)

  "A product type" - {
    "of all primitive types" - {
      "should resolve the implicit instance for Eq" in {
        import instances.eq._
        Eq[AllPrimitives]
      }
    }

    "when the syntax is imported" - {
      "should resolve the syntax operator" in {
        import instances.eq._
        import core.syntax.eq._
        "hello" ==== "bye"
      }
    }
  }
}
