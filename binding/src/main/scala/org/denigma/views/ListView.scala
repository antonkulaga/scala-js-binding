package org.denigma.views

import org.scalajs.dom._
import org.scalajs.dom
import rx.{Var, Rx}
import scalatags.HtmlTag
import org.denigma.binding.CollectionBinding
import scala.collection.immutable._
import scala.Some
import org.scalajs.dom.extensions._
import org.denigma.extensions._

object ListView {

  /**
   * View that is created for the items in case if no other view was specified
   * @param el
   * @param props properties to bind to
   */
  class JustMapView(el:HTMLElement,props:Map[String,Any]) extends MapView("justmapview",el,props){
    override def tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

    override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

    override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

    override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)
  }


  def apply(el:HTMLElement,props:Map[String,Any]):MapView = {
    new JustMapView(el,props)
  }
}





abstract class ListView(name:String,element:HTMLElement, params:Map[String,Any]) extends OrdinaryView(name,element) with CollectionBinding
{
  val key = params.get("items").getOrElse("items").toString

  val disp = element.style.display

  val template: HTMLElement = this.extractTemplate()

  /**
   * Extracts item template
   * @return
   */
  protected def extractTemplate(): HTMLElement =   element.childNodes.collectFirst{
    case n:HTMLElement if n.attributes.contains("data-template")=>n}.getOrElse {
      element.childNodes.collectFirst {
        case element: HTMLElement => element
      }.getOrElse(element)
    }



  /**
   * Creates new viewlist item
   * @param mp
   * @return
   */
  def newItem(mp:Map[String, Any]) = {
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

  /**
   * Creates view
   * @param el html element for which the view is created
   * @param viewAtt attribute
   * @return view
   */
  protected def createView(el:HTMLElement,viewAtt:dom.Attr,item:Map[String,Any]) =     {

    val v = this.inject(viewAtt.value,el,params)
    v.parent = Some(this)
    v.bindView(el)
    this.addView(v) //the order is intentional
    v

  }

  //TODO: maybe dangerous!!!
  /**
   *
   * @param el
   */
  override def bindView(el:HTMLElement) = {

    val id = "items_of_"+this.element.id
    val span: HTMLElement = sq.byId(id) match {
      case Some(el)=>el
      case None=>
        val sp = document.createElement("span")
        sp.id = id
        if(template==element) element.appendChild(sp) else element.replaceChild(sp,template)
        sp
    }

    //if(template==element) element.appendChild(span) else element.replaceChild(template,span)
    //element.children.toList.foreach(element.removeChild)

    if(!this.lists.contains(key)) throw new Exception(s"not items with key == $key")
    val items = this.lists(key).now.map(this.newItem)
    items.foreach{i=>
      this.addView(i)
      //element.appendChild(i.element)
      element.insertBefore(i.viewElement,span)
      //element.insertAdjacentElement()
      i.bindView(i.viewElement)
    }

    element.children.collect{case el:HTMLElement=>el}.foreach(bind)

  }

}