package org.denigma.pdf.extensions


import org.denigma.pdf._
import org.scalajs.dom
import org.scalajs.dom.ext._

import org.scalajs.dom.Node
import org.scalajs.dom.raw.{DocumentFragment, HTMLElement}

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration.FiniteDuration
import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scalajs.concurrent.JSExecutionContext.Implicits.queue

class TextLayerRenderer(val viewport: PDFPageViewport, val content: TextContent) {


  lazy val items: List[TextContentItem] = content.items.toList
  lazy val strs: List[String] = items.map(i=>i.str)

  def render(timeout: FiniteDuration = 100 millis): Future[List[(String, Node)]] = {
    var textLayerFrag: DocumentFragment = dom.document.createDocumentFragment()
    val result = PDFJS.renderTextLayer(new RenderTextLayerParams(content, textLayerFrag, viewport, new js.Array[HTMLElement](), timeout.toMillis.toInt))
    val fut = result.promise.toFuture
    fut.map{ case _=>
        val frag = textLayerFrag
        val children = frag.childNodes.toList
        strs.zip(children)
    }
  }
}
