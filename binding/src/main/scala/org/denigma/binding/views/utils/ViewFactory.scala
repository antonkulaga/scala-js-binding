package org.denigma.binding.views.utils

import org.denigma.binding.views.{utils, BindingView}
import org.scalajs.dom.HTMLElement

import scala.util.Try

/**
 * Is used for dependency injection of views, see implementations of this class like ViewInjector
 */
class ViewFactory {

  type ChildView<:BindingView

  type ViewFactory = (HTMLElement,Map[String,Any])=>Try[ChildView]

  var factories = Map.empty[String,ViewFactory]

  def inject(viewName:String,element:HTMLElement,params:Map[String,Any]): Option[Try[ChildView]] = this.factories.get(viewName).map(vf=>vf(element,params))


  def register(name:String,factory:ViewFactory): this.type = {
    this.factories = this.factories+(name->factory)
    this
  }
}
