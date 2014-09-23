package org.denigma.binding.views

import org.denigma.binding.binders.{NavigationBinding, ItemsBinder}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._
import rx.{Rx, Var}

import scala.collection.immutable._
import scalatags.Text.Tag

object ListView {

  class JustMapView(el:HTMLElement,val params:Map[String,Any]) extends MapView(el,params){


    override def activateMacro(): Unit = { this.extractors.foreach(_.extractEverything(this))}

    override protected def attachBinders(): Unit = binders = MapView.defaultBinders(this)
  }


  def apply(el:HTMLElement,props:Map[String,Any]):MapView = {
    new JustMapView(el,props)
  }
}

object MapView {
  implicit def defaultBinders(view:MapView) =  new ItemsBinder(view,view.reactiveMap)::new NavigationBinding(view)::Nil
}


abstract class MapView(val elem:HTMLElement,props:Map[String,Any]) extends BindableView {

  val reactiveMap: Map[String, Var[String]] = props.map(kv => (kv._1, Var(kv._2.toString)))
}




abstract class ListView(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView
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