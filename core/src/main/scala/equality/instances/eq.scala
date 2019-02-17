package equality.instances

import java.io.File

import equality.Eq._
import equality._
import equality.syntax.eq._
import cats.{Eq => _, _}
import cats.data._
import cats.implicits._

private[equality] trait EqInstances0 extends EqInstances1 {
  private def primitive[A]: Eq[A] = instance {
    case (s1, s2) =>
      val `class` = s1.getClass.getSimpleName
      if (s1 == s2) Primitive(`class`, isEqual = true, s1.toString)
      else Primitive(`class`, isEqual = false, content = s"$s1 not equal to $s2")
  }
  implicit val stringEq: Eq[String]            = primitive[String]
  implicit def primitiveEq[A <: AnyVal]: Eq[A] = primitive[A]

  implicit val fileEq: Eq[File] = instance {
    case (l, r) =>
      val isEqual = l == r
      val content = if (isEqual) s"${l.getAbsolutePath}" else s"${l.getAbsolutePath} not equal to ${r.getAbsolutePath}"
      Primitive("File", isEqual, content)
  }
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
        Primitive(className, isEqual = false, s"Left contains ${F.size(l)} elements and right contains ${F.size(r)}")
      } else {
        val zipped = CommutativeApply[ZipList].product(ZipList(l.toList), ZipList(r.toList)).value
        val res = zipped.zipWithIndex.foldLeft[Named](Named(className, List.empty)) {
          case (acc, ((l, r), index)) =>
            acc.copy(fields = acc.fields :+ (index.toString -> (l.asInstanceOf[A] =><= r.asInstanceOf[A])))
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
      val equal     = r.intersect(l)
      def f[F[_]: Functor](fa: F[A]): F[(String, CTree)] =
        fa.map(a => "" -> (a =><= a))
      if (notEqualL.nonEmpty || notEqualR.nonEmpty) {
        if (notEqualL.size + notEqualR.size > LARGE) {
          Large("Set", equal = f(equal.toList), notEqual = f(notEqualL.toList ++ notEqualR.toList))
        } else {
          Named(className = "Set [missing elements]",
                fields = (notEqualL.toList ++ notEqualR.toList).map(a => "" -> (a =><= a)),
                force = Some(false))
        }
      } else {
        val res = equal.foldLeft[Named](Named("Set", List.empty)) {
          case (acc, next) =>
            // Comparing with itself to print a representation of the class
            acc.copy(fields = acc.fields :+ ("" -> (next =><= next)))
        }
        filterLarge(res.className, res)
      }
  }

  implicit def mapEq[K, V: Eq]: Eq[Map[K, V]] = instance {
    case (l, r) =>
      val (keysL, keysR) = (l.keySet, r.keySet)
      val notEqualL      = keysL.diff(keysR)
      val notEqualR      = keysR.diff(keysR)
      val equal          = keysL.intersect(keysR)
      if (notEqualL.nonEmpty || notEqualR.nonEmpty) {
        val error =
          s"Missing keys: ${notEqualL.map(_.toString).mkString(",")} (Left) ${notEqualR.map(_.toString).mkString(",")} (Right)"
        Primitive("Map", isEqual = false, content = error)
      } else {
        val res = equal.foldLeft[Named](Named("Map", List.empty)) {
          case (acc, key) =>
            acc.copy(fields = acc.fields :+ (key.toString -> (l(key) =><= r(key))))
        }
        filterLarge(res.className, res)
      }
  }
}

object eq extends EqInstances0
