package org.denigma.controls.papers

import org.denigma.controls.papers.SimpleSelection
import org.scalajs.dom
import org.scalajs.dom.raw.Element

object TextSelection {

  def apply(text: String) = SimpleSelection(text)
}

case class SimpleSelection(text: String) extends TextSelection {

  lazy val highlighted = s"""<span class="highlighted">$text<span>"""

  def select(textLayer: Element) = if (text != ""){
    textLayer.innerHTML = textLayer.innerHTML.replace(text, highlighted)
  }
}

trait TextSelection {
  def text: String
  def select(textLayer: Element)
}


/*
case class RangeSelection(range: dom.raw.Range){
  def fragment = range.cloneContents()
  def text = fragment.textContent
  val start = range.startContainer.nodeValue
  val end = range.endContainer.nodeValue
  def hightlight(textLayer: Element) = {
    println(s"start is $start")
    println(s"end is $end")
    println(s"text is $text")

  }
}*/
