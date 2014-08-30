package org.denigma.binding.frontend.papers

import org.denigma.binding.extensions._
import org.denigma.controls.semantic.SelectableModelView
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
class TasksView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends AjaxModelCollection("Tasks",element,params)
{

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)


  val addClick = Var(this.createMouseEvent())

  addClick handler {
    this.addItem()
  }

  val isDirty = Rx{  this.dirty().size>0  }


}

class Task(val elem:HTMLElement, val params:Map[String,Any]) extends controls.SelectableModelView{

  override def name:String = "task"

  val initial: Option[Var[ModelInside]] = params.get("model").collect{case mi:Var[ModelInside]=>mi}





  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  val removeClick = Var(this.createMouseEvent())

  removeClick.handler{
    this.die()
  }


}