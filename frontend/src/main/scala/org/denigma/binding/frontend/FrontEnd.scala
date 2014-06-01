package org.denigma.binding.frontend

import org.scalajs.dom
import scalatags.all._
import scalatags.HtmlTag
import rx._
import scala.scalajs.js.annotation.JSExport
import org.denigma.extensions._
import org.scalajs.jquery._
import scala.util.Try
import org.denigma.views.OrdinaryView
import scala.collection.immutable.Map
import org.scalajs.dom.{HTMLElement, MouseEvent}
import scala.scalajs.js
import org.denigma.binding.frontend.tests.{LongListView, RandomView}
import org.denigma.extensions._
import org.denigma.binding.frontend.slides.{TestModelView, RemoteSlide, BindSlide, SlideView}
import org.denigma.tools.CodeMirrorView
import org.denigma.binding.frontend.tools.CodeView
import org.denigma.binding.models.{RegisterPicklers=>rp}

@JSExport
object FrontEnd extends OrdinaryView("main",dom.document.body)  with scalajs.js.JSApp
{

  val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)


  val sidebarParams =  js.Dynamic.literal(exclusive = false)
  /**
   * Register views
   */
  org.denigma.views
    .register("menu", (el, params) =>Try{ new MenuView(el,params) })
    .register("ArticleView", (el, params) =>Try(new ArticleView(el,params)))
    .register("sidebar", (el, params) =>Try(new SidebarView(el,params)))
    .register("random",(el,params)=> Try {new RandomView(el,params)})
    .register("lists",(el,params)=>Try {new LongListView(el,params)})
    .register("SlideView",(el,params)=>Try {new SlideView(el,params)})
    .register("BindSlide",(el,params)=>Try {new BindSlide(el,params)})
    .register("RemoteSlide",(el,params)=>Try {new RemoteSlide(el,params)})
    .register("CodeView",(el,params)=>Try {new CodeView(el,params)})
    .register("TestModelView",(el,params)=>Try{new TestModelView(el)})


  //    .register("righ-menu", (el, params) =>Try(new RightMenuView(el,params)))

  @JSExport
  def main(): Unit = {
    rp.registerPicklers()
    this.bind(this.viewElement)
    jQuery(".top.sidebar").dyn.sidebar(sidebarParams).sidebar("show")
    jQuery(".left.sidebar").dyn.sidebar(sidebarParams).sidebar("show")
  }

  @JSExport
  def load(content:String,into:String): Unit = {
    dom.document.getElementById(into).innerHTML = content
  }

  @JSExport
  def moveInto(from:String,into:String): Unit = {
    val ins: HTMLElement = dom.document.getElementById(from)

    val intoElement = dom.document.getElementById(into)
    this.loadElementInto(intoElement,ins.innerHTML)


    //dom.document.getElementById(into).innerHTML =ins.innerHTML
    ins.parentNode.removeChild(ins)
  }


  val toggle: Var[MouseEvent] = Var(this.createMouseEvent())

  toggle.handler{
    jQuery(".left.sidebar").dyn.sidebar(sidebarParams).sidebar("toggle")
  }
//  val onMenuOver: Var[MouseEvent] = Var(this.createMouseEvent())
//  val onMenuOut: Var[MouseEvent] = Var(this.createMouseEvent())



}
