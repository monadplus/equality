package equality.instances

import equality.Eq._
import equality._
import equality.syntax.eq._
import cats.{Eq => _, _}, cats.data._, cats.implicits._

private[equality] trait EqInstances0 extends EqInstances1 {
  private def primitive[A]: Eq[A] = instance {
    case (s1, s2) =>
      val `class` = s1.getClass.getSimpleName
      if (s1 == s2) Primitive(`class`, isEqual = true)
      else Primitive(`class`, isEqual = false, error = Some(s"$s1 not equal to $s2"))
  }
  implicit val stringEq: Eq[String]            = primitive[String]
  implicit def primitiveEq[A <: AnyVal]: Eq[A] = primitive[A]
}

sealed private[equality] trait EqInstances1 {

  // A collection is considered large when it has more than 10 elements
  private val LARGE = 10

  def filterLarge(className: String, ct: Product): CTree = {
    if (ct.fields.length > LARGE) {
      val (eq, neq) = ct.fields.partition(_._2.isEqual)
      Large(className, eq, neq)
    } else ct
  }

  implicit def seqEq[F[_], A: Eq](className: String = "Seq")(implicit F: Foldable[F]): Eq[F[A]] = instance[F[A]] {
    case (l, r) =>
      if (F.size(l) != F.size(r)) {
        Primitive(className,
                  isEqual = false,
                  Some(s"Left contains ${F.size(l)} elements and right contains ${F.size(r)}"))
      } else {
        val zipped = CommutativeApply[ZipList].product(ZipList(l.toList), ZipList(r.toList)).value
        val res = zipped.zipWithIndex.foldLeft[Named](Named(className, List.empty)) {
          case (acc, ((l, r), index)) =>
            acc.copy(fields = acc.fields :+ (index.toString -> (l.asInstanceOf[A] ==== r.asInstanceOf[A])))
        }
        filterLarge(res.className, res)
      }
  }

  implicit def listEq[A: Eq]: Eq[List[A]] =
    seqEq[List, A]("List")

  implicit def vectorEq[A: Eq]: Eq[Vector[A]] =
    seqEq[Vector, A]("Vector")

  implicit def setEq[A: Eq]: Eq[Set[A]] = instance {
    case (l, r) =>
      val notEqualL = l.diff(r)
      val notEqualR = r.diff(l)
      val equal = r.intersect(l)
      def f[F[_]: Functor](fa: F[A]): F[(String, CTree)] =
        fa.map(a => "" -> (a ==== a))
      if (notEqualL.nonEmpty || notEqualR.nonEmpty) {
        if (notEqualL.size + notEqualR.size > LARGE) {
          Large("Set", equal = f(equal.toList), notEqual = f(notEqualL.toList ++ notEqualR.toList))
        }
        else {
          Named(
            className = "Set [missing elements]",
            fields = (notEqualL.toList ++ notEqualR.toList).map(a => "" -> (a ==== a)),
            force = Some(false))
        }
      }
      else {
        val res = equal.foldLeft[Named](Named("Set", List.empty)) {
          case (acc, next) =>
            // Comparing with itself to print a representation of the class
            acc.copy(fields = acc.fields :+ ("" -> (next ==== next)))
        }
        filterLarge(res.className, res)
      }
  }

//  implicit def mapEq[K, V: Eq]: Eq[Map[K, V]] = instance {
//    case (l, r) =>
//      val lKeys = l.keySet
//      val rKeys = r.keySet
//      val leftDiff = lKeys diff rKeys
//      if (leftDiff.isEmpty) {
//        val rightDiff = rKeys diff lKeys
//        if (rightDiff.isEmpty) {
//          lKeys.foldLeft[Comparison](Equal) {
//            case (acc, key) =>
//              val next = (l(key) ==== r(key)).prependKey(key.toString)
//              acc.combine(next)
//          }
//        } else {
//          NotEqualPrimitive(s"""Left map does not contain keys: ${rightDiff.mkString(",")}""")
//        }
//      } else {
//        NotEqualPrimitive(s"""Right map does not contain keys: ${leftDiff.mkString(",")}""")
//      }
//  }
}

object eq extends EqInstances0
