package core

sealed trait Comparison extends Product with Serializable {
  def prependField(fieldName: String): Comparison =
    prepend(Field(fieldName, _))

  def prependChoice(choice: String): Comparison =
    prepend(Choice(choice, _))

  private def prepend(f: Path => Path): Comparison = this match {
    case Equal                         => Equal
    case NotEqualPrimitive(difference) => NotEqual(Map(f(End) -> difference))
    case NotEqual(differences)         => NotEqual(differences.map { case (k, v) => f(k) -> v })
  }

  override def toString: String = this match {
    case Equal =>
      "[✔]  Equals"
    case NotEqualPrimitive(difference) =>
      s"[✕]︎ $difference"
    // TODO  
    case NotEqual(differences) =>
      differences.toString
  }
}
case object Equal                                         extends Comparison
final case class NotEqual(differences: Map[Path, String]) extends Comparison
final case class NotEqualPrimitive(difference: String)    extends Comparison
