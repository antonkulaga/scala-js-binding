package org.denigma.preview

import org.denigma.binding.extensions.sq
import org.denigma.binding.views.{OrganizedView, ViewInjector, BindableView}
import org.querki.jquery._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.semantic.SidebarConfig
import org.semantic.ui._

import scala.collection.immutable.Map
import scala.scalajs.js.annotation.JSExport
import scala.util.Try

/**
 * Just a simple view for the whole app, if interested ( see https://github.com/antonkulaga/scala-js-binding )
 */
@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  override def name = "main"

  override val params: Map[String, Any] = Map.empty

  lazy val elem: HTMLElement = dom.document.body

  val sidebarParams = SidebarConfig.exclusive(false).dimPage(false).closable(false).useLegacy(true)

  /**
   * Register views
   */
  override lazy val injector = defaultInjector
    .register("sidebar", (el, params) =>Try(new SidebarView(el,params)))
    .register("random", (el,params) => Try(new RandomView(el,params)))
    .register("menu", (el,params) => Try(new MenuView(el,params)))
    .register("testmenu", (el,params) => Try(new MenuView(el,params)))
    .register("BindSlide",(el,params)=>Try(new BindSlide(el,params)))
    .register("CollectionSlide",(el, params) =>Try(new CollectionSlide(el,params)))
    .register("random",(el,params)=> Try (new RandomView(el,params))  )
    .register("lists",(el,params)=>Try (new LongListView(el,params)))
    .register("test-macro", (el,params)=>Try(new TestMacroView(el,params)))
    .register("RdfSlide", (el,params)=>Try(new RdfSlide(el,params)))



    //.register("test", (el,params)=>Try(new Test(el,params)))






  @JSExport
  def main(): Unit = {
    this.bindView(this.viewElement)
  }

  @JSExport
  def showLeftSidebar() = {
    $(".left.sidebar").sidebar(sidebarParams).show()
  }

  @JSExport
  def load(content: String, into: String): Unit = {
    dom.document.getElementById(into).innerHTML = content
  }

  @JSExport
  def moveInto(from: String, into: String): Unit = {
    for {
      ins <- sq.byId(from)
      intoElement <- sq.byId(into)
    } {
      this.loadElementInto(intoElement, ins.innerHTML)
      ins.parentNode.removeChild(ins)
    }
  }

  override def activateMacro(): Unit = {
    extractors.foreach(_.extractEverything(this))
  }

  def attachBinders() = {
    this.binders = BindableView.defaultBinders(this)
  }
}
