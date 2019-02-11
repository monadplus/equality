package core

// TODO: Replace String for a parametrized type

sealed trait Path extends Product with Serializable

// Path in a product type
final case class Field(name: String, next: Path) extends Path
// Path in a coproduct type
final case class Choice(choice: String, next: Path) extends Path

// *** Special case ***

// Sequence-like classes
final case class Index(i: Int, next: Path) extends Path
final case class InSet(next: Path)         extends Path
// KVS classes
final case class Key(k: String, next: Path) extends Path

case object End extends Path
