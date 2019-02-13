package core

import cats.implicits._


sealed trait Comparison extends Product with Serializable {
  private[core] def prependField(fieldName: String): Comparison =
    prepend(Field(fieldName, _))

  private[core] def prependChoice(choice: String): Comparison =
    prepend(Choice(choice, _))

  private[core] def prependIndex(index: Int): Comparison =
    prepend(Index(index, _))

  private[core] def prependInSet: Comparison =
    prepend(InSet)

  private[core] def prependKey(k: String): Comparison =
    prepend(Key(k, _))

  private def prepend(f: Path => Path): Comparison = this match {
    case Equal                         => Equal
    case NotEqualPrimitive(difference) => NotEqual(Map(f(End) -> difference))
    case NotEqual(differences)         => NotEqual(differences.map { case (k, v) => f(k) -> v })
  }

  private[core] def combine(other: Comparison): Comparison = (this, other) match {
    case (Equal, y)                   => y
    case (x, Equal)                   => x
    case (NotEqual(d1), NotEqual(d2)) => NotEqual(d1 |+| d2)
    case _                            => throw new Exception("NotEqualPrimitive can't be combined")
  }

  override def toString: String =
    print

  def print(implicit config: PrintConfig = TreeStructure): String =
    this match {
      case Equal =>
        "[✔]  Equals"
      case NotEqualPrimitive(difference) =>
        s"[✕]︎ $difference"
      case NotEqual(d) =>
        config match {
          case TreeStructure =>
            d.toList
              .map {
                case (p, e) =>
                  printTree(p, e)
              }
              .mkString("\n\n")
        }
    }

  /*
    Field(x, Choice(A, Field(y, End))) -> asdf did not equal asd

    [x]
      └── x (A)
              └── y: asdf did not equal asd
   */
  def printTree(p: Path, error: String): String = {
    def addEnd(p: Path): String =
      p match {
        case End => " FAILED"
        case _   => ""
      }
    @scala.annotation.tailrec
    def loop(width: Int, p: Path, acc: String): String = p match {
      case End =>
        val spaces = Math.max(0, (width - error.length)) / 2
        acc + "\n\n" + (" " * spaces) + error
      case Field(name, next) =>
        val offset  = 2
        val postfix = "└── " + name + addEnd(next)
        val text    = acc + "\n" + (" " * (width - offset)) + postfix
        val w       = Math.max(0, width + postfix.length - offset)
        loop(w, next, text)
      case Choice(name, next) =>
        val postfix = " (" + name + ")" + addEnd(next)
        val text    = acc + postfix
        loop(width + postfix.length, next, text)
      case Index(index, next) =>
        val postfix = " (idx: " + index.toString + ")" + addEnd(next)
        val text    = acc + postfix
        loop(width + postfix.length, next, text)
      case InSet(next) =>
        val postfix = " (set)" + addEnd(next)
        val text    = acc + postfix
        loop(width + postfix.length, next, text)
      case Key(k, next) =>
        val postfix = " (key: " + k + ")"  + addEnd(next)
        val text    = acc + postfix
        loop(width + postfix.length, next, text)
    }

    val initial = "root"
    loop(initial.length, p, initial)
  }
}

case object Equal                                         extends Comparison
final case class NotEqual(differences: Map[Path, String]) extends Comparison
final case class NotEqualPrimitive(difference: String)    extends Comparison

sealed trait PrintConfig
case object TreeStructure extends PrintConfig

