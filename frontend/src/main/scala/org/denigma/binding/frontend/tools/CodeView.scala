package org.denigma.binding.frontend.tools

import org.scalajs.dom.{MouseEvent, HTMLElement}
import rx.Rx
import rx.Var
import scalatags.HtmlTag

import scala.scalajs.js
import js.Dynamic.{global=>g}
import org.denigma.views.tools.CodeMirrorView


class CodeView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends CodeMirrorView("CodeView",element,params)
{
  override def tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)
}
