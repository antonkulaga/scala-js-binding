package org.denigma.preview.slides
/*
import org.denigma.binding.binders.Events
import org.denigma.controls.code.CodeBinder
import org.denigma.controls.papers._
import org.denigma.preview.WebMessagesTransport
import org.denigma.preview.messages.WebMessages
import org.querki.jquery.$
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{Element, _}
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._

import scala.collection.immutable._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.typedarray.ArrayBuffer

class PaperView(val elem: Element) extends Annotator {

  lazy val subscriber = new WebMessagesTransport("test", "guest"+Math.random())

  lazy val loadedPapers = Var(Map.empty[String, Paper])

  lazy val paperLoader: PaperLoader = WebSocketPaperLoader(subscriber, loadedPapers)

  //start location to run
  val location: Var[Bookmark] = Var(Bookmark("toggle_switch/403339a0.pdf", 1))

  val selections = location.map(b=>b.selections)

  val paperURI = location.map(_.paper)


  val canvas: Canvas  = $("#the-canvas").get(0).asInstanceOf[Canvas]
  //val $textLayerDiv: JQuery = $("#text-layer")
  val textLayerDiv: HTMLElement = dom.document.getElementById("text-layer").asInstanceOf[HTMLElement]

  //val paperName = paperManager.currentPaper.map(_.name)


  val nextPage = Var(Events.createMouseEvent())
  val previousPage = Var(Events.createMouseEvent())


  override def bindView(): Unit = {
    super.bindView()
    subscribePapers()
   }

  override def subscribePapers():Unit = {
    nextPage.triggerLater{
      val b = location.now
      println(s"next click ${b.page + 1}")
      location() = location.now.copy(page = b.page +1)
      //println("nextPageClick works")
      //paperManager.currentPaper.now.nextPage()
    }
    previousPage.triggerLater{
      val b = location.now
      println("previous click")
      //println("previousPageClick works")
      //paperManager.currentPaper.now.previousPage()
      location() = location.now.copy(page = b.page - 1)
    }
    super.subscribePapers()
  }

  /**
    * Register views
    */
  override lazy val injector = defaultInjector
    .register("Bookmarks"){
      case (el, args) =>  new BookmarksView(el, location, textLayerDiv).withBinder(new CodeBinder(_))
    }

 }
*/