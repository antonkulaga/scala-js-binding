package org.denigma.binding.controls

import org.denigma.binding.views.OrdinaryView
import org.scalajs.dom.HTMLElement
import rx.Rx
import rx.Var

import scala.scalajs.js
import js.Dynamic.{global=>g}
import org.scalajs.dom
import importedjs.CodeMirror._
import org.scalajs.jquery.jQuery

abstract class CodeMirrorInsideView(name:String,elem:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends CodeMirrorView(name,elem,params){

  override def bindView(el:HTMLElement) {

    code() = jQuery(el).text()
    el.innerHTML = ""

    super.bindView(el)

  }



}


/**
 * View for article with some text
 */
abstract class CodeMirrorView(val name:String,val elem:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView{



  val code = Var("")
  val mode: String = params.get("mode").fold("htmlmixed")(_.toString())

  def onChange(ed:Editor)
  {
    val v =  ed.getDoc().getValue()
    if(code.now!=v)  code() = v

  }


  override def bindView(el:HTMLElement) {

    super.bindView(el)
    el match {
      case area:dom.HTMLTextAreaElement=>

        val params = js.Dynamic.literal(
          mode = this.mode.asInstanceOf[js.Any],
          lineNumbers = true,
          value = code().asInstanceOf[js.Any]
        )


        val m: Editor = CodeMirror.fromTextArea(area,params.asInstanceOf[EditorConfiguration])


        Rx{
          val v =  m.getDoc().getValue()
          if(code.now!=v)  m.getDoc().setValue(code())
        }
        m.on("change",onChange _)


      case _=> dom.console.log("it is not a text area!")
    }



  }



}
