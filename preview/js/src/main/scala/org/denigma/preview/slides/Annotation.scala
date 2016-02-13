package org.denigma.preview.slides

import org.denigma.binding.binders.{GeneralBinder, Events}
import org.denigma.binding.views.{ItemsSeqView, BindableView}
import org.denigma.controls.pdf._
import org.denigma.controls.pdf.extensions._
import org.denigma.preview.FrontEnd._
import org.querki.jquery.{JQuery, $}
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.Element
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

case class Page(pdf: PDFPageProxy)
{
  lazy val textContentFut = pdf.getTextContent().toFuture
  lazy val textLayerOpt = textContentFut.toVarOption

  def viewport(scale: Double) = pdf.getViewport(scale)
  def render(params: js.Dynamic) = pdf.render(params)
}

trait Paper
{
  def numPages = 0
  def pages: Var[Map[Int, Page]]

  def currentPageOpt(implicit ctx: Ctx.Owner): Rx[Option[Page]]
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

case object EmptyPaper extends Paper
{
  val pages: Var[Map[Int, Page]] = Var(Map.empty)
  def currentPageNum: Var[Int] = Var(0)

  def currentPageOpt(implicit ctx: Ctx.Owner): Rx[Option[Page]]  = Var(None)

}

case class FailedPaper(exception: Throwable, previous: Paper = EmptyPaper) extends Paper
{
  val pages: Var[Map[Int, Page]] = Var(Map.empty)
  def currentPageNum = Var(0)

  def currentPageOpt(implicit ctx: Ctx.Owner): Rx[Option[Page]]  = Var(None)

}

/**
  * Class that wraps PDF paper and caches its pages
  *
  * @param pdf
  * @param pages
  * @param currentPageNum
  */
case class PaperPDF(pdf: PDFDocumentProxy,
                    currentPageNum: Var[Int],
                    pages: Var[Map[Int, Page]] = Var(Map.empty)
                   )
  extends Paper {

  override lazy val numPages: Int = pdf.numPages.toInt



  currentPageNum.foreach(loadPage)

  def currentPageOpt(implicit ctx: Ctx.Owner): Rx[Option[Page]]  =  Rx{
    val pg = pages()
    pg.get(currentPageNum())
  }

  private def rightNum(num: Int) = {
    if(numPages < num) {
      dom.console.error(s"trying to load the page #$num that it higher than the page number($numPages)")
      numPages
    } else if(num <=0){
      dom.console.error(s"trying to load the page #$num that it higher than the page number")
      0
    } else num
  }

  protected def loadPage(num: Int): Unit =
  {
    val pageNum = rightNum(currentPageNum.now)
    if(!pages.now.contains(num)) {
      pdf.getPage(pageNum).toFuture.onSuccess{ case value => pages() = pages.now.updated(pageNum , Page(value)) }
    }
  }

}

class PaperManager(
                   workerPath: String = "/resources/pdf/pdf.worker.js",
                   currentPaperURI: Rx[String] = Var(""),
                   currentPageNum: Var[Int] = Var(1),
                   papers: Var[Map[String, Paper]] = Var(Map.empty)

                  )
{
  PDFJS.workerSrc = "/resources/pdf/pdf.worker.js"

  val currentPaper:Rx[Paper] = Rx.apply{
    val uri = currentPaperURI()
    papers().getOrElse(uri, EmptyPaper)
  }

  val currentPageOpt: Rx[Option[Page]] = Rx{
   val paper = currentPaper()
    val page = paper.currentPageOpt
    page()
  }
  currentPaperURI.foreach{
    path =>
    //fut.onSuccess{ case value=> papers() = papers() + (path -> value) }
    papers.now.get(path) match {
      case Some(paper: PaperPDF) => //do nothing

      case other =>
        val fut = PDFJS.getDocument(path).toFuture
        fut.onComplete{
          case Success(result) => papers() = papers.now.updated(path, PaperPDF(result, currentPageNum))
          case Failure(error) => other match {
            case Some(f: FailedPaper) => f.copy(exception = error)
            case Some(p) => FailedPaper(error, p)
            case None => FailedPaper(error, EmptyPaper)
          }
        }
    }
  }

}

class Bookmarks(val elem: Element) extends ItemsSeqView {

  override type Item = Var[String]

  override type ItemView = Bookmark

  override val items: Rx[Seq[Item]] = Var(List.empty)

