package org.denigma.binding.frontend

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.extensions._
import org.denigma.binding.frontend.controls.{ShapeEditor, EditableShape, EditableShape$, ShapeProperty}
import org.denigma.binding.frontend.datagrids.{DataGrid, GridCell, GridRow}
import org.denigma.binding.frontend.papers.{Report, ReportsView, Task, TasksView}
import org.denigma.binding.frontend.slides._
import org.denigma.binding.frontend.tests.{LongListView, PicklerView, RandomView, TestMacroView}
import org.denigma.binding.frontend.tools.SelectView
import org.denigma.binding.views.BindableView
import org.denigma.binding.views.utils.ViewInjector
import org.denigma.controls.editors.{CkEditor, CodeMirrorEditor, editors}
import org.denigma.controls.general.DatePairView
import org.denigma.semantic.shapes.HeadersView
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, MouseEvent}
import org.scalajs.jquery._
import rx._

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.Try


@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  override def name = "main"

  lazy val elem:HTMLElement = dom.document.body

  override val params:Map[String,Any] = Map.empty



  val sidebarParams =  js.Dynamic.literal(
    exclusive = false,
    dimPage = false,
    closable =  false,
    useLegacy = false

  )
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
    .register("TestModelView",(el,params)=>Try(new TestModelView(el,params)))
    .register("PicklerView",(el,params)=>Try(new PicklerView(el,params)))
    .register("PageEditView",(el,params)=>Try(new PageEditView(el,params)))



    .register("Tasks",(el,params)=>Try(new TasksView(el,params)))
    .register("row",(el,params)=>Try(new RowView(el,params)))

    .register("task",(el,params)=>Try(new Task(el,params)))

    .register("SuggestView",(el,params)=>Try(new TestSuggestBinding(el,params)))
    .register("ReportsView",(el, params) =>Try(new ReportsView(el,params)))
    .register("report",(el, params) =>Try(new Report(el,params)))


    .register("CollectionSlide",(el, params) =>Try(new CollectionSlide(el,params)))
    .register("SparqlSlide", (el,params)=>Try(new SparqlSlide(el,params)))
    .register("DatepairView",(el,params)=>Try(new DatePairView(el,params)))
    .register("GlobeSlide", (el,params)=>Try(new GlobeSlide(el,params)))
    .register("test-macro", (el,params)=>Try(new TestMacroView(el,params)))

  ViewInjector //register shapes
    .register("ShapeEditor",(el,params)=>Try(new ShapeEditor(el,params)))
    .register("EditableShape",(el,params)=>Try(new EditableShape(el,params)))
    .register("ShapeProperty",(el,params)=>Try(new ShapeProperty(el,params)))

  ViewInjector //register datagrids
    .register("Headers",(el,params,parent)=>Try(new HeadersView(el,params,parent)))
    .register("DataGrid",(el,params) => Try(new DataGrid(el,params)))
    .register("GridRow",(el,params) => Try(new GridRow(el,params)))
    .register("GridCell",(el,params) => Try(new GridCell(el,params)))

  editors
    .registerEditor("ckeditor",CkEditor, true)
    .registerEditor("codemirror",CodeMirrorEditor)




  //    .register("righ-menu", (el, params) =>Try(new RightMenuView(el,params)))

  @JSExport
  def main(): Unit = {
    this.bindView(this.viewElement)
  }

  @JSExport
  def showLeftSidebar() = {
    jQuery(".left.sidebar").dyn.sidebar(sidebarParams).sidebar("show")
  }

  @JSExport
  def load(content:String,into:String): Unit = {
    dom.document.getElementById(into).innerHTML = content
  }

  @JSExport
  def moveInto(from:String,into:String): Unit = {
    for{
      ins <- sq.byId(from)
      intoElement <-sq.byId(into)
    }
    {
      this.loadElementInto(intoElement,ins.innerHTML)
      ins.parentNode.removeChild(ins)
    }

  }


  override def activateMacro(): Unit = {
    extractors.foreach(_.extractEverything(this))
  }

  def attachBinders() = {this.binders = BindableView.defaultBinders(this)}


  val toggle: Var[MouseEvent] = Var(EventBinding.createMouseEvent())

  toggle.handler{
    jQuery(".left.sidebar").dyn.sidebar(sidebarParams).sidebar("toggle")
  }
//  val onMenuOver: Var[MouseEvent] = Var(EventBinding.createMouseEvent())
//  val onMenuOut: Var[MouseEvent] = Var(EventBinding.createMouseEvent())


}
