package org.denigma.binding.frontend.slides

import org.scalajs.dom.{MouseEvent, HTMLElement}
import rx._
import scalatags._
import org.denigma.views.core.OrdinaryView
import scalatags.Text.Tag

/**
 * Slide about RDF-related binding
 * @param element
 * @param params
 */
class RdfSlide(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("rdf",element)
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
