package org.denigma.binding.views

import org.denigma.binding.binders.{MapItemsBinder, NavigationBinder}
import org.scalajs.dom.raw.HTMLElement
import rx.Var

import scala.collection.immutable._

object MapCollectionView {


  class JustMapView(val elem:HTMLElement,val params:Map[String,Any]) extends BindableView
  {

   val reactiveMap: Map[String, Var[String]] = params.map(kv => (kv._1, Var(kv._2.toString)))

    withBinders(m=>new MapItemsBinder(m,reactiveMap)::new NavigationBinder(m)::Nil)

  }

}



abstract class MapCollectionView(val elem:HTMLElement) extends BindableView  with ItemsSeqView
{
  val disp = elem.style.display

  override type Item = Map[String,Any]

  override type ItemView = BindableView

  def newItem(item:Item):ItemView = this.constructItemView(item){  (el,mp)=>new MapCollectionView.JustMapView(el,item) }

}