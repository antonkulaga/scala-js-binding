package org.denigma.pdf.extensions

import org.denigma.pdf.{PDFPageProxy, PDFPageViewport}
import org.scalajs.dom
import org.scalajs.dom.Node
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{Element, HTMLElement}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js


class PageRenderer(page: Page) {

  protected def adjustCanvasSize(canvas: Canvas, viewport: PDFPageViewport): (Canvas, PDFPageViewport) = {
    canvas.height = viewport.height.toInt
    canvas.width = viewport.width.toInt
    canvas -> viewport
  }

  def adjustSize(parent: Element, canvas: Canvas, textLayerDiv: HTMLElement, scale: Double) = {
    val viewport: PDFPageViewport = page.viewport(scale)
    adjustCanvasSize(canvas, viewport)

    alignTextLayer(parent, textLayerDiv, viewport)

  }

  protected def renderCanvasLayer(canvas: Canvas, viewport: PDFPageViewport): Future[(PDFPageViewport, PDFPageProxy)] = {
      val toRender = js.Dynamic.literal(
        canvasContext = canvas.getContext("2d"),
        viewport = viewport
      )
      page.render(toRender).toFuture transform( value => viewport -> value ,{ th =>
        dom.console.error(s"$page {page.num} rendering failed because of ${th}")
        th
      })
  }

  def render(canvas: Canvas, textLayerDiv: HTMLElement, scale: Double)
            (implicit timeout: FiniteDuration = 1 second):
    Future[(Canvas, HTMLElement, List[(String, Node)])] = {
      val viewport: PDFPageViewport = page.viewport(scale)
      adjustCanvasSize(canvas, viewport)
      renderCanvasLayer(canvas, viewport).flatMap{ case (vp, pg) =>
          val text: Future[(Canvas, HTMLElement, List[(String, Node)])] = page.textContentFut.flatMap {
            textContent =>
              val layer = new TextLayerRenderer(vp, textContent)
              //alignTextLayer(canvas.parentElement, textLayerDiv, vp)
              layer.render(timeout).map { res=>  (canvas, textLayerDiv , res)}
          }
          text
      }
  }


  protected def alignTextLayer(element: Element, textLayerDiv: HTMLElement, viewport: PDFPageViewport) = {
    textLayerDiv.style.height = viewport.height + "px"
    textLayerDiv.style.width = viewport.width + "px"
    textLayerDiv.scrollTop = element.scrollTop
    element match {
      case e: HTMLElement =>
        textLayerDiv.style.top = e.offsetTop + "px"
        textLayerDiv.style.left = e.offsetLeft + "px"
      case _ =>
    }
  }


}