package org.denigma.controls.selection


import rx.core.Var


object TextSelection{
  implicit val varOrdering:Ordering[rx.core.Var[TextSelection]] = new Ordering[Var[TextSelection]]{
    override def compare(x: Var[TextSelection], y: Var[TextSelection]): Int = if(x.now.position<y.now.position)
      -1 else if(x.now.position>y.now.position) 1 else 0
  }
  implicit val selectionOrdering:Ordering[TextSelection] = new Ordering[TextSelection]{
    override def compare(x: TextSelection, y: TextSelection): Int = if(x.position<y.position)
      -1 else if(x.position>y.position) 1 else 0
  }

}
case class TextSelection(value:String,label:String)(val position:Int) extends Selection[String]

trait Selection[T]{
  def value:T
  def label:T
}
