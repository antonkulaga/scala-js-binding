package org.denigma.preview

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.BindableView
import org.denigma.binding.views.collections.CollectionView
import org.scalajs.dom.raw.HTMLElement
import rx.Rx
import rx.core.Var

import scala.collection.immutable.Seq
import scala.util.Try

class SelectionView(val elem:HTMLElement,val params:Map[String,Any]) extends BindableView
{

  //override lazy val injector = defaultInjector.register("selected"){ case (el,pms)=>  new SelectionView(el,pms) }



}
/*
class OneItemView(val elem:HTMLElement,val params:Map[String,Any]) extends BindableView{

  override protected def attachBinders(): Unit = this.withBinders(new GeneralBinder(this))



}

class ItemsView(val elem:HTMLElement, val params:Map[String,Any]) extends CollectionView with BindableView
{


  //injector = defaultInjector.register("")

  override protected def attachBinders(): Unit = this.withBinders(new GeneralBinder(this))



  override type Item = this.type
  override type ItemView = BindableView

  override def newItem(item: Item): ItemView= {
    ???
  }


  override val items: Rx[Seq[Item]] = Var(Seq.empty)
}
*/
