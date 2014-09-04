package org.denigma.binding.frontend.tools

import org.denigma.controls.general.{CodeMirrorInsideView, CodeMirrorView}
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx.{Rx, Var}

import scala.scalajs.js.Dynamic.{global => g}
import scalatags.Text.Tag

class CodeInsideView(elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends CodeMirrorInsideView(elem,params)
{
    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


}


class CodeView(elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends CodeMirrorView(elem,params)
{
    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


}
