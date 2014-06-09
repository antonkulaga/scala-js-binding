package org.denigma.controls

import org.denigma.views.core.OrdinaryView
import rx.core.Var

import org.scalajs.dom.{HTMLDivElement, TextEvent, MouseEvent, HTMLElement}
import rx.core.Var
import scala.util.Random
import rx.Rx
import scala.collection.immutable.Map
import org.scalajs.dom
import org.denigma.views.core.OrdinaryView
import scalatags.Text.tags._
import scalatags.Text.{attrs => a, styles => s, _}
import org.denigma.extensions._
import org.scalax.semweb.rdf.IRI
import importedjs.CodeMirror.{EditorConfiguration, CodeMirror, Editor}
import scala.scalajs.js
import java.awt.TextArea

trait EditModelView extends ActiveModelView
{
  self:OrdinaryView=>

  def params:Map[String,Any]

  val mode: String = params.get("mode").fold("htmlmixed")(_.toString())

  val codeParams = js.Dynamic.literal(
    mode = this.mode.asInstanceOf[js.Any],
    lineNumbers = true
  )


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

  def bindEditable(el:HTMLElement,key:String) = {
    this.bindRx(key,el,this.editMode){ (el,model)=>
      el.contentEditable = editMode().toString
    }

  }



  override protected def bindRdfText(el: HTMLElement, key: IRI) = {
    super.bindRdfText(el,key)
    this.bindEditable(el,key.stringValue)
  }

  val toggleClick = Var(createMouseEvent())

  val saveClick = Var(createMouseEvent())



}
