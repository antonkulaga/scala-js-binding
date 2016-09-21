package org.denigma.controls.papers

import org.denigma.binding.views._
import org.denigma.pdf.extensions.{Page, PageRenderer}
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw._
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._

import scala.collection.immutable.SortedMap
import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

/**
  * Created by antonkulaga on 7/30/16.
  */

trait PageView extends BindableView{


  def num: Int
  def scale: Rx[Double]
  def page: Page

  lazy val canvas = elem.children.collectFirst {
    case canv: Canvas => canv
  }.get //unsafe

  lazy val textDiv = elem.children.collectFirst {
    case canv: HTMLDivElement => canv
  }.get //unsafe

  protected def renderPage(page: Page) = {
    val pageRenderer = new PageRenderer(page)
    pageRenderer.adjustSize(elem, canvas, textDiv, scale.now)
    pageRenderer.render(canvas, textDiv, scale.now).onComplete{
      case Success(result)=>
        for {(str, node) <- result._3} {textDiv.appendChild(node) }

      case Failure(th) =>
        dom.console.error(s"cannot load the text layer for page ${page.num} because of the ${th}")
    }
  }

  override def bindView() = {
    super.bindView()
    renderPage(page)
  }

}

trait PaperView extends CollectionSortedMapView{

  override type Key = Int

  override type Value = Page

  override type ItemView <: PageView

  def scale: Rx[Double]

  def paper: Rx[Paper]

  val items: Var[SortedMap[Key, Value]] = Var(SortedMap.empty[Key,Value])

  implicit def timeout: FiniteDuration = 15 seconds

  val paperContainer = elem.children.collectFirst{
    case e: HTMLElement if e.id == "paper-container"=> e
  }.getOrElse(elem)

  override def subscribeUpdates() = {
    super.subscribeUpdates()
    paper.foreach{
      case EmptyPaper => //do nothing
      case p: PaperPDF =>
        for(i <- 1 until p.numPages){
          p.loadPage(i).onComplete{
            case Success(page) => this.items() = items.now.updated(page.num, page)
            case Failure(th) => dom.console.error(s"cannot load page $i in paper ${p.name} with exception ${th}")
          }
      }
    }
  }
}
