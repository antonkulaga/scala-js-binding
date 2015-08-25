package org.denigma.controls.selection

import org.denigma.binding.views.{BindableView, ItemsSetView}
import org.scalajs.dom.raw.HTMLElement
import rx.core.Var


case class TextSelection(value:String,label:String,position:Int) extends Selection[String]

trait Selection[T]{
  def value:T
  def label:T
}

trait TextSelectionView extends ItemsSetView{

  type Item = Var[TextSelection]
  type ItemView = OptionView

  implicit val ordering:Ordering[Item] = new Ordering[Item]{
    override def compare(x: Var[TextSelection], y: Var[TextSelection]): Int = if(x.now.position<y.now.position)
      -1 else if(x.now.position>y.now.position) 1 else 0
  }


  override def newItem(item: Var[TextSelection]): OptionView =  this.constructItemView(item){case (el,mp)=>
    new OptionView(el,item,mp)
  }

}

