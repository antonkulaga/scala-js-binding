package org.denigma.binding.frontend.slides

import org.denigma.binding.views.OrdinaryView
import org.scalajs.dom.{MouseEvent, HTMLElement}
import rx._
import scalatags._
import scalatags.Text.Tag


class RemoteSlide(val elem:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView
{

  val name = "remote"


  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override def bindView(el:HTMLElement) {
  //jQuery(el).slideUp()
  super.bindView(el)

  }



}
