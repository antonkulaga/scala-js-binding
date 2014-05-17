package org.denigma.binding.frontend

import org.scalajs.dom.HTMLElement
import org.scalajs.dom.{MouseEvent, HTMLElement}
import org.denigma.views.OrdinaryView
import rx._
import scalatags._
import org.scalajs.dom.extensions.Ajax
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.denigma.extensions.sq
import scala.scalajs.js

/**
 * View for the sitebar
 */
class SidebarView (element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("sidebar",element){
  override def tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)

  val logo = Var("")

  Ajax.get(sq.withHost("/logo/sidebar")).foreach{res=>
    logo()=sq.withHost(res.responseText)
    js.eval("$('.ui.accordion').accordion();")

  }


}
