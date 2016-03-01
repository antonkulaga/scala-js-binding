package org.denigma.controls.papers

import org.denigma.controls.papers.SimpleSelection
import org.scalajs.dom
import org.scalajs.dom.ext._

import org.scalajs.dom.raw.{HTMLElement, Element}

import scala.annotation.tailrec
import scala.scalajs.js

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

  /*
  @tailrec final def elemenetsThatContain(elements: List[Element], text: String, history: List[Element] = Nil): List[Element] = elements match {
    case head::tail=>
      val t = head.textContent
      if(text.startsWith(t))
      {
        if (t == text) history.reverse
        else
          elemenetsThatContain(tail, text.substring(t.length), head::history)
      } else Nil

    case Nil => if(text == "") history.reverse else Nil
  }

  protected def findElements(elements: List[Element], text: String): List[Element] =  elements match {
    case Nil => Nil
    case head::tail =>
      val t = head.textContent
      text.indexOf(t) match {
        case 0 => elemenetsThatContain(elements, text)
        case -1 =>
          //findElements(tail, text)
          tail match {
            case Nil => Nil
            case h::Nil => if(h.textContent==text) tail else Nil
            case h::tt =>
              val s = h.textContent

          }
          elemenetsThatContain(elements, text) match {
            case Nil => findElements(tail, text)
            case list =>
              if()
          }
        case num =>
          findElements(tail, text)
      }
    case Nil=> Nil
  }
  */

  /***
    * *finds minimal element that contains the textcontent
    *
    * @param element
    * @param text
    * @return
    */
  def findMinimalElement(element: Element, text: String): Option[Element] = {
    element.children.collectFirst{ case e if e.textContent.contains(text) => findMinimalElement(e, text) }.getOrElse( Some(element))
  }

  private def paint(element: Element, text: String) = element.children.foreach{
    case ch=>
      val t = ch.textContent
      if(t !="" && text.contains(t)) ch.classList.add("highlighted")
  }

  override def select(textLayer: Element): Unit = {
    //val html = textLayer.innerHTML
    val content = textLayer.textContent
    content.indexOf(text) match {
      case i if i >=0 =>
        findMinimalElement(textLayer, text) match {
          case Some(e) =>
            paint(e, text)
          case None =>  println(s"WARNING: cannot find $text inside selection IT IS A BUG!")
        }

      case _ => println(s"WARNING: cannot find $text inside selection")
    }
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
