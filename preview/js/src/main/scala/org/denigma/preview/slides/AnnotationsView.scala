package org.denigma.preview.slides

import org.denigma.binding.views.BindableView
import org.denigma.controls.code.CodeBinder
import org.denigma.controls.papers._
import org.denigma.pdf.extensions.Page
import org.denigma.preview.WebMessagesTransport
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import rx.{Rx, Var}

import scala.concurrent.duration._
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}


class AnnotationsView(val elem: Element, val paperURI: String) extends BindableView {
  lazy val subscriber = new WebMessagesTransport("test", "guest"+Math.random())

  lazy val loadedPapers = Var(Map.empty[String, Paper])

  lazy val paperLoader: PaperLoader = WebSocketPaperLoader(subscriber, loadedPapers)

  val paper: Var[Paper] = Var(EmptyPaper)

  override def bindView() = {
    super.bindView()
    paperLoader.getPaper(paperURI, 15 seconds).onComplete{
      case Failure(th) => dom.console.error(s"cannot load paper ${paperURI}")
      case Success(value) => paper() = value
    }
  }

  override lazy val injector = defaultInjector
    .register("Publication"){
      case (el, args) =>  new PublicationView(el, paper).withBinder(v => new CodeBinder(v))
    }

}

class PublicationView(val elem: Element, val paper: Rx[Paper]) extends PaperView {

  lazy val scale = Var(1.4)

  override type ItemView = ArticlePageView

  override def newItemView(item: Int, value: Page): ItemView = this.constructItemView(item){
    case (el, args) =>
      val view = new ItemView(el, item, value, scale).withBinder(v=>new CodeBinder(v))
      view
  }

  override def updateView(view: ArticlePageView, key: Int, old: Page, current: Page): Unit = {
    dom.console.error("page view should be not updateble!")
  }
}

class ArticlePageView(val elem: Element, val num: Int, val page: Page, val scale: Rx[Double])  extends PageView {
  val name = Var("page_"+num)
  val title = Var("page_"+num)
}
