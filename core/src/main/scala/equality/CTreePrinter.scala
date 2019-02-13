package equality

// Example:
//
// ✕ Dog
//     ├── ✕ breed: Chihuahua
//     │                  ├── ✔ hasPedigree: Boolean
//     │                  ├── ✔ color: Brown
//     │                  └── ✕ state: String [California did not equal to Louisiana]
//     └── ✔ owner: Owner
//                    ├── ✔ name: String
//                    └── ✔ age: Int

object CTreePrinter {
  type Matrix = Map[Int, List[Char]]
  type Fields = List[(String, CTree)]
  type Offset = Int

  implicit private class ListOps[A](self: List[A]) {
    def insert(a: A, at: Int): List[A] = {
      val (l, r) = self.splitAt(at)
      (l :+ a) ++ r.drop(1)
    }
  }

  private def mark(ct: CTree): String =
    if (ct.isEqual) "✔"
    else "✕"

  def print(ct: CTree): String = {

    def tree(curr: Matrix, fields: Fields, height: Int, width: Int, fromNamed: Boolean = false): (Matrix, Int) = {
      val whitespaces = " " * (width - 1 /* ├ character is displayed 1 character to the right*/)
      val prefix      = (idx: Int) => if (idx == fields.length - 1) "└──" else "├──"
      fields.zipWithIndex.foldLeft[(Matrix, Int)]((curr, height)) {
        case ((acc, height), ((fieldName, field), index)) =>
          val text               = whitespaces ++ s"${prefix(index)} ${mark(field)} $fieldName"
          val matrix: Matrix     = acc + (height -> text.toList)
          val (matrix2, height2) =
            (field, fromNamed) match {
              case (Named(className, fields), true) =>
                val prefix = s": $className"
                loop(Unnamed(fields), addText(matrix, height, prefix), height, text.length + prefix.length - (className.length / 2))
              case _ =>
                loop(field, matrix, height, text.length)
            }
          val matrix3 =
            if (index == fields.length - 1) matrix2
            else {
              List.fill(height2 - height)('│').zipWithIndex.foldLeft(matrix2) {
                case (acc, (c, i)) =>
                  val index = i + 1 // Skip our current line
                  acc + (index -> acc(index).insert(c, width))
              }
            }
          matrix3 -> (height2 + 1/*next line*/)
      }
    }

    def addText(acc: Matrix, height: Int, text: String): Matrix =
      acc + (height -> (acc.getOrElse(height, Nil) ++ text.toList))

    def loop(c: CTree, acc: Matrix, height: Int, width: Int): (Matrix, Int /*branch height*/ ) = c match {
      case Named(className, fields) =>
        val prefix = s" ${mark(c)} "
        val next = addText(acc, height, prefix ++ className)
        tree(next, fields, height + 1, width + prefix.length + (className.length / 2), fromNamed = true)
      case Unnamed(fields) =>
        tree(acc, fields, height + 1, width)
      case Coproduct(className, c) =>
        val prefix = ": "
        val next   = addText(acc, height, prefix ++ className)
        loop(c, next, height, width + prefix.length + (className.length / 2))
      case Mismatch(reason) =>
        addText(acc, height, s": [$reason]") -> height
      case Primitive(className, isEqual, error) =>
        val msg = if (!isEqual) s" [${error.get}]" else ""
        addText(acc, height, s": $className" ++ msg) -> height
      case CUnit =>
        (acc, height)
    }

    loop(ct, Map.empty, 0, 0)._1.values.map(_.mkString("")).mkString("\n")
  }
}
