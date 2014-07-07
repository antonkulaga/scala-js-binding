package org.denigma.binding.frontend.tools

import org.denigma.controls.general.{CodeMirrorInsideView, CodeMirrorView}
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx.{Rx, Var}

import scala.scalajs.js.Dynamic.{global => g}
import scalatags.Text.Tag

class CodeInsideView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends CodeMirrorInsideView("CodeView",element,params)
{
  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)
}


class CodeView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends CodeMirrorView("CodeView",element,params)
{
  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)
}
