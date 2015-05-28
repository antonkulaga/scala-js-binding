package org.denigma.binding.views.utils

import org.denigma.binding.views.{OrganizedView, Injector, utils, BasicView}
import org.scalajs.dom.raw.HTMLElement

import scala.util.Try

/**
 * Is used for dependency injection of views, see implementations of this class like ViewInjector
 */
class ViewFactory{

  type ChildView<:BasicView

  type SimpleViewFactory = (HTMLElement,Map[String,Any])=>Try[ChildView]

  type ViewFactory = (HTMLElement,Map[String,Any],Option[OrganizedView])=>Try[ChildView]

  def transformFactory(factory:SimpleViewFactory):ViewFactory = (el,params,parent)=> factory(el,params)

  var factories = Map.empty[String,ViewFactory]

  def inject(viewName:String,element:HTMLElement,params:Map[String,Any],parent:Option[OrganizedView]): Option[Try[ChildView]] = this.factories.get(viewName).map(vf=>vf(element,params,parent))


  def register(name:String,factory:ViewFactory): this.type = {
    this.factories = this.factories+(name->factory)
    this
  }

  def register(name:String,factory:SimpleViewFactory): this.type = {
    this.factories = this.factories+(name->this.transformFactory(factory))
    this
  }
}
