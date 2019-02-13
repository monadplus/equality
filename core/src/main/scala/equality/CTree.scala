package equality

sealed trait CTree extends Serializable {
  def isEqual: Boolean
  override def toString: String = CTreePrinter.print(this)
}

case object CUnit extends CTree {
  override def isEqual: Boolean = true
}

case class Primitive(className: String, isEqual: Boolean, error: Option[String] = None) extends CTree
case class Mismatch(reason: String) extends CTree {
  val isEqual: Boolean = false
}

sealed trait Product extends CTree {
  val fields: List[(String, CTree)]

  override def isEqual: Boolean =
    fields.forall(_._2.isEqual)
}
case class Unnamed(fields: List[(String, CTree)])                  extends Product
case class Named(className: String, fields: List[(String, CTree)]) extends Product

case class Coproduct(className: String, tree: CTree) extends CTree {
  override def isEqual: Boolean =
    tree.isEqual
}
