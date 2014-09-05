package org.denigma.controls.general

import org.denigma.binding.views.BindableView
import org.scalajs.codemirror.{CodeMirror, EditorConfiguration, Editor}
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.jquery.jQuery
import rx.{Rx, Var}

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}

abstract class CodeMirrorInsideView(elem:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends CodeMirrorView(elem,params){

  override def bindView(el:HTMLElement) {

    code() = jQuery(el).text()
    el.innerHTML = ""

    super.bindView(el)

  }



}


/**
 * View for article with some text
 */
abstract class CodeMirrorView(val elem:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends BindableView{



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
