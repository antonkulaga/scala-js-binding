package org.denigma.binding.frontend

import org.denigma.binding.extensions._
import org.denigma.binding.frontend.controls.{EditShapeView, ShapeProperty, ShapeEditor}
import org.denigma.binding.frontend.slides.RowView
import org.denigma.binding.views.BindableView
import org.denigma.binding.views.utils.ViewInjector
import org.denigma.semantic.rdf.ShapeInside
import org.denigma.semantic.shapes.ShapeView
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import rx.core.Var

import scala.collection.immutable.Map
import scala.scalajs.js.annotation.JSExport
import scala.util.Try
import org.denigma.binding.frontend.papers.{Task, TasksView}

class TestShapeEditor(el:HTMLElement,params:Map[String,Any]) extends ShapeEditor(el,params){
  override def newItem(item:Item):ItemView = this.constructItem(item,Map("shape"->item)){
    (el,mp)=>  new EditShapeTestView(el,mp)
  }
}

class EditShapeTestView(el:HTMLElement,params:Map[String,Any]) extends EditShapeView(el,params){

  override protected def onSaveClick(): Unit= {
    val str = this.shapeString
    val sid = shape.now.id.asResource.stringValue
    val id = sid
    saveAs(sid.substring(sid.indexOf(":")+2),str)
  }


}

@JSExport("FrontEndSpec")
object FrontEndSpec extends BindableView with scalajs.js.JSApp{

  override def name = "main"

  lazy val elem:HTMLElement = dom.document.body

  override val params:Map[String,Any] = Map.empty


  /**
   * Register views
   */
  ViewInjector
    .register("test-general", (el, params) =>Try(new TestGeneral(el,params)))

  ViewInjector
    .register("Tasks",(el,params)=>Try(new TasksView(el,params)))
    .register("row",(el,params)=>Try(new RowView(el,params)))
    .register("task",(el,params)=>Try(new Task(el,params)))

  ViewInjector //register shapes
    .register("ShapeEditor",(el,params)=>Try(new ShapeEditor(el,params)))
    .register("EditableShape",(el,params)=>Try(new EditShapeView(el,params)))
    .register("ShapeProperty",(el,params)=>Try(new ShapeProperty(el,params)))
    .register("ValueClass",(el,params)=>Try(new ShapeProperty(el,params)))





  @JSExport
  def main(): Unit = {
    this.bindView(this.viewElement)
  }

  override def activateMacro(): Unit = {
    extractors.foreach(_.extractEverything(this))
  }

  def attachBinders() = {this.binders = BindableView.defaultBinders(this)}

}
