package org.denigma.binding.frontend

import org.denigma.binding.extensions.sq
import org.denigma.binding.views.OrdinaryView
import org.scalajs.dom.extensions.Ajax
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scalatags.Text.Tag

/**
 * View for the sitebar
 */
class SidebarView (val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView{

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  val logo = Var("")

  Ajax.get(sq.withHost("/logo/sidebar")).foreach{res=>
    logo()=sq.withHost(res.responseText)
    js.eval("$('.ui.accordion').accordion();")

  }


}
