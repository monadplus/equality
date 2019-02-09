package core

import shapeless.labelled.FieldType
import cats.implicits._
import shapeless.{:+:, ::, CNil, Coproduct, HList, HNil, Inl, Inr, LabelledGeneric, Lazy, Witness}

import scala.annotation.implicitNotFound

@implicitNotFound("""Cannot find an implicit value for Eq[${A}]:
  * Primitive types e.g. Int, Long, Float, Double, etc. must have an explicit written instance in scope.
  * Non primitive types must be written by hand e.g. List, Option, Map, etc.
  """)
trait Eq[A] {
  def compare(a: A, a2: A): Comparison
}

object Eq {
  def apply[A](implicit eq: Eq[A]): Eq[A] = eq

  def unit[A]: Eq[A] = new Eq[A] {
    override def compare(a: A, a2: A): Comparison = Equal
  }

  def instance[A](f: (A, A) => Comparison): Eq[A] = new Eq[A] {
    override def compare(a: A, a2: A): Comparison =
      f(a, a2)
  }

  implicit val hnilEq: Eq[HNil] =
    Eq.unit[HNil]

  implicit def hlistEq[H, K <: Symbol, T <: HList](implicit
                                                   witness: Witness.Aux[K],
                                                   hEq: Lazy[Eq[H]],
                                                   tEq: Eq[T]): Eq[FieldType[K, H] :: T] =
    instance {
      case (l, r) =>
        val fieldName = witness.value.name
        val head = hEq.value.compare(l.head, r.head) match {
          case Equal =>
            Equal
          // TODO: si no es primitiu afegir un nivell de profundidad
          case primitive: NotEqualPrimitive =>
            primitive.addPrefix(fieldName)
          case _ =>
            // TODO: aqui pot ser primitiu, no afegir profunditat
            throw new Exception("Inconceivable")
        }
        val tail = tEq.compare(l.tail, r.tail).addPrefix(fieldName)
        (head, tail) match {
          case (Equal, y)                   => y
          case (x, Equal)                   => x
          case (NotEqual(d1), NotEqual(d2)) => NotEqual(d1 |+| d2)
          case _                            => throw new Exception("Inconceivable")
        }
    }

  implicit val cnilEq: Eq[CNil] =
    instance { case (_, _) => throw new Exception("Inconceivable") }

  implicit def coproductEq[H, K <: Symbol, T <: Coproduct](implicit
                                                           witness: Witness.Aux[K],
                                                           hEq: Lazy[Eq[H]],
                                                           tEq: Eq[T]): Eq[FieldType[K, H] :+: T] = {
    val fieldName = witness.value.name
    instance {
      case (Inl(h), Inl(h2)) =>
        hEq.value.compare(h, h2).addPrefix(fieldName)
      case (Inr(t), Inr(t2)) =>
        tEq.compare(t, t2).addPrefix(fieldName)
      case (Inl(l), Inr(r)) =>
        NotEqualPrimitive(s"Expected: ${l.getClass} but found ${r.getClass}")
      case (Inr(l), Inl(r)) =>
        NotEqualPrimitive(s"Expected: ${l.getClass} but found ${r.getClass}")
    }
  }

  implicit def genericEq[A, H](
    implicit
    gen: LabelledGeneric.Aux[A, H],
    eq: Lazy[Eq[H]]
  ): Eq[A] =
    instance {
      case (v1, v2) =>
        eq.value.compare(gen.to(v1), gen.to(v2))
    }
}
