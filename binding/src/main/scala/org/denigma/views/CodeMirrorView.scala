package org.denigma.views

import org.denigma.views.OrdinaryView
import org.scalajs.dom.{HTMLTextAreaElement, MouseEvent, HTMLElement}
import rx.{Rx, Var}
import scalatags.HtmlTag
import rx.Var
import scalatags.HtmlTag
import org.scalajs.jquery.jQuery

import scala.scalajs.js
import js.Dynamic.{global=>g}
import org.scalajs.dom
import importedjs.CodeMirror._


/**
 * View for article with some text
 */
class CodeMirrorView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("CodeView",element){
  override def tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)


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
