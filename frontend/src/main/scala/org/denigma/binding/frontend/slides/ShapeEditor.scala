package org.denigma.binding.frontend.slides

import org.denigma.binding.extensions._
import org.denigma.binding.semantic.{ActiveModelView, ModelInside}
import org.denigma.controls.semantic.AjaxModelCollection
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx.Var
import rx.core.Rx

import scalatags.Text.Tag

class ShapeEditor (elem:HTMLElement,params:Map[String,Any]) extends AjaxModelCollection("ShapeEditor",elem,params)
{


  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def mouseEvents: Predef.Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)


  val addClick = Var(this.createMouseEvent())

  addClick handler {
    //this.addItem()
  }

}

class ShapeProperty(val elem:HTMLElement, val params:Map[String,Any]) extends ActiveModelView{



  val initial: Option[Var[ModelInside]] = params.get("model").collect{case mi:Var[ModelInside]=>mi}

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