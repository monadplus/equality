package equality

object CTreePrinter {
  type Matrix = Map[Int, List[Char]]
  type Fields = List[(String, CTree)]
  type Offset = Int
  val MAX_CONTENT_SIZE = 100 // characters

  implicit private class ListOps[A](self: List[A]) {
    def insert(a: A, at: Int): List[A] = {
      val (l, r) = self.splitAt(at)
      (l :+ a) ++ r.drop(1)
    }
  }

  private def mark(ct: CTree, force: Option[Boolean] = None): String = {
    val isEqual = (b: Boolean) => if (b) "✔" else "✕"
    force match {
      case Some(eq) =>
        isEqual(eq)
      case None =>
        isEqual(ct.isEqual)
    }
  }

  def print(ct: CTree): String = {

    def tree(curr: Matrix, fields: Fields, height: Int, width: Int): (Matrix, Int) = {
      val prefix      = (idx: Int) => if (idx == fields.length - 1) "└──" else "├──"
      val whitespaces = " " * width
      fields.zipWithIndex.foldLeft[(Matrix, Int)]((curr, height)) {
        case ((acc, h), ((fieldName, field), index)) =>
          val height             = h + 1 /*next line*/
          val text               = whitespaces ++ s"${prefix(index)} ${mark(field)} $fieldName: "
          val matrix: Matrix     = acc + (height -> text.toList)
          val (matrix2, height2) = loop(field, matrix, height, text.length)
          val matrix3 =
            if (index == fields.length - 1) matrix2
            else {
              (height to height2).foldLeft(matrix2) {
                case (acc, index) =>
                  if (index == height) acc /*skip current line*/
                  else acc + (index -> acc(index).insert('│', width))
              }
            }
          matrix3 -> height2
      }
    }

    def addText(acc: Matrix, height: Int, text: String): Matrix =
      acc + (height -> (acc.getOrElse(height, Nil) ++ text.toList))

    def primitiveContentAsString(p: Primitive): String = {
      val Primitive(className, _, content) = p
      val sanitized                        = if (content.length > MAX_CONTENT_SIZE && className != "Map") "[...]" else content
      s"$className [$sanitized]"
    }

    def loop(c: CTree, acc: Matrix, height: Int, width: Int): (Matrix, Int /*branch height*/ ) = c match {
      case Named(className, fields, force) =>
        val prefix = if (height == 0) s"${mark(c, force)} " else ""
        val next   = addText(acc, height, prefix ++ className)
        tree(next, fields, height, width + prefix.length + (className.length / 2))
      case Unnamed(fields) =>
        tree(acc, fields, height, width)
      case Large(className, _, ne) =>
        val text: String =
          if (ne.isEmpty) s"$className (too large)"
          else s"$className (too large: ${ne.length} elements are not equal)"
        addText(acc, height, text) -> height
      case Coproduct(className, c) =>
        val prefix = if (height == 0) mark(c) + " " else ""
        val next   = addText(acc, height, prefix + className)
        loop(c, next, height, width + prefix.length + (className.length / 2))
      case Mismatch(reason) =>
        val text = if (height == 0) reason else s"[$reason]"
        addText(acc, height, text) -> height
      case p: Primitive =>
        val text = primitiveContentAsString(p)
        addText(acc, height, text) -> height
      case CUnit =>
        (acc, height)
    }

    loop(ct, Map.empty, 0, 0)._1.toList.sortBy(_._1).map(_._2.mkString("")).mkString("\n")
  }
}
