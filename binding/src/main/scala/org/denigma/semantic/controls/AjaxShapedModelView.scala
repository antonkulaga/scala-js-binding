package org.denigma.semantic.controls

import org.denigma.binding.views.CollectionView
import org.denigma.semantic.binding.ModelInside
import org.scalajs.dom
import org.scalajs.dom.extensions._
import org.scalajs.dom.{HTMLElement, MouseEvent}
import org.scalax.semweb.shex.{ArcRule, Shape}
import rx.ops._
import rx.{Rx, Var}

import scala.collection.immutable.Map
import scalatags.Text.Tag

object PropertyView {
  def apply(el:HTMLElement,mp:Map[String,Any]) = {
    new JustPropertyView(el,mp)
  }

}

class JustPropertyView(val elem:HTMLElement,val params:Map[String,Any]) extends PropertyView {
  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def mouseEvents: Predef.Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)


}

/**
 * View to display property and its name
 * TODO: refactor in Future
 */
trait PropertyView extends SelectableModelView {

  require(params.contains("arc"),"Property view should contain ArcRule")

  require(params.contains("model"),"PropertyView should contain Model")

  val arc = params("arc").asInstanceOf[ArcRule]


  override val modelInside:Var[ModelInside]= params("arc").asInstanceOf[Var[ModelInside]]

  override protected def bindRdf(el: HTMLElement) = {
     this.updatePrefixes(el)


    def binded(str:String) = str.contains("data") && str.contains("bind")

    val ats = el.attributes.collect { case (key, value) if !binded(value.value) => (key, value.value.toString)}.toMap

    ats.foreach { case (key, value) =>
      this.rdfPartial(el, key, value,ats).orElse(this.propertyPartial(el, key, value,ats)).orElse(otherPartial)(key)


    }
  }

  protected def propertyPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] = {
    case "data" if value=="title" =>
      //this.bindProperty()
      //TODO: finish
      ???

    case "data" if value == "property" =>
     // this.bindrdfPa
    //TODO: finish
    ???



  }



}

abstract class AjaxShapedModelView extends SelectableModelView with  CollectionView{


  type ItemView =  PropertyView

  lazy val shape:Var[Shape] = this.nearestParentOf[Var[Shape]]{case col:AjaxModelCollection=>col.shape}.get

  override type Item = ArcRule

  override def newItem(item:Item):ItemView =
  {
    //dom.console.log(template.outerHTML.toString)
    val el = template.cloneNode(true).asInstanceOf[HTMLElement]

    el.removeAttribute("data-template")
    val mp: Map[String, Any] = Map[String,Any]("shape"->shape, "arc"->item, "model"->this.modelInside)

    val view: ItemView = el.attributes.get("data-item-view") match {
      case None=> null

      case Some(v)=> this.inject(v.value,el,mp) match {
        case iv:ItemView=> iv
        case iv if iv.isInstanceOf[PropertyView]=> iv.asInstanceOf[PropertyView]
        case _=>
          dom.console.error(s"view ${v.value} exists but does not inherit ItemView")
          PropertyView.apply(el,mp)
      }
    }
    view
  }




  override val items: Rx[List[ArcRule]] =  shape.map(sh=>sh.arcSorted())
}
