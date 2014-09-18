package org.denigma.semantic.shapes

import org.denigma.binding.views.CollectionView
import org.denigma.semantic.models.RemoteModelView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.dom.extensions._
import org.scalax.semweb.shex.ArcRule
import rx.ops._

import scala.collection.immutable.Map

object ShapedModelView {



}


abstract class ShapedModelView(val elem:HTMLElement,val params:Map[String,Any]) extends RemoteModelView with  CollectionView{


  type ItemView =  PropertyView


  override type Item = ArcRule

  override def newItem(item:Item):ItemView =
  {
    //dom.console.log(template.outerHTML.toString)
    val el = template.cloneNode(true).asInstanceOf[HTMLElement]
    el.removeAttribute("data-template")

    val mp: Map[String, Any] = Map[String,Any]("shape"->shape, "arc"->item, "model"->this.model)

    val view: ItemView = el.attributes.get("data-item-view") match {
      case None=> PropertyView(el,mp)

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

  override val items =  shape.map(sh=>sh.current.arcSorted())

  override def bindView(el:HTMLElement) = {
    super.bindView(el)
    this.subscribeUpdates()
  }


}
