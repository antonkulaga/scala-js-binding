package org.denigma.controls.papers

import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.{HTMLCollection, Element, Node}

import scala.scalajs.js

object TextLayerSelection {

  case class SimpleTextLayerSelection(
                            label: String,
                            fromChunk: String,
                            toChunk: String,
                            fromToken: String,
                            toToken: String
                          ) extends TextLayerSelection

  val data_chunk_id = "data-chunk-id"

  val data_num = "data-num"

  lazy val empty: TextLayerSelection with Object = new SimpleTextLayerSelection("", "", "", "", "")

  def apply(label: String, fromChunk: String, toChunk: String, fromToken: String = "", toToken: String = ""): TextLayerSelection =
    new SimpleTextLayerSelection(label, fromChunk, toChunk, fromToken, toToken)

  implicit def fromRange(label: String, range: org.scalajs.dom.raw.Range): TextLayerSelection = {
    val (fromChunk, fromToken) = extractChunkToken(range.startContainer)
    val (toChunk, toToken) = extractChunkToken(range.endContainer)
    //js.debugger()
    new SimpleTextLayerSelection(label, fromChunk, toChunk, fromToken, toToken)
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

trait TextLayerSelection extends ElementSelector
{

  def label: String
  def fromChunk: String
  def toChunk: String
  def fromToken: String
  def toToken: String

  def selectChunks(textLayer: Element): List[dom.Element] = selectBetween(textLayer, fromChunk, toChunk)(selectChunk)

  /*
  def selectSpans(textLayer: Element): List[dom.Element] = selectChunks(textLayer) match {
    case Nil => Nil

    case head::Nil if fromChunk=="" && toChunk == "" => takeTokens(head.children)

    case head::Nil => selectBetween(head, fromToken, toToken)(selectNum)

    case head::other =>
      val headTokens = takeTokens(head.children)
      val startList: List[Element] = headTokens.dropWhile(v=> v.getAttribute(TextLayerSelection.data_num) != fromToken)
      val (mid, last) = (other.dropRight(1), other.last)
      val middle = mid.flatMap(v => takeTokens(v.children))
      val lt = selectBetween(last, "", toToken)(selectNum)
      startList ++ middle ++ lt
  }
  */

  //protected def selectSpan(chunkElement: Element, num: String): Element = chunkElement.selectByAttribute(TextLayerSelection.data_num, num)

  def selectTokenSpans(textLayer: Element): List[dom.Element] = selectChunks(textLayer) match {
    case Nil => Nil

    case head::Nil if fromChunk=="" && toChunk == "" => takeTokens(head.children)

    case head::Nil => selectBetween(head, fromToken, toToken)(selectNum)

    case head::other =>
      val headTokens = takeTokens(head.children)
      val startList: List[Element] = headTokens.dropWhile(v=> v.getAttribute(TextLayerSelection.data_num) != fromToken)
      val (mid, last) = (other.dropRight(1), other.last)
      val middle = mid.flatMap(v => takeTokens(v.children))
      val lt = selectBetween(last, "", toToken)(selectNum)
      startList ++ middle ++ lt
  }

  protected def takeTokens(collection: HTMLCollection) = collection.filter(c => c.hasAttribute(TextLayerSelection.data_num)).toList

  protected def selectChunk(textLayer: Element, chunk: String): Element = textLayer.selectByAttribute(TextLayerSelection.data_chunk_id, chunk)

  protected def selectNum(chunkElement: Element, num: String): Element = chunkElement.selectByAttribute(TextLayerSelection.data_num, num)

}

trait ElementSelector {

  //inclusive
  def selectBetween(element: Element, fromID: String, toID: String)(selector: (Element, String) => Element): List[Element] = (fromID, toID) match {
    case ("", "") => Nil
    case ("", b) =>
      Option(selector(element, b)).map{
        case el => el.previousAll(el, List(el))
      }.getOrElse(Nil)


    case (a, "") =>
      println("test zero")
      val result = for(el <-Option(selector(element, a))) yield  {
        println("test one")
        js.debugger()
        el.nextAll(el, List(el))
      }
      result.getOrElse(Nil)

    case (a, b) if a==b =>
      val result = for(
        el <-Option(selector(element, a))
      ) yield List(el)
      result.getOrElse(Nil)

    case (a, b) =>
      val result: Option[List[dom.Element]] = for{
        from <- Option(selector(element, a))
        to <- Option(selector(element, b))
      } yield {
        from.nextUntil(from, List(from), v=> v.isSameNode(to)) :+ to
      }
      result.getOrElse(Nil)
  }
}
