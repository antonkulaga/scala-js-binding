package org.denigma.semantic.binders.editable

import org.denigma.binding.views.BindableView
import org.denigma.controls.editors.editors
import org.denigma.semantic.binders.ModelBinder
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.{Event}
import org.denigma.semweb.rdf.IRI
import rx.core.{Rx, Var}
import org.scalajs.dom.ext._
class EditModelBinder(view:BindableView, modelInside:Var[ModelInside], editMode:Rx[Boolean]) extends ModelBinder(view,modelInside) {


  /**
   * Binds editor to editable element
   * @param el
   * @param key
   */
  def bindEditable(el:HTMLElement,key:String) = {
    //dom.console.log(s"editmode works ${el.contentEditable}")

    this.bindRx(key,el,this.editMode){ (el,model)=>
      el.contentEditable = editMode().toString
      el.attributes.get("editor") match {
        case Some(ed)=>      if(editMode.now) editors.on(el,view)(ed.value) else editors.off(el,view)(ed.value)
        case None=>    if(editMode.now) editors.on(el,view) else editors.off(el,view)
      }
    }
  }

  /**
   * TODO: simplify to avoid code duplication
   * @param el
   * @param key
   */
  override protected def bindRdfInner(el: HTMLElement, key: IRI) =
  {
    dom.console.log(s"editmode works ${el.contentEditable}")
    el.onblur = this.makeRdfHandler(el, key, "innerHTML")
    super.bindRdfInner(el, key)
    this.bindEditable(el,key.stringValue)
  }

  override protected def bindRdfText(el: HTMLElement, key: IRI) = {
    dom.console.log(s"editmode works ${el.contentEditable}")
    el.onblur = this.makeRdfHandler(el, key, "textContent")
    super.bindRdfText(el,key)
    this.bindEditable(el,key.stringValue)
  }


}