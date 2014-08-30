package org.denigma.binding.frontend.slides

import org.denigma.binding.extensions._
import org.denigma.binding.semantic.ModelInside
import org.denigma.binding.views.OrdinaryView
import org.denigma.semantic.binding
import org.denigma.semantic.binding.ActiveModelView
import org.denigma.semantic.controls.AjaxModelCollection
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx._

import scalatags.Text.Tag

/**
 * Slide about RDF-related binding
 * @param elem html element to which view is attached
 * @param params
 */
class RdfSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView
{

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override def bindView(el:HTMLElement) {
    //jQuery(el).slideUp()
    super.bindView(el)

  }
}

class Todos(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends AjaxModelCollection("Todos",element,params)
{
  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override def bindView(el:HTMLElement) {
    //jQuery(el).slideUp()
    super.bindView(el)
  }


  val addClick = Var(this.createMouseEvent())

  addClick handler {
    this.addItem()
  }

  val isDirty = Rx{  this.dirty().size>0  }



}
class Todo(val elem:HTMLElement, val params:Map[String,Any], override val name:String = "todo") extends ActiveModelView{

  val initial: Option[Var[binding.ModelInside]] = params.get("model").collect{case mi:Var[binding.ModelInside]=>mi}

  require(initial.isDefined,"No model received!")

  override val modelInside  = initial.get

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  val removeClick = Var(this.createMouseEvent())

  removeClick.handler{
    this.die()
  }


}