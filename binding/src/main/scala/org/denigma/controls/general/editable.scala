package org.denigma.controls.general

import importedjs.CodeMirror.{CodeMirror, Editor, EditorConfiguration}
import org.denigma.binding.semantic.{ActiveModelView, ModelInside}
import org.denigma.binding.views.OrganizedView
import org.denigma.binding.{InlineEditor, views}
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
        case Some(ed)=>      if(editMode.now) views.on(el,this)(ed.value) else views.off(el,this)(ed.value)
        case None=>    if(editMode.now) views.on(el,this) else views.off(el,this)
      }
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

//  override def canEdit(el: HTMLElement, view: OrganizedView): Boolean = true

//  override def isActiveAt(el: HTMLElement, view: OrganizedView): Boolean = editors.get(el).isDefined
}

object CodeMirrorEditor extends InlineEditor {


  var pairs = Map.empty[HTMLElement, CodeMirrorEditor]

  override def on(el: HTMLElement, view: OrganizedView): Unit = {

//
//    val area = dom.document.createElement("textarea").asInstanceOf[dom.HTMLTextAreaElement]
//    val html = el.innerHTML
//    val p = el.parentElement
//    p.appendChild(area)
//    p.replaceChild(area,el)
//    area.id = el.id
//    val m: Editor = CodeMirror.fromTextArea(area,params(settings).asInstanceOf[EditorConfiguration])
//    m.getDoc().setValue(html)
//    m.on("change",onChange _)
//    pairs = pairs + (el->m)


  }

  override def off(el: HTMLElement, view: OrganizedView): Unit = pairs.get(el) match {
    case Some(ed)=> dom.document.getElementById(el.id) match {
      case area:dom.HTMLTextAreaElement =>
        dom.console.log("closing "+el.id)
        val p =  area.parentElement
        p.appendChild(el)
        p.replaceChild(el,area)
        el.innerHTML = area.value
        pairs = pairs - el
      case _=>dom.console.log("cannot find text aread")
    }


    case None=>
  }
}

class CodeMirrorEditor(val el:HTMLElement)
{
  def params(md:String) = js.Dynamic.literal(
    mode = md.asInstanceOf[js.Any],
    lineNumbers = true
  )

  val settings = el.attributes.get("data-mode") match {
    case None=>"htmlmixed"
    case Some(value)=>value.value
  }

  lazy val area: HTMLTextAreaElement = {
    val p = el.parentElement
    val ar =  dom.document.createElement("textarea").asInstanceOf[dom.HTMLTextAreaElement]
    p.appendChild(ar)
    p.replaceChild(ar,el)
    ar.id = el.id
    ar
  }

  lazy val editor = CodeMirror.fromTextArea(area,params(settings).asInstanceOf[EditorConfiguration])

  def activate() = {

  }

  def onChange(ed:Editor)
  {
    val v = ed.getDoc().getValue()
    if(el.innerHTML!=v) el.innerHTML=v

  }
}



