package org.denigma.pdf.extensions

import org.denigma.pdf._

import scala.concurrent.Future
import scala.scalajs.js

case class Page(num: Int, pdf: PDFPageProxy)
{

  lazy val textContentFut: Future[TextContent] = pdf.getTextContent().toFuture
  lazy val annotations: Future[PDFAnnotations] = pdf.getAnnotations().toFuture
  //lazy val textLayerOpt: Var[Option[TextContent]] = textContentFut.toVarOption

  def viewport(scale: Double): PDFPageViewport = pdf.getViewport(scale)
  def render(params: js.Dynamic): PDFRenderTask = pdf.render(params)
}