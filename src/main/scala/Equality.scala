import shapeless.labelled.FieldType
import cats.implicits._
import shapeless.{:+:, ::, CNil, Coproduct, HList, HNil, Inl, Inr, LabelledGeneric, Lazy, Witness}

import scala.annotation.implicitNotFound

object Equality extends App {

  sealed trait Comparison extends Product with Serializable {
    def addPrefix(prefix: String): Comparison = this match {
      case Equal                         => Equal
      case NotEqual(differences)         => NotEqual(differences.map { case (k, v) => (prefix + k) -> v })
      case NotEqualPrimitive(difference) => NotEqual(Map(prefix -> difference))
    }

    override def toString: String = this match {
      case Equal =>
        "[✔]  Equals"
      case NotEqualPrimitive(difference) =>
        s"[✕]︎ Divergence: $difference"
      // TODO: define a proper toString :)
      case NotEqual(differences) =>
        s"[✕]︎ Divergence: $differences"
    }
  }
  case object Equal extends Comparison
  type Path = String
  case class NotEqual(differences: Map[Path, String]) extends Comparison
  case class NotEqualPrimitive(difference: String)    extends Comparison

  @implicitNotFound(
    """Cannot find an implicit value for Eq[${A}]:
  * Primitive types e.g. Int, Long, Float, Double, etc. must have an explicit written instance in scope.
  * Non primitive types must be written by hand e.g. List, Option, Map, etc.
  """
  )
  trait Eq[A] {
    def compare(a: A, a2: A): Comparison
  }

  object Eq {
    def apply[A](implicit eq: Eq[A]): Eq[A] = eq

    def ====[A](a: A, a2: A)(implicit eq: Eq[A]): Comparison =
      eq.compare(a, a2)

    def unit[A]: Eq[A] = new Eq[A] {
      override def compare(a: A, a2: A): Comparison = Equal
    }

    implicit val stringEq: Eq[String] = eqInstance {
      case (s1, s2) =>
        if (s1 == s2) Equal else NotEqualPrimitive(s"$s1 not equal to $s2")
    }

    // TODO: not sure about this
    implicit def primitiveEq[A <: AnyVal]: Eq[A] = eqInstance {
      case (s1, s2) =>
        if (s1 == s2) Equal else NotEqualPrimitive(s"$s1 not equal to $s2")
    }
  }

  def eqInstance[A](f: (A, A) => Comparison): Eq[A] = new Eq[A] {
    override def compare(a: A, a2: A): Comparison =
      f(a, a2)
  }

  implicit val hnilEq: Eq[HNil] =
    Eq.unit[HNil]

  implicit def hlistEq[H, K <: Symbol, T <: HList](implicit
                                                   witness: Witness.Aux[K],
                                                   hEq: Lazy[Eq[H]],
                                                   tEq: Eq[T]): Eq[FieldType[K, H] :: T] =
    eqInstance {
      case (l, r) =>
        val fieldName = witness.value.name
        val head = hEq.value.compare(l.head, r.head) match {
          case Equal =>
            Equal
          case primitive: NotEqualPrimitive =>
            primitive.addPrefix(fieldName)
          case _ =>
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
    eqInstance { case (_, _) => throw new Exception("Inconceivable") }

  implicit def coproductEq[H, K <: Symbol, T <: Coproduct](implicit
                                                           witness: Witness.Aux[K],
                                                           hEq: Lazy[Eq[H]],
                                                           tEq: Eq[T]): Eq[FieldType[K, H] :+: T] = {
    val fieldName = witness.value.name
    eqInstance {
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
    eqInstance {
      case (v1, v2) =>
        eq.value.compare(gen.to(v1), gen.to(v2))
    }

  case class Dog(name: String)
  val max   = Dog(name = "Max")
  val bella = Dog(name = "Bella")

  import Eq._

  List(
    Eq[Dog].compare(max, max),
    Eq[Dog].compare(max, bella),
    Eq[Dog].compare(bella, max)
  ).foreach(println(_))

  sealed trait Shape
  case class Circle(radius: Double)   extends Shape
  case class Square(diagonal: Double) extends Shape
  val circle: Shape  = Circle(1.0)
  val circle2: Shape = Circle(2.0)
  val square: Shape  = Square(1.0)
  val square2: Shape = Square(2.0)

  List(
    Eq[Shape].compare(circle, circle),
    Eq[Shape].compare(circle, circle2),
    Eq[Shape].compare(square, square),
    Eq[Shape].compare(square, square2),
    Eq[Shape].compare(circle, square),
    Eq[Shape].compare(square, circle)
  ).foreach(println(_))

  case class Shapes(name: String, shape: Shape)
  val shapes0 = Shapes("one", circle)
  val shapes1 = Shapes("two", circle2)
  val shapes2 = Shapes("one", square)
  List(
    Eq[Shapes].compare(shapes0, shapes0),
    // TODO: line 84 is throwing
    Eq[Shapes].compare(shapes0, shapes1),
//    Eq[Shapes].compare(shapes0, shapes2),
  ).foreach(println(_))
}
