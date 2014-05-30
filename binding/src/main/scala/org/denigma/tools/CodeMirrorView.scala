package org.denigma.tools

import org.denigma.views.OrdinaryView
import org.scalajs.dom.HTMLElement
import rx.Rx
import rx.Var

import scala.scalajs.js
import js.Dynamic.{global=>g}
import org.scalajs.dom
import importedjs.CodeMirror._


/**
 * View for article with some text
 */
abstract class CodeMirrorView(name:String,element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView(name,element){



  val code = Var("")
  val mode: String = params.get("mode").fold("htmlmixed")(_.toString())


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
          m.getDoc().setValue(code())
        }


        dom.console.log("code mirror has started!")

      case _=> dom.console.log("it is not a text area!")
    }



  }



}
