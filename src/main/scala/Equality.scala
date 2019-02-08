import shapeless.labelled.FieldType
import cats.implicits._
import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Witness}

import scala.annotation.implicitNotFound

object Equality extends App {
  // TODO: define a proper toString :)
  sealed trait Comparison extends Product with Serializable {
    def addPrefix(prefix: String): Comparison = this match {
      case Equal                         => Equal
      case NotEqual(differences)         => NotEqual(differences.map { case (k, v) => (prefix + k) -> v })
      case NotEqualPrimitive(difference) => NotEqual(Map(prefix -> difference))
    }
  }
  case object Equal extends Comparison
  type Path = String
  case class NotEqual(differences: Map[Path, String]) extends Comparison
  case class NotEqualPrimitive(difference: String)    extends Comparison

  @implicitNotFound("""Cannot find an implicit value for Eq[${A}]:
  * You must create implicit instances for primitive types such as String, Long, etc...
  """)
  trait Eq[A] {
    def compare(a: A, a2: A): Comparison
  }

  object Eq {
    def apply[A](implicit eq: Eq[A]): Eq[A] = eq

    def ====[A](a: A, a2: A)(implicit eq: Eq[A]): Comparison =
      eq.compare(a, a2)

    implicit val stringEq: Eq[String] = eqInstance {
      case (s1, s2) =>
        if (s1 == s2) Equal else NotEqualPrimitive(s"[String] $s1 not equal to $s2")
    }
  }

  def eqInstance[A](f: (A, A) => Comparison): Eq[A] = new Eq[A] {
    override def compare(a: A, a2: A): Comparison =
      f(a, a2)
  }

  implicit val hnilEq: Eq[HNil] =
    eqInstance { case (_, _) => Equal }

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

  println(Eq[Dog].compare(max, bella))
  println(Eq[Dog].compare(max, max))
}
