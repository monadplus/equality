package equality.syntax

import equality._
import org.scalatest._

trait EqSyntax {
  implicit def equalitySyntax[A: Eq](a: A): EqOps[A] =
    new EqOps[A](a)
}

final class EqOps[A](self: A)(implicit eq: Eq[A]) {
  def =><=(other: A): CTree =
    eq.compare(self, other)

  def ====(other: A): Assertion = {
    val c = eq.compare(self, other)
    if (c.isEqual) Assertions.succeed
    else Assertions.fail(c.toString)
  }
}

object eq extends EqSyntax
