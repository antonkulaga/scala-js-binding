package org.denigma.binding

import org.denigma.binding.views.OrganizedView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement

import scala.collection.immutable.Map
import scala.util.Try


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

  def registerEditor(name:String,editor:InlineEditor) = {
   this.editors = this.editors+(name.toLowerCase->editor)
   this
  }
  var defEditor: Option[InlineEditor] = None


  def on(el:HTMLElement,view:OrganizedView)(implicit editor:String = "") ={
    this.editors.get(editor.toLowerCase) match {
      case Some(ed)=> ed.on(el,view)
      case None=> defEditor match{
        case None=>  dom.console.error(s"no editor called $editor for view ${view.id} and element ${el.outerHTML}")
        case Some(ed)=>ed.on(el,view)
      }

    }
  }

  def off(el:HTMLElement,view:OrganizedView)(implicit editor:String="")= {
    this.editors.get(editor.toLowerCase) match {
      case Some(ed) => ed.off(el, view)
      case None => defEditor match {
        case None =>
        case Some(ed) => ed.off(el, view)
      }

    }

  }

  def offAll(el:HTMLElement,view:OrganizedView) = {
    this.editors.values.foreach(e=>e.off(el,view))
  }
}

/**
 * trait that on/offs inline editor when content editable is changed
 */
trait InlineEditor {
//  def canEdit(el:HTMLElement,view:OrganizedView):Boolean
//  def isActiveAt(el:HTMLElement,view:OrganizedView):Boolean
  def on(el:HTMLElement,view:OrganizedView):Unit
  def off(el:HTMLElement,view:OrganizedView):Unit

}