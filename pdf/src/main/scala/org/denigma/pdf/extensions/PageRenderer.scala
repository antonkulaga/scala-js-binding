package org.denigma.pdf.extensions

import org.denigma.pdf.PDFPageViewport
import org.scalajs.dom
import org.scalajs.dom.Node
import org.scalajs.dom.html.Canvas
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.scalajs.dom.raw.HTMLElement

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.scalajs.js
import scala.util.{Failure, Success}

class PageRenderer(page: Page) {

  def render(canvas: Canvas, textLayerDiv: HTMLElement, scale: Double)(implicit timeout: FiniteDuration = 1 second): Future[(HTMLElement, List[(String, Node)])] = {
    println("render fires")
    val viewport: PDFPageViewport = page.viewport(scale)
    var context: js.Dynamic = canvas.getContext("2d") //("webgl")
    canvas.height = viewport.height.toInt
    canvas.width = viewport.width.toInt
    page.render(js.Dynamic.literal(
      canvasContext = context,
      viewport = viewport
    )).toFuture.onComplete{
      case Success(value) =>
        println(s"page rendering ${page.num} succeeded")
      case Failure(th) =>
        dom.console.error(s"$page {page.num} rendering failed because of ${th}")
    }
    page.textContentFut.flatMap {
      case textContent =>
        val layer = new TextLayerRenderer(viewport, textContent)
        alignTextLayer(canvas, textLayerDiv, viewport)
        println("text render")
        layer.render(timeout).map {
          case res=>
            textLayerDiv.innerHTML = ""
            for {(str, node) <- res} {
              textLayerDiv.appendChild(node)
            }
            textLayerDiv -> res
        }
    }
  }

  protected def alignTextLayer(canvas: Canvas, textLayerDiv: HTMLElement, viewport: PDFPageViewport) = {
    textLayerDiv.style.height = viewport.height + "px"
    textLayerDiv.style.width = viewport.width + "px"
    textLayerDiv.style.top = canvas.offsetTop + "px"
    textLayerDiv.style.left = canvas.offsetLeft + "px"
  }

}