  override def newItemView(item: Item): ItemView  = this.constructItemView(item){
    case (el, mp) => new Bookmark(el, item)
  }

}

class Bookmark(val elem: Element, val bookmark: Var[String]) extends BindableView {

}


class Annotation(val elem: Element) extends Annotator {

  lazy val paperURI: Var[String] = Var("/resources/pdf/eptcs.pdf")
  val canvas: Canvas  = $("#the-canvas").get(0).asInstanceOf[Canvas]
  val $textLayerDiv: JQuery = $("#text-layer")

  val nextPage = Var(Events.createMouseEvent())
  val previousPage = Var(Events.createMouseEvent())

  override def bindView(): Unit = {
    super.bindView()
    paperManager.currentPageOpt.foreach(onPageOptionChange)
    nextPage.triggerLater{
      //println("nextPageClick works")
      paperManager.currentPaper.now.nextPage()
    }
    previousPage.triggerLater{
      //println("previousPageClick works")
      paperManager.currentPaper.now.previousPage()
    }

    //currentPaper.onChange(onPaperChange)
    //paperURI.foreach(loadPaper)
  }
  /**
    * Register views
    */
  override lazy val injector = defaultInjector
    .register("Bookmarks"){
      case (el, args) =>  new Bookmarks(el).withBinder(new GeneralBinder(_))
    }

 }


trait Annotator extends BindableView{

  def canvas: Canvas //= $("#the-canvas").get(0).asInstanceOf[Canvas]
  def $textLayerDiv: JQuery

  def workerPath: String = "/resources/pdf/pdf.worker.js"

  def paperURI: Rx[String]

  val currentPageNum: Var[Int] = Var(1)
  val paperManager: PaperManager = new PaperManager(workerPath, paperURI, currentPageNum)
  val currentPaper = paperManager.currentPaper
  val hasNextPage: Rx[Boolean] = currentPaper.flatMap(_.hasNext).asInstanceOf[Rx[Boolean]] //ugly but to overcome the bug
  val hasPreviousPage: Rx[Boolean] = currentPaper.flatMap(_.hasPrevious).asInstanceOf[Rx[Boolean]]


  protected def alignTextLayer(viewport: PDFPageViewport) = {
    $textLayerDiv
      .css("height", viewport.height + "px")
      .css("width", viewport.width + "px")
      .css("top", canvas.offsetTop + "px")
      .css("left", canvas.offsetLeft + "px")
  }

  protected def onPageOptionChange(pageOpt: Option[Page]) = for(page <- pageOpt) {
    println(s"page option change with ${page}")
    var scale = 1.0
    val viewport: PDFPageViewport = page.viewport(scale)
    var context = canvas.getContext("2d")
    canvas.height = viewport.height.toInt
    canvas.width =  viewport.width.toInt
    page.render(js.Dynamic.literal(
      canvasContext = context,
      viewport = viewport
    ))
    val textContentFut = page.textContentFut.onSuccess{
      case textContent =>
        println(s"text layer update with ${textContent.items.length} items")
        for(item <- textContent.items)
        {
          println(s"TEXT = ${item.str}")
        }
        alignTextLayer(viewport)
        $textLayerDiv.get(0).toOption match {
          case Some(div) =>
            val textLayerOptions = new TextLayerOptions(div, 1, viewport)
            val textLayer = new TextLayerBuilder(textLayerOptions)
            textLayer.setTextContent(textContent)
            textLayer.render()

          case None => dom.console.error(s"cannot find div for the text layer")
        }
    }
  }
}

/*

// Loading document.
PDFJS.getDocument(DEFAULT_URL).then(function (pdfDocument) {
// Document loaded, retrieving the page.
return pdfDocument.getPage(PAGE_TO_VIEW).then(function (pdfPage) {
// Creating the page view with default parameters.
var pdfPageView = new PDFJS.PDFPageView({
container: container,
id: PAGE_TO_VIEW,
scale: SCALE,
defaultViewport: pdfPage.getViewport(SCALE),
// We can enable text/annotations layers, if needed
textLayerFactory: new PDFJS.DefaultTextLayerFactory(),
annotationLayerFactory: new PDFJS.DefaultAnnotationLayerFactory()
});
// Associates the actual page with the view, and drawing it
pdfPageView.setPdfPage(pdfPage);
return pdfPageView.draw();
});
});*/
