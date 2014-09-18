package org.denigma.binding.frontend.papers

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.semantic.models.{AjaxModelCollection, RemoteModelView}
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom.HTMLElement
import rx._

/**
 * Tasks (about papers)
 * @param element
 * @param params
 */
class TasksView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends AjaxModelCollection("Tasks",element,params)
{

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  val addClick = Var(EventBinding.createMouseEvent())

  addClick handler {
    this.addItem()
  }

  val isDirty = Rx{  this.dirty().size>0  }

  override protected def attachBinders(): Unit = binders = BindableView.defaultBinders(this)
}

class Task(val elem:HTMLElement, val params:Map[String,Any]) extends RemoteModelView{

  override def name:String = "task"

  val initial: Option[Var[ModelInside]] = params.get("model").collect{case mi:Var[ModelInside]=>mi}

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}



  val removeClick = Var(EventBinding.createMouseEvent())

  removeClick.handler{
    this.die()
  }

  override protected def attachBinders(): Unit = binders = RemoteModelView.selectableBinders(this)

}