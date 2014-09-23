package org.denigma.controls.editors

import org.denigma.binding.views.{BindableView, OrganizedView}
import org.denigma.controls.editors.InlineEditor
import org.scalajs.dom
import org.scalajs.dom.HTMLElement

import scala.collection.immutable.Map

object editors {



  var editors = Map.empty[String,InlineEditor]

  def registerEditor(name:String,editor:InlineEditor,default:Boolean = false) = {
    this.editors = this.editors+(name.toLowerCase->editor)
    if(default) this.editors = this.editors+(""->editor)
    this
  }


  def on(el:HTMLElement,view:BindableView)(implicit editor:String = "") ={
    this.editors.get(editor.toLowerCase) match {
      case Some(ed)=> ed.on(el,view)
      case None=>  dom.console.error(s"no editor called $editor for view ${view.id} and element ${el.outerHTML}")

    }
  }

  def off(el:HTMLElement,view:BindableView)(implicit editor:String="")= {
    this.editors.get(editor.toLowerCase) match {
      case Some(ed) => ed.off(el, view)
      case None =>
    }

  }

  def offAll(el:HTMLElement,view:BindableView) = {
    this.editors.values.foreach(e=>e.off(el,view))
  }
}
