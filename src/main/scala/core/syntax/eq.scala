package core.syntax

import core._

trait EqSyntax {
  implicit def equalitySyntax[A: Eq](a: A): EqOps[A] =
    new EqOps[A](a)
}

final class EqOps[A: Eq](self: A) {
  def ====(other: A): Comparison =
    implicitly[Eq[A]].compare(self, other)
}

object eq extends EqSyntax
