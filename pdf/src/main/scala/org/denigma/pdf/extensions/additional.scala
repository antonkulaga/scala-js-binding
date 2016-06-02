package org.denigma.pdf.extensions

import org.denigma.pdf._
import org.scalajs.dom
import org.scalajs.dom.ext._

import org.scalajs.dom.Node
import org.scalajs.dom.raw.{DocumentFragment, HTMLElement}

import scala.concurrent.Promise
import scala.concurrent.duration.FiniteDuration
import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scalajs.concurrent.JSExecutionContext.Implicits.queue


class ExtendedPDFPromise[T](val promisePDF: PDFPromise[T]) extends AnyVal {

  def toFuture = {

    val p = Promise[T]
    //val onResolve: js.Function1[T, Unit] = { case value => p.success(value) }
    //val onReject: js.Function1[String, Unit] = {case str => p.failure(new Exception(s"PDF promise exception: $str")) }
    def onResolve(value: T): Unit = p.success(value)
    def onReject(message: Any): Unit = {
      println(s"any failed: $message")
      p.failure(new Exception(s"PDF promise exception: $message"))
    }
    promisePDF.then(onResolve _, onReject _)
    p.future
  }
}


@ScalaJSDefined
class RenderTextLayerParams(val textContent: TextContent,
                            val container: Node,
                            val viewport: PDFPageViewport,
                            val textDivs: js.Array[HTMLElement],
                            val timeout: Int
                           )
                         extends js.Object

/*
createTextLayerBuilder: function (textLayerDiv, pageIndex, viewport) {
return new TextLayerBuilder({
textLayerDiv: textLayerDiv,
pageIndex: pageIndex,
viewport: viewport
});
*/

/*

this.textLayerDiv = options.textLayerDiv;
this.renderingDone = false;
this.divContentDone = false;
this.pageIdx = options.pageIndex;
this.pageNumber = this.pageIdx + 1;
this.matches = [];
this.viewport = options.viewport;
this.textDivs = [];
this.findController = options.findController || null;
this.textLayerRenderTask = null;
this._bindMouse();*/
