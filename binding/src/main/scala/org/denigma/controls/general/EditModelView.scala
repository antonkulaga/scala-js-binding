package org.denigma.controls.general

import org.denigma.binding.semantic.{ActiveModelView, ModelInside}
import org.denigma.binding.views
import org.denigma.controls.editors.editors
import org.denigma.controls.semantic.AjaxModelView
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._
import org.scalax.semweb.rdf.IRI
import rx.core.Var

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scalatags.Text.{attrs => a, styles => s}


trait EditModelView extends AjaxModelView
{

  def params:Map[String,Any]

  val mode: String = params.get("mode").fold("htmlmixed")(_.toString())

  val codeParams = js.Dynamic.literal(
    mode = this.mode.asInstanceOf[js.Any],
    lineNumbers = true
  )

  override val modelInside: Var[ModelInside] = this.params.get("model").map(m=>m.asInstanceOf[Var[ModelInside]]).getOrElse(Var(ModelInside.empty))



  val editMode = Var(false)


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


  /**
   * Binds editor to editable element
   * @param el
   * @param key
   */
  def bindEditable(el:HTMLElement,key:String) = {
    this.bindRx(key,el,this.editMode){ (el,model)=>
      el.contentEditable = editMode().toString
      el.attributes.get("editor") match {
        case Some(ed)=>      if(editMode.now) editors.on(el,this)(ed.value) else editors.off(el,this)(ed.value)
        case None=>    if(editMode.now) editors.on(el,this) else editors.off(el,this)
      }
    }
  }

  val toggleClick = Var(createMouseEvent())

  val saveClick = Var(createMouseEvent())



}