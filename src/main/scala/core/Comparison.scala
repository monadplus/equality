package core

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
case object Equal                                           extends Comparison
final case class NotEqual(differences: Map[String, String]) extends Comparison
final case class NotEqualPrimitive(difference: String)      extends Comparison
