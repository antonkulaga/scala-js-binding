package org.denigma.binding.views.collections

import org.denigma.binding.binders.NavigationBinding
import org.denigma.binding.binders.collections.MapItemsBinder
import org.denigma.binding.views.BindableView
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
import rx.Var

import scala.collection.immutable._

object MapCollectionView {

  class JustMapView(val elem:HTMLElement,val params:Map[String,Any]) extends BindableView{

   val reactiveMap: Map[String, Var[String]] = params.map(kv => (kv._1, Var(kv._2.toString)))


    override def activateMacro(): Unit = { this.extractors.foreach(_.extractEverything(this))}

    override protected def attachBinders(): Unit =   this.withBinders(new MapItemsBinder(this,reactiveMap)::new NavigationBinding(this)::Nil)
  }

}



abstract class MapCollectionView(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView  with CollectionView
{
  //val key = params.get("items").getOrElse("items").toString

  val disp = elem.style.display

  override type Item = Map[String,Any]
  override type ItemView = BindableView

  def newItem(params:Item):ItemView = this.constructItem(params,params){  (el,mp)=>new MapCollectionView.JustMapView(el,mp) }

}