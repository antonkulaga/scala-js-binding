package org.denigma.binding.views

import org.denigma.binding.binders._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement

import scala.collection.immutable.Map

object BindableView {

  class JustView(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView

  def apply(elem:HTMLElement,params:Map[String,Any] = Map.empty) =
    new JustView(elem,params).withBinders(me=>new GeneralBinder(me)::new NavigationBinder(me)::Nil)

}

/**
 * View that can have binders attached to it
 */
trait BindableView extends OrganizedView with BubbleView
{

  protected lazy val defaultBinders:List[ViewBinder] = List(new BinderForViews[this.type](this))

  private var _binders:List[ViewBinder] = defaultBinders
  override def binders = _binders
  protected def binders_=(value:List[ViewBinder]):Unit = _binders = value


  private def withBinders(bns:List[ViewBinder]):this.type = {
    this.binders = this.binders ++ bns
    this
  }

  /**
   * Returns myself with some binders
   * @param fun
   * @return
   */
  def withBinder(fun:this.type=>ViewBinder):this.type  = withBinders(fun(this)::binders)

  def withBinders(fun:this.type=>List[ViewBinder]):this.type  = withBinders(fun(this)++binders)

  def makeDefault(el:HTMLElement,props:Map[String,Any] = Map.empty):ChildView = {
    BindableView(el,props)
  }


  protected def warnIfNoBinders(asError:Boolean) = if(this.binders.isEmpty) {
    val mess = s"the view $name does not have any binders! Its outer HTML is: ${elem.outerHTML}"
    if(asError) dom.console.error(mess) else dom.console.log(mess)
  }

  override def bindView() = {
    this.bindElement(this.viewElement)
    warnIfNoBinders(asError = false)
  }

}
