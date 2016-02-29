package org.denigma.controls.papers

import org.denigma.controls.papers.SimpleSelection
import org.scalajs.dom
import org.scalajs.dom.raw.Element

object TextSelection {

  def apply(text: String) = SimpleSelection(text)

}

case class SimpleSelection(text: String) extends TextSelection {

  def highlighted(string: String) = s"""<span class="highlighted">$string<span>"""

  /*
  override def text: String =
    s"""
      |selection:start "$from
      |selection:end $to
    """.stripMargin
*/
  override def select(textLayer: Element): Unit = {
    val html = textLayer.innerHTML
    if (html.contains(text)) {
      textLayer.innerHTML = html.replace(text, highlighted(text))
    } else println(s"WARNING: cannot find $text inside selection")
  }
}
/*
case class SimpleSelection(text: String) extends TextSelection {

  lazy val highlighted = s"""<span class="highlighted">$text<span>"""

  def select(textLayer: Element) = if (text != ""){
    val inner = textLayer.innerHTML
    val t = text
    textLayer.innerHTML = inner.replace(text, highlighted)
  }
}
*/
trait TextSelection {
  def text: String
  def select(textLayer: Element): Unit
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
