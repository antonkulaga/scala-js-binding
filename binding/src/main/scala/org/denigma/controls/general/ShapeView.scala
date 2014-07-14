package org.denigma.controls.general

import org.denigma.binding.semantic.ActiveModelView
import org.denigma.binding.views.{CollectionView, IView, OrdinaryView}
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex.{ArcRule, Shape}
import rx.Rx
import rx.core.Var

trait ShapedModelView extends ShapeView with ActiveModelView {

}

case class ArcView(arc:ArcRule,viewElement:HTMLElement, parent:ShapeView) extends IView{

  override def id: String = arc.toString

  override def unbindView(): Unit = {
    this.parent.unbind(viewElement)
  }


  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  override def bindView(el: HTMLElement): Unit = {
    parent.bind(el)
  }
}

trait ShapeView extends OrdinaryView with CollectionView
{
  def shape:Var[Shape]


  override type Item = ArcRule
  override type ItemView = ArcView



  override def newItem(item:Item):ItemView =
  {
    //dom.console.log(template.outerHTML.toString)
    val el = template.cloneNode(true).asInstanceOf[HTMLElement]
    //el.removeAttribute("data-template")
    ArcView(item,el,this)
  }


  override val items: Rx[List[ArcRule]] = Rx{this.shape().arcRules().sortWith{
    case (a,b)=> (a.priority,b.priority) match {
      case (Some(ap),None)=>true
      case (Some(ap),Some(bp))=>ap > bp
      case (None,Some(bp))=>false
      case (None,None)=>true

    }
    }
  }


}
