package org.denigma.semantic.binders.editable

import org.denigma.binding.views.BindableView
import org.denigma.controls.editors.editors
import org.denigma.semantic.binders.ModelBinder
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.IRI
import rx.core.{Rx, Var}
import org.scalajs.dom.extensions._

class EditModelBinder(view:BindableView, modelInside:Var[ModelInside], editMode:Rx[Boolean]) extends ModelBinder(view,modelInside) {

  /**
   * Binds editor to editable element
   * @param el
   * @param key
   */
  def bindEditable(el:HTMLElement,key:String) = {
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
    super.bindRdfInner(el, key)
    this.bindEditable(el,key.stringValue)
  }

  override protected def bindRdfText(el: HTMLElement, key: IRI) = {
    super.bindRdfText(el,key)
    this.bindEditable(el,key.stringValue)
  }


}