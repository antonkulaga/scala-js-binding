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
import models.RegisterPicklers
import models.{RegisterPicklers=>rp}
import scala.scalajs.js
import org.denigma.binding.frontend.tests.{LongListView, RandomView}
import org.denigma.extensions._

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
    dom.document.getElementById(into).innerHTML =ins.innerHTML

    ins.parentNode.removeChild(ins)
  }


  val toggle: Var[MouseEvent] = Var(this.createMouseEvent())

  toggle.handler{
    jQuery(".left.sidebar").dyn.sidebar(sidebarParams).sidebar("toggle")
  }
//  val onMenuOver: Var[MouseEvent] = Var(this.createMouseEvent())
//  val onMenuOut: Var[MouseEvent] = Var(this.createMouseEvent())



}
