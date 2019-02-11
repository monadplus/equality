package core.syntax

import core._
import org.scalatest._

trait EqSyntax {
  implicit def equalitySyntax[A: Eq](a: A): EqOps[A] =
    new EqOps[A](a)
}

final class EqOps[A](self: A)(implicit eq: Eq[A]) {
  def ====(other: A): Comparison =
    eq.compare(self, other)

  def ===!(other: A): Assertion  = {
    eq.compare(self, other) match {
      case Equal => Assertions.succeed
      case otherwise => Assertions.fail(otherwise.toString)
    }
  }
}

object eq extends EqSyntax
