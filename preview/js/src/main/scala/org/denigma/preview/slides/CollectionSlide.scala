package org.denigma.preview.slides

import org.denigma.binding.binders.{MapItemsBinder, NavigationBinder, GeneralBinder, Events}
import org.denigma.binding.extensions._
import org.denigma.binding.views.{ItemsSeqView, MapCollectionView, BindableView}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.Element
import rx._
import rx.core.Var

import scala.collection.immutable.{Map, Seq}
import scala.util._

class CollectionSlide(val elem: Element) extends BindableView with CollectionSlideCode{

  def parseHTML(string: String): Option[Element] ={
    val p = new  DOMParser()
    Try {
      p.parseFromString(string, "text/html")
    } match {
      case Success(doc) =>
        dom.document.body.children.collectFirst{case html: Element => html}

      case Failure(th) =>
        dom.console.error(th.toString)
        None
    }
  }

  override def name = "COLLECTION_SLIDE"

  val code = Var("")
  val apply = Var(Events.createMouseEvent())
  this.apply.handler {
      this.findView("testmenu") match {
        case Some(view: BindableView) =>
          dom.console.log("ID IS = "+view.id)
          dom.console.log("HTML is = "+view.elem.outerHTML)
          this.parseHTML(code.now).foreach{case c =>
            dom.console.log("CODE NOW IS"+code.now)
            dom.console.log("CODE HTML"+c.outerHTML)
            view.refreshMe(c)
          }
        case _ => dom.console.error("test menu not found")}
  }
    override lazy val injector = defaultInjector

    .register("testmenu"){
      case (el, args) => new TestMenuView(el)
        .withBinder(new GeneralBinder(_)).withBinder(new NavigationBinder(_))
    }



}

trait CollectionSlideCode {

  val collectionCode = Var(
  """
    |// it will be our item for the menu
    |case class MenuItem(uri: String, label: String)
    |
    |class TestMenuItemView(val elem: Element, menuItem: Rx[MenuItem]) extends BindableView
    |{
    |  // to add extensions like .map to reactive variables
    |  import rx.ops._
    |
    |  // let's make a label reactive variable to use for the binding to html element property
    |  // menuItem.map(_.label) is the same as Rx{ menuItem().label }
    |  val label:Rx[String] = menuItem.map(_.label)
    |
    |  // let's make the same with URI
    |  val uri:Rx[String] = menuItem.map(_.uri)
    |}
    |
    |
    |class TestMenuView(val elem: Element) extends ItemsSeqView {
    |
    |  self =>
    |
    |  // we use MenuItem wrapped in a reactive variable here to be able to dynamicly change menu item when needed
    |  override type Item = Rx[MenuItem]
    |
    |  override type ItemView = TestMenuItemView
    |
    |  // we use helper function "construct view" that will help us to create view for the item
    |  def newItem(item: Item): ItemView = this.constructItemView(item){
    |    // it already creates Element ( el ) for you by copying the template
    |    // and it also extracts all params (to mp) from the template element
    |    (el, mp) =>
    |      // let's create a view for the item and pass it element and the item
    |      new TestMenuItemView(el, item)
    |      // here with _ we pass the view to the GeneralBinder that will extract reactive variables from the view
    |      .withBinder(new GeneralBinder(_))
    |      // navigation binder is useful for loading http pages in an AJAX way
    |      .withBinder(new NavigationBinder(_))
    |  }
    |
    |  // let's add some data for the test
    |  override val items: Rx[Seq[Item]] = Var(
    |    Seq(
    |      Var(MenuItem("pages/bind", "Basic binding example")),
    |      Var(MenuItem("pages/start", "Getting Started")),
    |      Var(MenuItem("pages/collection", "Collection binding")),
    |      Var(MenuItem("pages/controls", "Various controls")),
    |      Var(MenuItem("pages/charts", "Charts")),
    |      Var(MenuItem("pages/rdf", "RDF support"))
    |    )
    |  )
    |}
    |
  """.stripMargin
  )
}

// it will be our item for the menu
case class MenuItem(uri: String, label: String)

class TestMenuItemView(val elem: Element, menuItem: Rx[MenuItem]) extends BindableView
{
  // to add extensions like .map to reactive variables
  import rx.ops._

  // let's make a label reactive variable to use for the binding to html element property
  // menuItem.map(_.label) is the same as Rx{ menuItem().label }
  val label:Rx[String] = menuItem.map(_.label)

  // let's make the same with URI
  val uri:Rx[String] = menuItem.map(_.uri)
}


class TestMenuView(val elem: Element) extends ItemsSeqView {

  self =>

  // we use MenuItem wrapped in a reactive variable here to be able to dynamicly change menu item when needed
  override type Item = Rx[MenuItem]

  override type ItemView = TestMenuItemView

  // we use helper function "construct view" that will help us to create view for the item
  def newItemView(item: Item): ItemView = this.constructItemView(item){
    // it already creates Element ( el ) for you by copying the template
    // and it also extracts all params (to mp) from the template element
    (el, mp) =>
      // let's create a view for the item and pass it element and the item
      new TestMenuItemView(el, item)
      // here with _ we pass the view to the GeneralBinder that will extract reactive variables from the view
      .withBinder(new GeneralBinder(_))
      // navigation binder is useful for loading http pages in an AJAX way
      .withBinder(new NavigationBinder(_))
  }

  // let's add some data for the test
  override val items: Rx[Seq[Item]] = Var(
    Seq(
      Var(MenuItem("pages/bind", "Basic binding example")),
      Var(MenuItem("pages/start", "Getting Started")),
      Var(MenuItem("pages/collection", "Collection binding")),
      Var(MenuItem("pages/controls", "Various controls")),
      Var(MenuItem("pages/charts", "Charts")),
      Var(MenuItem("pages/rdf", "RDF support"))
    )
  )
}
