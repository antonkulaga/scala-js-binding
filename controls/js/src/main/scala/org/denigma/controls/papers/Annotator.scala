package org.denigma.controls.papers

/*
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.pdf.PDFPageViewport
import org.denigma.pdf.extensions.{Page, PageRenderer, TextLayerRenderer}
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

  def canvas: Canvas

  val textLayerDiv: dom.html.Element

  def paperURI: Rx[String]

  val location: Var[Bookmark]

  val selections: Rx[scala.List[TextLayerSelection]]

  val scale = Var(1.4)

  val currentPaper: Var[Paper] = Var(EmptyPaper)
  val currentPage: Var[Option[Page]] = Var(None)
  val currentPageNum = currentPage.map{
    case None=> 0
    case Some(num) => num.num
  }

  //val paperManager: PaperManager = new PaperManager(currentBookmark, Var(Map.empty), workerPath)
  def paperLoader: PaperLoader

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

  def refreshPage() = if(this.currentPage.now.nonEmpty){
    val paper = currentPaper.now
    paper.loadPage(currentPage.now.get.num).onSuccess{
      case pg: Page => onPageChange(Some(pg))
    }
  }

   protected def onPageChange(pageOpt: Option[Page]): Unit =  pageOpt match
   {
     case Some(page) =>
       deselect(textLayerDiv)
       val pageRenderer = new PageRenderer(page)
       pageRenderer.render(canvas, textLayerDiv, scale.now).onComplete{
         case Success(result)=>
           select(textLayerDiv)
         case Failure(th) =>
           dom.console.error(s"cannot load the text layer for ${location.now}")
       }
     case None =>
        //println("nothing changes")
        textLayerDiv.innerHTML = ""
   }

  import scala.concurrent.duration._
  implicit def timeout: FiniteDuration = 10 seconds

  protected def deselect(element: Element) = {
    selections.now.foreach {
      case sel =>
        val spans = sel.selectTokenSpans(element)
        spans.foreach {
          case sp =>
            if (sp.classList.contains("highlight"))
              sp.classList.remove("highlight")
        }
    }
  }

  protected def select(element: Element) = {
    selections.now.foreach{
      case sel=>
        println("chunks are: ")
        val spans = sel.selectTokenSpans(element)
        //spans.foreach(s=>println(s.outerHTML))
        spans.foreach{
          case sp=>
            if(!sp.classList.contains("highlight"))
              sp.classList.add("highlight")
        }
    }
  }

  protected def onLocationUpdate(bookmark: Bookmark): Unit = {
    //print(s"bookmark update $bookmark")
    val paper = currentPaper.now
    if(paper.name != bookmark.paper){
      paperLoader.getPaperAt(bookmark).onComplete{
        case Success( (p,  page) ) =>
          currentPaper() = p
          currentPage()= Some( page )
        case Failure(th) =>
          dom.console.error(s"cannot load the paper at ${bookmark} with error ${th}")
      }
    } else
    if(!currentPage.now.map(_.num).contains(bookmark.page)) {
      paper.loadPage(bookmark.page).onComplete{
        case Success(result) =>
          currentPage() = Some(result)
          //println(s"current page update to ${bookmark.page}")
        case Failure(th) =>
          dom.console.error(s"cannot load the page at ${bookmark}")
      }
    }
  }

  protected def subscribePapers(): Unit ={
    currentPage.onChange(onPageChange)
    location.foreach(onLocationUpdate)
    scale.onChange{ case sc=> refreshPage()  }
  }
}
*/