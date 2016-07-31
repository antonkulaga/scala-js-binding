package org.denigma.controls.papers

import org.denigma.binding.extensions._
import org.denigma.pdf._
import org.denigma.pdf.extensions._
import org.scalajs.dom
import org.scalajs.dom.FileReader
import org.scalajs.dom.raw.{Blob, BlobPropertyBag, ProgressEvent}
import rx._

import scala.collection.immutable._
import scala.concurrent.{Future, Promise}
import scala.concurrent.duration.FiniteDuration
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}
import js.JSConverters._

object Paper{

  def apply(name: String, pdf: PDFDocumentProxy, pages: collection.immutable.Map[Int, Page] = Map.empty): Paper = new PaperPDF(name, pdf, Var(pages))

}

trait Paper
{
  def name: String
  def numPages: Int
  def loadPage(num: Int): Future[Page]
  def hasPage(num: Int): Boolean = num < numPages
}

case object EmptyPaper extends Paper{
  def name = "empty"
  def numPages = 0
  def loadPage(num: Int) = Future.failed(new Exception("Empty paper does not contain anything!"))
}

class PaperPDF(val name: String, val pdf: PDFDocumentProxy, protected val pages: Var[collection.immutable.Map[Int, Page]] = Var(Map.empty)) extends Paper
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

  def pageMap(implicit owner: Ctx.Owner) = Rx{ pages() }

  def subscribe(pgs: Var[collection.immutable.Map[Int, Page]])(implicit owner: Ctx.Owner): Obs =
  {
    pages.foreach(r => pgs() = r)
    //do nothing
  }

  def loadPage(num: Int): Future[Page] =
  {
    val pageNum = rightNum(num)
    pages.now.get(pageNum).map(Future.successful).getOrElse{
      pdf.getPage(pageNum).toFuture.map{case pg =>
        val page = Page(num, pg)
        pages() = pages.now.updated(num, page)
        page
      }
    }
  }

}

trait PaperLoader
{

  def loadedPapers: Var[Map[String, Paper]]

  val workerPath: String = "/resources/pdf/pdf.worker.js"
  //var cache: collection.immutable.Map[String, Paper] = Map.empty


  PDFJS.workerSrc = workerPath

  def getPaper(path: String, timeout: FiniteDuration): Future[Paper]

  def getPaper(name: String, data: ArrayBuffer): Future[Paper] = {
    loadedPapers.now.get(name).map(Future.successful).getOrElse{
      PDFJS.getDocument(data).toFuture.map{
        case proxy =>
          val paper = Paper(name, proxy)
          loadedPapers() = loadedPapers.now.updated(name, paper)
          paper
      }
    }
  }


  def bytes2Arr(data: Array[Byte]): Future[ArrayBuffer] = {
    val p = Promise[ArrayBuffer]
    val options = BlobPropertyBag("octet/stream")
    val arr: Uint8Array = new Uint8Array(data.toJSArray)
    val blob = new Blob(js.Array(arr), options)
    //val url = dom.window.dyn.URL.createObjectURL(blob)
    val reader = new FileReader()
    def onLoadEnd(ev: ProgressEvent): Any = {
      p.success(reader.result.asInstanceOf[ArrayBuffer])
    }
    reader.onloadend = onLoadEnd _
    reader.readAsArrayBuffer(blob)
    p.future
  }
/*

  def bytes2Arr(data: Array[Byte]): Future[ArrayBuffer] = {
    val p = Promise[ArrayBuffer]
    val options = BlobPropertyBag("octet/stream")
    val arr: Uint8Array = new Uint8Array(data.toJSArray)
    val blob = new Blob(js.Array(arr), options)
    //val url = dom.window.dyn.URL.createObjectURL(blob)
    val reader = new FileReader()
    def onLoadEnd(ev: ProgressEvent): Any = {
      p.success(reader.result.asInstanceOf[ArrayBuffer])
    }
    reader.onloadend = onLoadEnd _
    reader.readAsArrayBuffer(blob)
    p.future
  }
  */


  def getPaperAt(bookmark: Bookmark)(implicit timeout: FiniteDuration): Future[(Paper, Page)] =
    this.getPaper(bookmark.paper, timeout).flatMap(paper =>  paper.loadPage(bookmark.page).map(paper -> _))

}