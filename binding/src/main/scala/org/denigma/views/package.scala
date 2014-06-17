package org.denigma

import scala.collection.immutable.Map
import org.scalajs.dom.{HTMLElement, Node}
import scala.util.Try
import org.denigma.views.core.{OrganizedView, BindingView}


/**
 * Implicits for views
 */
package object views {

  type ViewFactory = (HTMLElement,Map[String,Any])=>Try[BindingView]

  var factories = Map.empty[String,ViewFactory]

  def register(name:String,factory:ViewFactory) = {
    this.factories = this.factories+(name->factory)
    this
  }

  var editors = Map.empty[String,InlineEditor]

  def registerEditor(name:String,editor:InlineEditor) {
   this.editors = this.editors+(name->editor)
  }

  def onEdit(mode:Boolean,el:HTMLElement,view:OrganizedView) = {
    editors.foreach{case (k,e)=>if(mode) e.on(el,view) else e.off(el,view)}
  }

}

/**
 * trait that on/offs inline editor when content editable is changed
 */
trait InlineEditor {
  def on(el:HTMLElement,view:OrganizedView):Unit
  def off(el:HTMLElement,view:OrganizedView):Unit

}