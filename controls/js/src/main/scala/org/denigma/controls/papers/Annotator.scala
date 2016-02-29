package org.denigma.controls.papers

import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.controls.pdf._
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.Element
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.util.{Failure, Success}

/**
  * View-trait responsible for lo
  */
trait Annotator extends BindableView {

  def canvas: Canvas //= $("#the-canvas").get(0).asInstanceOf[Canvas]
  //def $textLayerDiv: JQuery
  val textLayerDiv: Element

  def paperURI: Rx[String]

  val location: Var[Bookmark]
  //val currentPageNum: Var[Int] = Var(1)

  val currentPaper:Var[Paper] = Var(EmptyPaper)
  val currentPage: Var[Option[Page]] = Var(None)
  val currentPageNum = currentPage.map{
    case None=> 0
    case Some(num) => num.num
  }

  //val paperManager: PaperManager = new PaperManager(currentBookmark, Var(Map.empty), workerPath)
  lazy val paperLoader = new PaperLoader()

  val hasNextPage: Rx[Boolean] = Rx{
    val paper = currentPaper()
    val page = currentPage()
    page.isDefined && paper.hasPage(page.get.num + 1)
  }

  val hasPreviousPage: Rx[Boolean] = Rx{
    val paper = currentPaper()
    val page = currentPage()
    page.isDefined && paper.hasPage(page.get.num - 1)
  }


  protected def alignTextLayer(viewport: PDFPageViewport) = {
    textLayerDiv.style.height = viewport.height + "px"
    textLayerDiv.style.width = viewport.width + "px"
    textLayerDiv.style.top = canvas.offsetTop + "px"
    textLayerDiv.style.left = canvas.offsetLeft + "px"
  }

   protected def onPageChange(pageOpt: Option[Page]): Unit =  pageOpt match
   {
     case Some(page) =>
      //println(s"page option change with ${page}")
      var scale = 1.0
      val viewport: PDFPageViewport = page.viewport(scale)
      var context = canvas.getContext("2d")//("webgl")
      canvas.height = viewport.height.toInt
      canvas.width =  viewport.width.toInt
      page.render(js.Dynamic.literal(
        canvasContext = context,
        viewport = viewport
      ))
      val textContentFut = page.textContentFut.onComplete{
        case Success(textContent) =>
          alignTextLayer(viewport)
          textLayerDiv.innerHTML = ""
          val textLayerOptions = new TextLayerOptions(textLayerDiv, 1, viewport)
          val textLayer = new TextLayerBuilder(textLayerOptions)
          textLayer.setTextContent(textContent)
          //println(textContent+"!!! is TEXT")
          textLayer.render()
          location.now.selections.foreach(_.select(textLayerDiv))

        case Failure(th) =>
            dom.console.error(s"cannot load the text layer for ${location.now}")
      }
      case None =>
        //println("nothing changes")
        textLayerDiv.innerHTML = ""
   }

  protected def onLocationUpdate(bookmark: Bookmark) = {
    //print(s"bookmark update $bookmark")
    val paper = currentPaper.now
    if(paper.name != bookmark.paper){
      paperLoader.getPaperAt(bookmark).onComplete{
        case Success( (p,  page) ) =>
          currentPaper() = p
          currentPage()= Some( page )
        case Failure(th) =>
          dom.console.error(s"cannot load the paper at ${bookmark}")
      }
    } else
    if(!currentPage.now.map(_.num).contains(bookmark.page)) {
      paper.getPage(bookmark.page).onComplete{
        case Success(result) =>
          currentPage() = Some(result)
          //println(s"current page update to ${bookmark.page}")
        case Failure(th) =>
          dom.console.error(s"cannot load the page at ${bookmark}")
      }
    }
    else location.now.selections.foreach(_.select(textLayerDiv))
  }

  protected def subscribePapers(): Unit ={
    currentPage.onChange(onPageChange)
    location.foreach(onLocationUpdate)
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
