package org.denigma.binding.frontend

import org.denigma.binding.frontend.papers.{Task, TasksView, Report, ReportsView}
import org.denigma.binding.views.OrdinaryView
import org.denigma.controls.general.{CodeMirrorEditor, CkEditor}

import org.scalajs.dom
import rx._
import scala.scalajs.js.annotation.JSExport
import org.scalajs.jquery._
import scala.util.Try
import scala.collection.immutable.Map
import org.scalajs.dom.{HTMLElement, MouseEvent}
import scala.scalajs.js
import org.denigma.binding.frontend.tests.{PicklerView, LongListView, RandomView}
import org.denigma.binding.extensions._
import org.denigma.binding.frontend.slides._
import org.denigma.binding.frontend.tools.{SelectView, CodeInsideView, CodeView}
import org.denigma.binding.picklers.rp
import scalatags.Text.Tag


@JSExport
object FrontEnd extends OrdinaryView with scalajs.js.JSApp
{

  val name = "main"
  lazy val elem:HTMLElement = dom.document.body

  val tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)


  val sidebarParams =  js.Dynamic.literal(exclusive = false)
  /**
   * Register views
   */
  org.denigma.binding.views
    .register("menu", (el, params) =>Try(new MenuView(el,params)))
    .register("ArticleView", (el, params) =>Try(new ArticleView(el,params)))
    .register("sidebar", (el, params) =>Try(new SidebarView(el,params)))
    .register("random",(el,params)=> Try (new RandomView(el,params))  )
    .register("lists",(el,params)=>Try (new LongListView(el,params)))
    .register("SlideView",(el,params)=>Try(new SlideView(el,params)))
    .register("BindSlide",(el,params)=>Try(new BindSlide(el,params)))
    .register("RemoteSlide",(el,params)=>Try(new RemoteSlide(el,params)))
    .register("RdfSlide",(el,params)=>Try(new RdfSlide(el,params)))
    .register("Todos",(el,params)=>Try(new Todos(el,params)))
    .register("CodeView",(el,params)=>Try(new CodeView(el,params)))
    .register("TestModelView",(el,params)=>Try(new TestModelView(el,params)))
    .register("PicklerView",(el,params)=>Try(new PicklerView(el,params)))
    .register("PageEditView",(el,params)=>Try(new PageEditView(el,params)))
    .register("CodeInsideView",(el,params)=>Try(new CodeInsideView(el,params)))
    .register("TableView",  (el,params)=>Try(new TableView(el,params)))
    .register("ShapeEditor",(el,params)=>Try(new ShapeEditor(el,params)))
    .register("ShapeProperty",(el,params)=>Try(new ShapeProperty(el,params)))
    .register("todo",(el,params)=>Try(new Todo(el,params)))
    .register("Tasks",(el,params)=>Try(new TasksView(el,params)))
    .register("task",(el,params)=>Try(new Task(el,params)))

    .register("SuggestView",(el,params)=>Try(new SuggestView(el,params)))
    .register("ReportsView",(el, params) =>Try(new ReportsView(el,params)))
    .register("report",(el, params) =>Try(new Report(el,params)))
    .register("TestSelect",(el, params) =>Try(new SelectView(el,params)))


  org.denigma.binding.views
    .registerEditor("ckeditor",CkEditor)
    .registerEditor("codemirror",CodeMirrorEditor)




  //    .register("righ-menu", (el, params) =>Try(new RightMenuView(el,params)))

  @JSExport
  def main(): Unit = {
    this.bindView(this.viewElement)
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
