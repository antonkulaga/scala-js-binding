package org.denigma.semantic.shapes

import org.denigma.binding.extensions.sq
import org.denigma.binding.views.{CollectionView, IView, OrdinaryView}
import org.denigma.semantic.binding.ChangeSlot
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex.{ArcRule, Shape}
import rx.Rx
import rx.core.Var
import rx.ops._


object ShapeInside {

  def apply(initial:Shape):ShapeInside = ShapeInside(initial,initial)

}

case class ShapeInside(initial:Shape,current:Shape,wantsToDie:Boolean = false) extends ChangeSlot
{
  override type Value = Shape
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


  protected def getShape:Shape = {
    require(params.contains("shape"),"ShapeView must contain shape in params")
    this.params("shape").asInstanceOf[Shape]
  }

  lazy val shapeInside: Var[ShapeInside] = Var(ShapeInside(this.getShape))


  override type Item = ArcRule
  override type ItemView = ArcView



  override def newItem(item:Item):ItemView =
  {
    //dom.console.log(template.outerHTML.toString)
    val el = template.cloneNode(true).asInstanceOf[HTMLElement]
    //el.removeAttribute("data-template")
    ArcView(item,el,this)
  }


  val items: Rx[List[ArcRule]] = this.shapeInside.map(shi=>shi.current.arcSorted())


}

