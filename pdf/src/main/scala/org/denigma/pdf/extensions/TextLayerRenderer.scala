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

  protected def tokenize(text: String): String = {
    text.split(" ").zipWithIndex.map{
      case (str, i) =>"<tspan data-num=\""+i+"\">" + str + "</tspan>"
    }.reduce(_ + " " + _)
  }

}
