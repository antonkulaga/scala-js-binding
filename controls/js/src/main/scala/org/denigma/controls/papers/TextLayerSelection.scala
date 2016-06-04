package org.denigma.controls.papers

import org.denigma.binding.extensions._
import org.scalajs.dom.Element
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.{Element, Node}

object TextLayerSelection {

  val data_chunk_id = "data-chunk-id"

  val data_num = "data-num"

  lazy val empty = new TextLayerSelection("", "", "", "")

  def apply(fromChunk: String, toChunk: String, fromToken: String, toToken: String): TextLayerSelection =
    new TextLayerSelection(fromChunk, toChunk, fromToken, toToken)

  implicit def fromRange(range: org.scalajs.dom.raw.Range): TextLayerSelection = {
    val (fromChunk, fromToken) = extractChunkToken(range.startContainer)
    val (toChunk, toToken) = extractChunkToken(range.endContainer)
    TextLayerSelection(fromChunk, fromToken, toChunk, toToken)
  }

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

    case el: Element if el.children.nonEmpty && el.children.head.hasAttribute(TextLayerSelection.data_chunk_id) => extractChunkToken(el.children.head, start)

    case other => extractChunkToken(other.parentNode, start)
  }



}

class TextLayerSelection(
                 val fromChunk: String,
                 val toChunk: String,
                 val fromToken: String,
                 val toToken: String
               ) extends ElementSelector
{

  def select(textLayer: Element) =  selectBetween(textLayer, fromChunk, toChunk)(selectChunk)


  protected def selectChunk(textLayer: Element, chunk: String): Element = textLayer.selectByAttribute(TextLayerSelection.data_chunk_id, chunk)

  protected def selectNum(chunkElement: Element, num: String): Element = chunkElement.selectByAttribute(TextLayerSelection.data_num, num)

}

trait ElementSelector {
  def selectBetween(element: Element, fromID: String, toID: String)(selector: (Element, String)=>Element) = (fromID, toID) match {
    case ("", "") => Nil
    case (a, b) if a==b => List(a)
    case ("", b) =>  selector(element, b).previousAll()
    case (a, "") => selector(element, a).nextAll()
    case (a, b) =>
      val from = selector(element, a)
      val to = selector(element, b)
      Nil
  }
}
