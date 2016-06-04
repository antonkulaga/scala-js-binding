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

  def render(canvas: Canvas, textLayerDiv: HTMLElement, scale: Double, timeout: FiniteDuration = 300 millis): Future[(HTMLElement, List[(String, Node)])] = {
    val viewport: PDFPageViewport = page.viewport(scale)
    var context: js.Dynamic = canvas.getContext("2d") //("webgl")
    canvas.height = viewport.height.toInt
    canvas.width = viewport.width.toInt
    page.render(js.Dynamic.literal(
      canvasContext = context,
      viewport = viewport
    ))
    page.textContentFut.flatMap {
      case textContent =>
        val layer = new TextLayerRenderer(viewport, textContent)
        alignTextLayer(canvas, textLayerDiv, viewport)
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
