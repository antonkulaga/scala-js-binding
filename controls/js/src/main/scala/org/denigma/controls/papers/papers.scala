package org.denigma.controls.papers

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.denigma.controls.pdf._
import org.denigma.controls.pdf.extensions._
import org.querki.jquery.{$, JQuery}
import org.scalajs.dom
import org.scalajs.dom.{Event, MouseEvent}
import org.scalajs.dom.html.{Canvas, Div}
import org.scalajs.dom.raw.{DocumentFragment, Element, HTMLElement, Selection}
import rx.opmacros.Utils.Id

import scala.collection.immutable._
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.util._
import rx._
import org.denigma.binding.extensions._
import rx.Ctx.Owner.Unsafe.Unsafe
import rx.async._
import rx.async.Platform._

import scala.scalajs.js.typedarray.ArrayBuffer

case class Page(num: Int, pdf: PDFPageProxy)
{
  lazy val textContentFut: Future[TextContent] = pdf.getTextContent().toFuture
  lazy val textLayerOpt: Var[Option[TextContent]] = textContentFut.toVarOption

  def viewport(scale: Double): PDFPageViewport = pdf.getViewport(scale)
  def render(params: js.Dynamic): PDFRenderTask = pdf.render(params)
}

/*
trait Paper
{
  def name: String
  def numPages = 0
  def pages: Var[Map[Int, Page]]

  //def currentPageOpt(implicit ctx: Ctx.Owner): Rx[Option[Page]]
  def currentPage: Rx[Page]
  def currentPageNum: Var[Int]

  lazy val hasNext = currentPageNum.map(_<numPages)
  lazy val hasPrevious = currentPageNum.map(_>1)

  def nextPage() = {
    if(hasNext.now) currentPageNum() = currentPageNum.now + 1
  }

  def previousPage() = {
    if(hasNext.now) currentPageNum() = currentPageNum.now - 1
  }

}
*/

/*
case object EmptyPaper extends Paper
{
  val name = "Empty"
  val pages: Var[Map[Int, Page]] = Var(Map.empty)
  def currentPageNum: Var[Int] = Var(0)

  //def currentPageOpt(implicit ctx: Ctx.Owner): Rx[Option[Page]]  = Var(None)

}
*/
/*

case class FailedPaper(name: String, exception: Throwable, previous: Paper = EmptyPaper) extends Paper
{
  val pages: Var[Map[Int, Page]] = Var(Map.empty)
  def currentPageNum = Var(0)
  val currentPage = previous.currentPage

  //def currentPageOpt(implicit ctx: Ctx.Owner): Rx[Option[Page]]  = Var(None)
}
*/



object Paper{

  def apply(name: String, pdf: PDFDocumentProxy, pages: collection.immutable.Map[Int, Page] = Map.empty): Paper = new PaperPDF(name, pdf, pages)

}

trait Paper
{
  def name: String
  def numPages: Int
  def getPage(num: Int): Future[Page]
  def hasPage(num: Int): Boolean = num < numPages
}

case object EmptyPaper extends Paper{
  def name = "empty"
  def numPages = 0
  def getPage(num: Int) = Future.failed(new Exception("Empty paper does not contain anything!"))

}

class PaperPDF(val name: String, val pdf: PDFDocumentProxy, var pages: collection.immutable.Map[Int, Page] = Map.empty) extends Paper
  {

  lazy val numPages: Int = pdf.numPages.toInt

  protected def rightNum(num: Int) = {
    if(numPages < num) {
      dom.console.error(s"trying to load the page #$num that it higher than the page number($numPages)")
      numPages
    } else if(num <=0){
      dom.console.error(s"trying to load the page #$num that it higher than the page number")
      0
    } else num
  }

  def getPage(num: Int): Future[Page] =
  {
    val pageNum = rightNum(num)
    pages.get(pageNum).map(Future.successful).getOrElse{
      pdf.getPage(pageNum).toFuture.map(pg => Page(num, pg))
    }
  }

}

class PaperLoader(
                   workerPath: String = "/resources/pdf/pdf.worker.js",
                   var cache: collection.immutable.Map[String, Paper] = Map.empty
                 )
{

  PDFJS.workerSrc = workerPath

  def getPaper(path: String): Future[Paper] = {
    cache.get(path).map(Future.successful).getOrElse{
      PDFJS.getDocument(path).toFuture.map{
        case proxy =>
        val paper = Paper(path,proxy)
        cache = cache.updated(path, paper)
        paper
      }
    }
  }

  def getPaper(name: String, data: ArrayBuffer): Future[Paper] = {
    cache.get(name).map(Future.successful).getOrElse{
      PDFJS.getDocument(data).toFuture.map{
        case proxy =>
          val paper = Paper(name, proxy)
          cache = cache.updated(name, paper)
          paper
      }
    }
  }

  def getPaperAt(bookmark: Bookmark): Future[(Paper, Page)] = this.getPaper(bookmark.paper).flatMap(paper =>  paper.getPage(bookmark.page).map(paper -> _))

}