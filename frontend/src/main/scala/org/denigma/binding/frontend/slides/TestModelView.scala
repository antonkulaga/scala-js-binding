package org.denigma.binding.frontend.slides

import org.denigma.binding.extensions._
import org.denigma.controls.semantic.AjaxLoadView
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, MouseEvent, TextEvent}
import rx._
import rx.core.Var

import scala.collection.immutable.Map
import scalatags.Text.Tag


class TestModelView(val elem:HTMLElement,val params:Map[String,Any]) extends AjaxLoadView
{


  val saveClick: Var[MouseEvent] = Var(this.createMouseEvent())

  this.saveClick.takeIf(dirty).handler{
    //dom.console.log("it should be saved right now")
    this.saveModel()
  }


  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  lazy val textEvents: Map[String, rx.Var[TextEvent]] = this.extractTextEvents(this)

  lazy val mouseEvents: Map[String, rx.Var[dom.MouseEvent]] = this.extractMouseEvents(this)

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)
}
