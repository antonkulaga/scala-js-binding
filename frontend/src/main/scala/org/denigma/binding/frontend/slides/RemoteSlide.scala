package org.denigma.binding.frontend.slides

import org.scalajs.dom.{MouseEvent, HTMLElement}
import org.denigma.views.OrdinaryView
import rx._
import scalatags._

import org.denigma.views.OrdinaryView

class RemoteSlide(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("remote",element)
{
  override def tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override def bindView(el:HTMLElement) {
  //jQuery(el).slideUp()
  super.bindView(el)

  }



}
