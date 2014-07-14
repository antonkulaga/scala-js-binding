package org.denigma.binding.frontend.slides

import org.denigma.binding.views.OrdinaryView
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx._

import scalatags.Text.Tag


class RemoteSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView
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
