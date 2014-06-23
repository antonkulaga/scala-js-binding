package org.denigma.binding.controls
import org.denigma.binding.{InlineEditor, views}
import org.denigma.binding.views.{OrganizedView, OrdinaryView}
import rx.core.Var

import org.scalajs.dom.{HTMLDivElement, TextEvent, MouseEvent, HTMLElement}
import rx.core.Var
import scala.util.Random
import rx.Rx
import scala.collection.immutable.Map
import org.scalajs.dom
import scalatags.Text.tags._
import scalatags.Text.{attrs => a, styles => s, _}
import org.denigma.binding.extensions._
import org.scalax.semweb.rdf.IRI
import importedjs.CodeMirror.{EditorConfiguration, CodeMirror, Editor}
import scala.scalajs.js
import js.Dynamic.{ global => g }

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

  override protected def bindRdfText(el: HTMLElement, key: IRI) = {
    super.bindRdfText(el,key)
    this.bindEditable(el,key.stringValue)
  }


  def bindEditable(el:HTMLElement,key:String) = {
    this.bindRx(key,el,this.editMode){ (el,model)=>
      el.contentEditable = editMode().toString
      views.onEdit(editMode.now,el,this)

    }
  }





  val toggleClick = Var(createMouseEvent())

  val saveClick = Var(createMouseEvent())



}
/**
 * Note works only if you added CKEditor js.lib
 */
object CkEditor extends InlineEditor{

  var editors = Map.empty[HTMLElement,js.Dynamic]

  override def on(el: HTMLElement, view: OrganizedView): Unit = {
    val id = el.id
    val editor = g.CKEDITOR.inline( el )
    editors = editors + (el->editor)
  }

  override def off(el: HTMLElement, view: OrganizedView): Unit = {
    editors.get(el) match {
      case Some(ed)=>
        this.editors = editors - el
        ed.destroy()
      case None=>
    }
    //g.CKEDITOR.inline( el )
  }
}