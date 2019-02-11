package core.instances

import core._
import core.Eq._
import syntax.eq._

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
  implicit def optionEq[A: Eq]: Eq[Option[A]] = instance {
    case (None, None)         => Equal
    case (None, some)         => NotEqualPrimitive(s"None not equal to $some")
    case (some, None)         => NotEqualPrimitive(s"$some not equal to None")
    case (Some(a1), Some(a2)) => (a1 ==== a2).prependChoice("Some")
  }

  implicit def listEq[A: Eq]: Eq[List[A]] = instance {
    case (l, r) =>
      if (l.length != r.length)
        NotEqualPrimitive(s"${l.length} elements expected but ${r.length} found")
      else
        l.zip(r).zipWithIndex.foldLeft[Comparison](Equal) {
          case (acc, ((l, r), index)) =>
            val next: Comparison = (l ==== r).prependIndex(index)
            acc.combine(next)
        }
  }

  implicit def seqEq[A: Eq]: Eq[Seq[A]] = instance {
    case (l, r) =>
      if (l.length != r.length)
        NotEqualPrimitive(s"${l.length} elements expected but ${r.length} found")
      else
        l.zip(r).zipWithIndex.foldLeft[Comparison](Equal) {
          case (acc, ((l, r), index)) =>
            val next: Comparison = (l ==== r).prependIndex(index)
            acc.combine(next)
        }
  }

  /** seqEq is not inferir Vector[A] */
  implicit def vectorEq[A: Eq]: Eq[Vector[A]] = instance {
    case (l, r) =>
      if (l.length != r.length)
        NotEqualPrimitive(s"${l.length} elements expected but ${r.length} found")
      else
        l.zip(r).zipWithIndex.foldLeft[Comparison](Equal) {
          case (acc, ((l, r), index)) =>
            val next: Comparison = (l ==== r).prependIndex(index)
            acc.combine(next)
        }
  }

  implicit def setEq[A: Eq]: Eq[Set[A]] = instance {
    case (l, r) =>
      val leftDiff = l.diff(r)
      if (leftDiff.isEmpty) {
        val rightDiff = r.diff(l)
        if (rightDiff.isEmpty) {
          Equal
        } else {
          NotEqualPrimitive(s"""Left set does not contain: ${rightDiff.mkString(",")}""")
        }
      } else {
        NotEqualPrimitive(s"""Right set does not contain: ${leftDiff.mkString(",")}""")
      }
  }

  implicit def mapEq[K, V: Eq]: Eq[Map[K, V]] = instance {
    case (l, r) =>
      val lKeys = l.keySet
      val rKeys = r.keySet
      val leftDiff = lKeys diff rKeys
      if (leftDiff.isEmpty) {
        val rightDiff = rKeys diff lKeys
        if (rightDiff.isEmpty) {
          lKeys.foldLeft[Comparison](Equal) {
            case (acc, key) => 
              val next = (l(key) ==== r(key)).prependKey(key.toString)
              acc.combine(next)
          }
        } else {
          NotEqualPrimitive(s"""Left map does not contain keys: ${rightDiff.mkString(",")}""")
        }
      } else {
        NotEqualPrimitive(s"""Right map does not contain keys: ${leftDiff.mkString(",")}""")
      }
  }
}

object eq extends EqPrimitivesTypes with EqStdInstances
