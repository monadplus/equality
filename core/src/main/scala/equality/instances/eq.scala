package equality.instances

import equality.{Eq, Primitive}
import equality.Eq._

private[equality] trait EqInstances0 {
  private def primitive[A]: Eq[A] = instance {
    case (s1, s2) =>
      val `class` = s1.getClass.getSimpleName
      if (s1 == s2) Primitive(`class`, isEqual = true)
      else Primitive(`class`, isEqual = false, error = Some(s"$s1 not equal to $s2"))
  }
  implicit val stringEq: Eq[String]            = primitive[String]
  implicit def primitiveEq[A <: AnyVal]: Eq[A] = primitive[A]
}

object eq extends EqInstances0
