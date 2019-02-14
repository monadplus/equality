package equality.instances

import equality._
import equality.Eq._
import equality.syntax.eq._

private[equality] trait EqInstances0 extends EqInstances1{
  private def primitive[A]: Eq[A] = instance {
    case (s1, s2) =>
      val `class` = s1.getClass.getSimpleName
      if (s1 == s2) Primitive(`class`, isEqual = true)
      else Primitive(`class`, isEqual = false, error = Some(s"$s1 not equal to $s2"))
  }
  implicit val stringEq: Eq[String]            = primitive[String]
  implicit def primitiveEq[A <: AnyVal]: Eq[A] = primitive[A]
}

 private[equality] sealed trait EqInstances1 {

   implicit def listEq[A: Eq]: Eq[List[A]] = instance {
     case (l, r) =>
       if (l.length != r.length) {
         Primitive("List", isEqual = false, Some(s"L: ${l.length} R: ${r.length}."))
       }
       else {
         val res =  l.zip(r).zipWithIndex.foldLeft[Named](Named("List", List.empty)) {
           case (acc, ((l, r), index)) =>
             acc.copy(fields = acc.fields :+ (index.toString -> (l ==== r)))
         }
         if (res.fields.length > 10) {
           val (eq , neq) = res.fields.partition(_._2.isEqual)
           Large(res.className, eq, neq)
         } else res
       }
   }
 }


//
//  implicit def listEq[A: Eq]: Eq[List[A]] = instance {
//    case (l, r) =>

//  }
//
//  implicit def seqEq[A: Eq]: Eq[Seq[A]] = instance {
//    case (l, r) =>
//      if (l.length != r.length)
//        NotEqualPrimitive(s"${l.length} elements expected but ${r.length} found")
//      else
//        l.zip(r).zipWithIndex.foldLeft[Comparison](Equal) {
//          case (acc, ((l, r), index)) =>
//            val next: Comparison = (l ==== r).prependIndex(index)
//            acc.combine(next)
//        }
//  }
//
//  /** seqEq is not inferir Vector[A] */
//  implicit def vectorEq[A: Eq]: Eq[Vector[A]] = instance {
//    case (l, r) =>
//      if (l.length != r.length)
//        NotEqualPrimitive(s"${l.length} elements expected but ${r.length} found")
//      else
//        l.zip(r).zipWithIndex.foldLeft[Comparison](Equal) {
//          case (acc, ((l, r), index)) =>
//            val next: Comparison = (l ==== r).prependIndex(index)
//            acc.combine(next)
//        }
//  }
//
//  // import cats.data._
//  // implicit def nonEmptyListEq[A: Eq]: Eq[NonEmptyList[A]] = new Eq[NonEmptyList[A]] {
//  //   override def compare(x: NonEmptyList[A], y: NonEmptyList[A]) = {
//  //     val head = (x.head ==== y.head).prependField("head")
//  //     val tail = (x.tail ==== y.tail).prependField("tail")
//  //     head.combine(tail)
//  //   }
//  // }
//
//
// TODO represent set as an Named collection where elemts have empty field name
//  implicit def setEq[A: Eq]: Eq[Set[A]] = instance {
//    case (l, r) =>
//      val leftDiff = l.diff(r)
//      if (leftDiff.isEmpty) {
//        val rightDiff = r.diff(l)
//        if (rightDiff.isEmpty) {
//          Equal
//        } else {
//          NotEqualPrimitive(s"""Left set does not contain: ${rightDiff.mkString(",")}""")
//        }
//      } else {
//        NotEqualPrimitive(s"""Right set does not contain: ${leftDiff.mkString(",")}""")
//      }
//  }
//
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
//}
object eq extends EqInstances0
