package org.denigma.controls.pdf

import org.scalajs.dom

import scala.concurrent.Promise
import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined


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

object extensions {
  implicit def toExtendedPDFPromise[T](promise: PDFPromise[T]): ExtendedPDFPromise[T] = {
    new ExtendedPDFPromise[T](promise)
  }
}

@js.native
class TextLayerBuilder(options: TextLayerOptions) extends js.Object {

  def setTextContent(content: TextContent): Unit = js.native

  def render(timeout: Double = ???): Unit = js.native
}

@ScalaJSDefined
class TextLayerOptions(val textLayerDiv: dom.Element, val pageIndex: Int, val viewport: PDFPageViewport) extends js.Object{

}

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
