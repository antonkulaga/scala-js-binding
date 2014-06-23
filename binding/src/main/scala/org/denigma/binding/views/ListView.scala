package org.denigma.binding.views

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._
import rx.{Rx, Var}

import scala.collection.immutable._
import scalatags.Text.Tag

object ListView {

  /**
   * View that is created for the items in case if no other view was specified
   * @param el
   * @param props properties to bind to
   */
  class JustMapView(el:HTMLElement,props:Map[String,Any]) extends MapView("justmapview",el,props){
    override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

    override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

    override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

    override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)
  }


  def apply(el:HTMLElement,props:Map[String,Any]):MapView = {
    new JustMapView(el,props)
  }
}




abstract class ListView(val name:String,val elem:HTMLElement, val params:Map[String,Any]) extends OrdinaryView
  //with CollectionBinding
  with CollectionView
{
  //val key = params.get("items").getOrElse("items").toString

  val disp = elem.style.display

  override type Item = Map[String,Any]
  override type ItemView = MapView

  /**
   * Creates new viewlist item
   * @param mp
   * @return
   */
  def newItem(mp:Item):ItemView = {
    val el = template.cloneNode(true).asInstanceOf[HTMLElement]
    val view: MapView = el.attributes.get("item-view") match {
      case None=> ListView.apply(el,mp)
      case Some(v)=> this.inject(v.value,el,mp) match {
        case item:MapView=> item
        case _=>
          dom.console.error(s"view ${v.value} exists but does not inherit MapView")
          ListView.apply(el,mp)
      }
    }
    view
  }


}