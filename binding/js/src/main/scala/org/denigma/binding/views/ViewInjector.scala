package org.denigma.binding.views

import org.scalajs.dom.raw.HTMLElement

import scala.collection.immutable.Map
import scala.util.Try

object ViewInjector{
  def apply[View<:OrganizedView](magnet: InjectorMagnet[View]):magnet.Injector = magnet.injector
}

trait InjectorMagnet[View<:OrganizedView] //Resolver is only needed to resolve the injector from a type class
{
  type Injector <: ViewInjector[View]
  def injector:Injector
}

trait ViewInjector[View <:OrganizedView]{
  self=>

  type This<:ViewInjector[View]
  type ChildView  = View#ChildView

  def view:View


  def factories:Map[String,(HTMLElement,Map[String,Any])=>Try[View#ChildView]]

  def inject(viewName:String, element:HTMLElement, params:Map[String,Any]): Option[Try[View#ChildView]] =
  {
    this.factories
      .get(viewName)
      .map(vf=>vf(element,params))
      .orElse{ parentInjection(viewName,element,params) }
  }

  protected def parentInjection(viewName:String, element:HTMLElement, params:Map[String,Any]): Option[Try[View#ChildView]]

  def register(name:String,init:(HTMLElement,Map[String,Any])=>Try[View#ChildView]): This
}


trait Injector[ChildView<:BasicView] {

  def inject(viewName:String,element:HTMLElement,params:Map[String,Any]):Option[Try[ChildView]]
}
