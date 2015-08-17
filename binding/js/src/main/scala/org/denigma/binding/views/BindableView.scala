package org.denigma.binding.views

import org.denigma.binding.binders._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement

import scala.collection.immutable.Map

object BindableView {

  class JustView(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView

  def apply(elem:HTMLElement,params:Map[String,Any] = Map.empty) =
    new JustView(elem,params).withBinders(me=>new GeneralBinder(me)::new NavigationBinding(me)::Nil)

}

/**
 * View that can have binders attached to it
 */
trait BindableView extends ReactiveView
{
  type Binder = BasicBinding

  var binders:List[Binder] = List.empty

  protected def withBinders(bns:List[Binder]):this.type = {
    this.binders = this.binders ++ bns
    this
  }

  /**
   * Returns myself with some binders
   * @param fun
   * @return
   */
  def withBinder(fun:this.type=>Binder):this.type  = withBinders(fun(this)::binders)
  def withBinders(fun:this.type=>List[Binder]):this.type  = withBinders(fun(this)++binders)


  def makeDefault(el:HTMLElement,props:Map[String,Any] = Map.empty):ChildView = {
    BindableView(el,props)
  }

  override def bindAttributes(el:HTMLElement,ats:Map[String, String]) = {
    binders.foreach(b=>b.bindAttributes(el,ats))
  }


  override def unbind(el:HTMLElement)= {
    //this.binders.foreach(b=>b.bindAttributes())
    //is required for those view that need some unbinding
  }

  protected def warnIfNoBinders(asError:Boolean=true) = if(this.binders.isEmpty) {
    val mess = s"the view $name does not have any binders! Its outer HTML is: ${elem.outerHTML}"
    if(asError) dom.console.error(mess) else dom.console.log(mess)
  }

  override def bindView(el:HTMLElement) = {
    this.bind(el)
    warnIfNoBinders(true)
  }

}






