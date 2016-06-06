package org.denigma.pdf.extensions


import org.denigma.pdf._
import org.scalajs.dom
import org.scalajs.dom.Node
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.{Element, HTMLElement}

import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, _}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

class TextLayerRenderer(val viewport: PDFPageViewport, val content: TextContent) {

  lazy val items: List[TextContentItem] = content.items.toList

  lazy val strs: List[String] = items.map(i=>i.str)

  def render(timeout: FiniteDuration = 100 millis): Future[List[(String, Node)]] = {
    val textLayerFragment = dom.document.createDocumentFragment()
    val result = PDFJS.renderTextLayer(new RenderTextLayerParams(content, textLayerFragment, viewport, new js.Array[HTMLElement](), timeout.toMillis.toInt))
    val fut = result.promise.toFuture
    fut.map{ case _=>
        val frag = textLayerFragment
        strs.zip(frag.childNodes.collect{
          case el: Element =>
            el.innerHTML = tokenize(el.innerHTML)
            el
        }).zipWithIndex.map{
          case ((str, el), i)=>
            el.setAttribute("data-chunk-id", i.toString)
            str -> el
        }
    }
  }

  def keyMovePartition[Key](text: String)(characterize: String=> (Key, Int)) = text.foldLeft(List.empty[(Key, String)]){
    case (Nil, el) =>
      val est = el +""
      (characterize(est)._1 -> est)::Nil

    case ((key, value)::tail, el) =>
      val join = value + el
      val (k, move) = characterize(join)
      val (keep, give) = join.splitAt(join.length - move)
      if(k==key) key->join::tail else {
        (k->give)::(key, keep)::tail
      }
  }.reverse

  def matched(str: String, regex: String): (Int, Int) = {
    regex.r.findAllMatchIn(str).foldLeft((-1, 0)){
      case ( (-1, to), m)=>
        (m.start, Math.max(to, m.end))

      case ((from, to), m)=>
        (from, Math.max(to, m.end))
    }
  }

  def regexPartition(text: String, reg: String): scala.List[(Boolean, String)] = keyMovePartition(text){
    case str =>
      matched(str, reg) match {

        case (from, to) if from >= 0 && to==str.length =>
          val diff = to - from
          true -> diff

        case _ =>
          false -> 1
      }
  }

  protected def tokenize(text: String): String = {

    val sp = "\\s+"
    val (txt, _) = regexPartition(text, sp).foldLeft(("", 0)){
      case ((str, i), (true, value))=>
        (str + s"""<tspan data="delimiter">$value</tspan>""", i)
      case ((str, i), (false, value))=>
        val ni = i + 1
        (str + "<tspan data-num=\""+ni+"\">" + value + "</tspan>", ni)
    }
    txt
  }

}
