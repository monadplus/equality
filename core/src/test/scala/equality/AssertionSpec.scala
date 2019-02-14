package equality

import equality.all._
import org.scalatest._
import scala.util._

class AssertionSpec extends FreeSpec {

    case class A(x1: Int, x2: Boolean, x3: Double)

    "Assert" - {
        "should succeed when both instances are equal" in {
            val a = A(0, true, 0.0)
            val result = Try(a ===! a)
            assert(result.isSuccess)
            assert(result.get === Succeeded)
        }
        
        "should fail when the instances are not equal" in {
            val a = A(0, true, 0.0)
            val a2 = A(1, false, 1.0)
            val result = Try(a ===! a2)
            assert(result.isFailure)
        }
    }
}