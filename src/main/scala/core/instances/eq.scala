package core.instances

import core._
import core.Eq._

sealed trait EqPrimitivesTypes {
  implicit val stringEq: Eq[String] = instance {
    case (s1, s2) =>
      if (s1 == s2) Equal else NotEqualPrimitive(s"$s1 not equal to $s2")
  }

  implicit def primitiveEq[A <: AnyVal]: Eq[A] = instance {
    case (s1, s2) =>
      if (s1 == s2) Equal else NotEqualPrimitive(s"$s1 not equal to $s2")
  }
}

sealed trait EqStdInstances {
  implicit def optionEq[A: Eq]: Eq[Option[A]] =
    ???
}

object eq extends EqPrimitivesTypes with EqStdInstances
