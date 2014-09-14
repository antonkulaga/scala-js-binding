package org.denigma.semantic.shapes

import org.denigma.binding.views.CollectionView
import org.denigma.semantic.models.SelectableModelView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.dom.extensions._
import org.scalax.semweb.shex.ArcRule
import rx.Var

import scala.collection.immutable.Map
//
object PropertyView {
  def apply(el:HTMLElement,mp:Map[String,Any]) = {
    new JustPropertyView(el,mp)
  }

}

class JustPropertyView(val elem:HTMLElement,val params:Map[String,Any]) extends PropertyView
{
    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit = {
    //binders = new PropertyBinder(elem,this.model,arc,)::Nil
    //NOT IMPLEMENTED
  }
}

/**
* View to display property and its name
* TODO: refactor in Future
*/
trait PropertyView extends SelectableModelView {

  require(params.contains("arc"),"Property view should contain ArcRule")

  require(params.contains("model"),"PropertyView should contain Model")

  val arc = params("arc").asInstanceOf[ArcRule]


}

abstract class AjaxShapedModelView extends SelectableModelView with  CollectionView{


  type ItemView =  PropertyView


  override type Item = ArcRule

  override def newItem(item:Item):ItemView =
  {
    //dom.console.log(template.outerHTML.toString)
    val el = template.cloneNode(true).asInstanceOf[HTMLElement]

    el.removeAttribute("data-template")
    val mp: Map[String, Any] = Map[String,Any]("shape"->shape, "arc"->item, "model"->this.model)

    val view: ItemView = el.attributes.get("data-item-view") match {
      case None=> null

      case Some(v)=> this.inject(v.value,el,mp) match {
        case iv:ItemView=> iv
        //case iv if iv.isInstanceOf[PropertyView]=> iv.asInstanceOf[PropertyView]
        case _=>
          dom.console.error(s"view ${v.value} exists but does not inherit ItemView")
          PropertyView.apply(el,mp)
      }
    }
    view
  }


  override val items =  Var(shape.map(sh=>sh.arcSorted()).getOrElse(Nil))
}
