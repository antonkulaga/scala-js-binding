package org.denigma.semantic.controls

import org.denigma.binding.binders.{NavigationBinding, GeneralBinder}
import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.views.{BindableView, OrdinaryView}
import org.denigma.controls.editors.editors
import org.denigma.semantic.binding.{ModelView, ModelBinder, ModelInside}
import org.scalajs.dom._
import org.scalajs.dom.extensions._
import org.scalax.semweb.rdf.IRI
import rx.core.{Rx, Var}

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scalatags.Text.{attrs => a, styles => s}


trait EditModelView extends ModelView with OrdinaryView
{

  def params:Map[String,Any]

  val mode: String = params.get("mode").fold("htmlmixed")(_.toString())

  val codeParams = js.Dynamic.literal(
    mode = this.mode.asInstanceOf[js.Any],
    lineNumbers = true
  )

  override val modelInside: Var[ModelInside] = this.params.get("model").map(m=>m.asInstanceOf[Var[ModelInside]]).getOrElse(Var(ModelInside.empty))

  val editMode = Var(false)

  val toggleClick = Var(EventBinding.createMouseEvent())

  val saveClick = Var(EventBinding.createMouseEvent())

  override def attachBinders() = {
    binders = new EditModelBinder(this,this.modelInside,this.editMode)::new GeneralBinder(this)::new NavigationBinding(this)::Nil
  }



}

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