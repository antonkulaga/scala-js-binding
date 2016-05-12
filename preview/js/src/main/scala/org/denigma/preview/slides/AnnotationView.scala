package org.denigma.preview.slides

import scalajs.concurrent.JSExecutionContext.Implicits.queue

import java.nio.ByteBuffer

import org.denigma.binding.binders.Events
import org.denigma.binding.extensions._
import org.denigma.controls.code.CodeBinder
import org.denigma.controls.papers._
import org.denigma.preview.WebSocketTransport
import org.denigma.preview.messages.WebMessages
import org.querki.jquery.$
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{Blob, BlobPropertyBag, Element, FileReader}
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._

import scala.collection.immutable._
import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._
import scala.scalajs.js
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}

case class WebSocketPaperLoader(subscriber: WebSocketTransport,
                                loadedPapers: Var[Map[String, Paper]])
  extends PaperLoader {


  subscriber.input.onChange{
    case inp =>
      println("input is going: " + inp)
  }

  override def getPaper(path: String, timeout: FiniteDuration = 25 seconds): Future[Paper] =
    this.subscriber.ask[Future[ArrayBuffer]](WebMessages.Load(path), timeout){
      case WebMessages.DataMessage(source, bytes) =>
        println("PAPER message received!")
        //val arr= new Uint8Array(data.toJSArray)
        bytes2Arr(bytes)
    }.flatMap{case arr=>arr}.flatMap{ case arr=>  super.getPaper(path, arr) }

  import js.JSConverters._

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

  subscriber.open()

}

class AnnotationView(val elem: Element) extends Annotator {


  lazy val subscriber = new WebSocketTransport("test", "guest"+Math.random())

  lazy val loadedPapers = Var(Map.empty[String, Paper])

  lazy val paperLoader: PaperLoader = WebSocketPaperLoader(subscriber, loadedPapers)

  //start location to run
  val location = Var(Bookmark("toggle_switch/403339a0.pdf", 1))

  val paperURI = location.map(_.paper)


  val canvas: Canvas  = $("#the-canvas").get(0).asInstanceOf[Canvas]
  //val $textLayerDiv: JQuery = $("#text-layer")
  val textLayerDiv: Element = dom.document.getElementById("text-layer")//.asInstanceOf[HTMLElement]

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