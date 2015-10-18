package org.denigma.binding.views

import org.scalajs.dom.raw.Element

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

trait ViewInjector[View <:OrganizedView]
{
  self=>

  type This<:ViewInjector[View]
  type ChildView  = View#ChildView

  def view:View


  def factories:Map[String,(Element,Map[String,Any])=>Try[View#ChildView]]

  def inject(viewName:String, element:Element, params:Map[String,Any],goUp:Boolean = true): Option[Try[View#ChildView]] =
    this.factories.get(viewName).map(vf=>vf(element,params)) match {
      case None => if(goUp) parentInjection(viewName,element,params) else None
      case other => other
    }


  protected def parentInjection(viewName:String, element:Element, params:Map[String,Any]): Option[Try[View#ChildView]]

  def register(name:String)(init:(Element,Map[String,Any])=>View#ChildView):This = tryRegister(name)((el,args)=>Try(init(el,args)))

  def tryRegister(name:String)(init:(Element,Map[String,Any])=>Try[View#ChildView]):This

}


trait Injector[ChildView<:BasicView] {

  def inject(viewName:String,element:Element,params:Map[String,Any]):Option[Try[ChildView]]
}
