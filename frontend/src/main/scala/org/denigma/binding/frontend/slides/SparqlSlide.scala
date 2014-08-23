package org.denigma.binding.frontend.slides

import org.denigma.binding.views.OrdinaryView
import org.scalajs.dom.{HTMLElement, MouseEvent}
import org.scalax.semweb.parsers.DateParser
import rx._

import scalatags.Text._

/**
 * Slide about RDF-related binding
 * @param elem html element to which view is attached
 * @param params
 */
class SparqlSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView
{

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override def bindView(el:HTMLElement) {
    //jQuery(el).slideUp()
    super.bindView(el)

  }

  val input = Var("01.01.2010")
  val tree = Rx {
   // new Calculator(input()).InputLine.run().map(i=>i.toString).getOrElse("failure") // evaluates to `scala.util.Success(2)`
    new DateParser(input()).InputLine.run().map(i=>i.toString).getOrElse("failure")
  }



}



