package org.denigma.binding.frontend

import org.denigma.binding.extensions._
import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.frontend.controls.{ShapeEditor, ShapeProperty}
import org.denigma.binding.frontend.papers.{Report, ReportsView, Task, TasksBinder}
import org.denigma.binding.frontend.tests.{LongListView, PicklerView, RandomView}
import org.denigma.binding.frontend.tools.{CodeInsideView, CodeView, SelectView}
import org.denigma.binding.views.BindableView
import org.denigma.binding.views.utils.ViewInjector
import org.denigma.controls.editors.{CkEditor, CodeMirrorEditor, editors}
import org.denigma.controls.general.DatePairView
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, MouseEvent}
import org.scalajs.jquery._
import rx._

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.Try


@JSExport
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  override def name = "main"

  lazy val elem:HTMLElement = dom.document.body

  override val params:Map[String,Any] = Map.empty

  override def activateMacro(): Unit = {
    extractors.foreach(_.extractEverything(this))
  }




  val sidebarParams =  js.Dynamic.literal(exclusive = false)
  /**
   * Register views
   */
  ViewInjector
    .register("menu", (el, params) =>Try(new MenuView(el,params)))
    .register("ArticleView", (el, params) =>Try(new ArticleView(el,params)))
    .register("sidebar", (el, params) =>Try(new SidebarView(el,params)))
    .register("random",(el,params)=> Try (new RandomView(el,params))  )
    .register("lists",(el,params)=>Try (new LongListView(el,params)))
    .register("SlideView",(el,params)=>Try(new SlideView(el,params)))
    .register("BindSlide",(el,params)=>Try(new BindSlide(el,params)))
    .register("RemoteSlide",(el,params)=>Try(new RemoteSlide(el,params)))
    .register("RdfSlide",(el,params)=>Try(new RdfSlide(el,params)))
    .register("CodeView",(el,params)=>Try(new CodeView(el,params)))
    .register("TestModelView",(el,params)=>Try(new TestModelView(el,params)))
    .register("PicklerView",(el,params)=>Try(new PicklerView(el,params)))
    .register("PageEditView",(el,params)=>Try(new PageEditView(el,params)))
    .register("CodeInsideView",(el,params)=>Try(new CodeInsideView(el,params)))
    .register("TableView",  (el,params)=>Try(new TableBinder(el,params)))
    .register("ShapeEditor",(el,params)=>Try(new ShapeEditor(el,params)))
   // .register("ShapeProperty",(el,params)=>Try(new ShapeProperty(el,params)))
    .register("Tasks",(el,params)=>Try(new TasksBinder(el,params)))
    .register("task",(el,params)=>Try(new Task(el,params)))

    .register("SuggestView",(el,params)=>Try(new TestSuggestBinding(el,params)))
    .register("ReportsView",(el, params) =>Try(new ReportsView(el,params)))
    .register("report",(el, params) =>Try(new Report(el,params)))
    .register("TestSelect",(el, params) =>Try(new SelectView(el,params)))
    .register("CollectionSlide",(el, params) =>Try(new CollectionSlide(el,params)))
    .register("SparqlSlide", (el,params)=>Try(new SparqlSlide(el,params)))
    .register("GraphSlide", (el,params)=>Try(new GraphSlide(el,params)))
    .register("DatepairView",(el,params)=>Try(new DatePairView(el,params)))



  editors
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


def attachBinders() = {this.binders = BindableView.defaultBinders(this)}


  val toggle: Var[MouseEvent] = Var(EventBinding.createMouseEvent())

  toggle.handler{
    jQuery(".left.sidebar").dyn.sidebar(sidebarParams).sidebar("toggle")
  }
//  val onMenuOver: Var[MouseEvent] = Var(EventBinding.createMouseEvent())
//  val onMenuOut: Var[MouseEvent] = Var(EventBinding.createMouseEvent())


}
