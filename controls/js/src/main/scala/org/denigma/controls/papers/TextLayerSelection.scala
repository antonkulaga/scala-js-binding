package org.denigma.controls.papers

import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.{HTMLCollection, Element, Node}

import scala.scalajs.js

object TextLayerSelection {

  val data_chunk_id = "data-chunk-id"

  val data_num = "data-num"

  lazy val empty = new TextLayerSelection("", "", "", "")

  def apply(fromChunk: String, toChunk: String, fromToken: String = "", toToken: String = ""): TextLayerSelection =
    new TextLayerSelection(fromChunk, toChunk, fromToken, toToken)

  implicit def fromRange(range: org.scalajs.dom.raw.Range): TextLayerSelection = {
    val (fromChunk, fromToken) = extractChunkToken(range.startContainer)
    val (toChunk, toToken) = extractChunkToken(range.endContainer)
    //js.debugger()
    new TextLayerSelection(fromChunk, toChunk, fromToken, toToken)
  }

  /*
  protected def isTSpan(node: Node) = node match {
    case null => false
    case el: Element if el.hasAttribute(data_num) => true
    case other => false
  }

  protected def tspan(node: Node) = node match {
    case el: Element if isTSpan(el) => el
    case el if isTSpan(el.nextSibling) =>
  }
  */

  protected def extractChunkToken(node: Node, start: Boolean = true): (String, String) = node match {
    case null => "" -> ""

    case el: Element if el.hasAttribute(TextLayerSelection.data_num) =>
      val num = el.getAttribute(TextLayerSelection.data_num)
      el.fromParentNode{
        case e: Element if e.hasAttribute(TextLayerSelection.data_chunk_id) => e.getAttribute(data_chunk_id) -> num
      }.getOrElse(("", ""))

    case el: Element if el.hasAttribute(TextLayerSelection.data_chunk_id) =>
      val chunk = el.getAttribute(TextLayerSelection.data_chunk_id)
      val children: Seq[Element] = if(start) el.children.toSeq else el.children.reverse
      children.collectFirst{
        case child: Element if child.hasAttribute(TextLayerSelection.data_num) => chunk -> child.getAttribute(TextLayerSelection.data_num)
      }.getOrElse(chunk -> "")

    case el: Element if el.children.nonEmpty && el.children.head.hasAttribute(TextLayerSelection.data_num) =>
      extractChunkToken(el.children.head, start)

    case other if other.nextSibling != null && other.nextSibling.attributes.contains(TextLayerSelection.data_num) =>
      extractChunkToken(other.nextSibling, start)

    case other if other.previousSibling != null && other.previousSibling.attributes.contains(TextLayerSelection.data_num) =>
      extractChunkToken(other.previousSibling, start)

    case other =>
      extractChunkToken(other.parentNode, start)
  }



}

class TextLayerSelection(
                 val fromChunk: String,
                 val toChunk: String,
                 val fromToken: String,
                 val toToken: String
               ) extends ElementSelector
{
  def selectChunks(textLayer: Element): List[dom.Element] = selectBetween(textLayer, fromChunk, toChunk)(selectChunk)

  def selectSpans(textLayer: Element): List[dom.Element] = selectBetween(textLayer, fromChunk, toChunk)(selectChunk) match {
    case Nil => Nil
    case head::Nil if fromChunk=="" && toChunk == "" => takeTokens(head.children)
    case head::Nil =>
      selectBetween(head, fromToken, toToken)(selectNum)

    case head::other =>
      val headTokens = takeTokens(head.children)
      val startList: List[Element] = headTokens.dropWhile(v=> v.getAttribute(TextLayerSelection.data_num) != fromToken)
      val (last, mid) = (other.last, other.dropRight(1))
      js.debugger()
      val middle = mid.flatMap(v => takeTokens(v.children))
      js.debugger()
      //val lt = selectBetween(last, "", toToken)(selectNum)
      js.debugger()
      startList ++ middle //++ lt
  }

  protected def takeTokens(collection: HTMLCollection) = collection.filter(c => c.hasAttribute(TextLayerSelection.data_num)).toList

  protected def selectChunk(textLayer: Element, chunk: String): Element = textLayer.selectByAttribute(TextLayerSelection.data_chunk_id, chunk)

  protected def selectNum(chunkElement: Element, num: String): Element = chunkElement.selectByAttribute(TextLayerSelection.data_num, num)

}

trait ElementSelector {

  //inclusive
  def selectBetween(element: Element, fromID: String, toID: String)(selector: (Element, String) => Element): List[Element] = (fromID, toID) match {
    case ("", "") => Nil
    case (a, b) if a==b => List(selector(element, a))
    case ("", b) =>
      val el = selector(element, b)
      el.previousAll(List(el))

    case (a, "") =>
      val el = selector(element, a)
      el.nextAll(List(el))

    case (a, b) =>
      val from = selector(element, a)
      val to = selector(element, b)
      from.nextUntil(List(from), v=> v!=to) :+ to
  }
}
