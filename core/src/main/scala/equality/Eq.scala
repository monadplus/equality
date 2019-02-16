package equality

import shapeless.labelled.FieldType
import shapeless.{:+:, ::, CNil, HList, Coproduct => ShapelessCoproduct, HNil, Inl, Inr, LabelledGeneric, Lazy, Witness}

import scala.annotation.implicitNotFound

@implicitNotFound("""Cannot find an implicit value for Eq[${A}]:
  * You may have forgotten to import primitive instances.
  """)
trait Eq[A] {
  def compare(a: A, a2: A): CTree
}

object Eq {
  def apply[A](implicit eq: Eq[A]): Eq[A] = eq

  def instance[A](f: (A, A) => CTree): Eq[A] = new Eq[A] {
    override def compare(a: A, a2: A): CTree =
      f(a, a2)
  }

  def unit[A]: Eq[A] =
    instance { case _ => CUnit }

  implicit val hnilEq: Eq[HNil] =
    Eq.unit[HNil]

  implicit def hlistEq[H, K <: Symbol, T <: HList](
    implicit
    witness: Witness.Aux[K],
    hEq: Lazy[Eq[H]],
    tEq: Eq[T]
  ): Eq[FieldType[K, H] :: T] =
    instance {
      case (l, r) =>
        val fieldName = witness.value.name
        val head      = fieldName -> hEq.value.compare(l.head, r.head)
        tEq.compare(l.tail, r.tail) match {
          case CUnit          => Unnamed(List(head))
          case Unnamed(nodes) => Unnamed(head :: nodes)
          case _              => throw new Exception("Inconceivable")
        }
    }

  implicit val cnilEq: Eq[CNil] =
    instance { case (_, _) => throw new Exception("Inconceivable") }

  implicit def coproductEq[H, K <: Symbol, T <: ShapelessCoproduct](
    implicit
    witness: Witness.Aux[K],
    hEq: Lazy[Eq[H]],
    tEq: Eq[T]
  ): Eq[FieldType[K, H] :+: T] = {
    instance {
      case (Inl(h), Inl(h2)) =>
        val tree = hEq.value.compare(h, h2) match {
          case Named(_, fields, _) => Unnamed(fields)
          case id               => id
        }
        Coproduct(witness.value.name, tree)
      case (Inr(t), Inr(t2)) =>
        tEq.compare(t, t2)
      case (Inl(l), Inr(Inl(r))) =>
        Mismatch(s"${l.getClass.getSimpleName} expected but ${r.getClass.getSimpleName} found")
      case (Inr(Inl(l)), Inl(r)) =>
        Mismatch(s"${l.getClass.getSimpleName} expected but ${r.getClass.getSimpleName} found")
      case _ =>
        throw new Exception("Inconceivable")
    }
  }

  implicit def genericEq[A, H](
    implicit
    gen: LabelledGeneric.Aux[A, H],
    eq: Lazy[Eq[H]]
  ): Eq[A] =
    instance {
      case (v1, v2) =>
        eq.value.compare(gen.to(v1), gen.to(v2)) match {
          case Unnamed(fields) => Named(v1.getClass.getSimpleName, fields)
          case id              => id
        }
    }
}
