package core

sealed trait Path extends Product with Serializable

// Path in a product type
final case class Field(name: String, next: Path) extends Path
// Path in a coproduct type
final case class Choice(choice: String, next: Path) extends Path

case object End extends Path