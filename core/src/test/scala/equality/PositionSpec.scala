package equality

import org.scalatest.FunSpec
import all._

import scala.util.{Failure, Success, Try}
import org.scalatest.exceptions.TestFailedException

class PositionSpec extends FunSpec {

  case class Foo(i: Int)

  describe("org.scalactic.source.Position") {
    it("should output the assert line in case of failure") {
      val foo = Foo(i = 10)
      val foo2 = Foo(i = 15)
      Try(foo ==== foo2) match {
        case Success(_) => fail("should throw")
        case Failure(t) =>
          val err = t.asInstanceOf[TestFailedException]
          val msg = err.message.get
          msg.lines.toList.last.endsWith(".(PositionSpec.scala:17)")
          succeed
      }
    }
  }
}
