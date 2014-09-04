package org.denigma.binding.frontend.papers

import org.denigma.binding.extensions._
import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.semantic.binding.ModelInside
import org.denigma.semantic.controls
import org.denigma.semantic.controls.AjaxModelCollection
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx._

import scalatags.Text._

/**
 * Tasks (about papers)
 * @param element
 * @param params
 */
class TasksBinder(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends AjaxModelCollection("Tasks",element,params)
{

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}




  val addClick = Var(EventBinding.createMouseEvent())

  addClick handler {
    this.addItem()
  }

  val isDirty = Rx{  this.dirty().size>0  }


}

class Task(val elem:HTMLElement, val params:Map[String,Any]) extends controls.SelectableModelView{

  override def name:String = "task"

  val initial: Option[Var[ModelInside]] = params.get("model").collect{case mi:Var[ModelInside]=>mi}

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}



  val removeClick = Var(EventBinding.createMouseEvent())

  removeClick.handler{
    this.die()
  }


}