package org.denigma.semantic.binders.shex

import org.denigma.binding.views.BindableView
import org.denigma.selectize.Selectize
import org.denigma.selectors.Selector
import org.denigma.semantic.binders.{SemanticSelector, RDFBinder}
import org.denigma.semantic.rdf.PropertyPrinter
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.scalax.semweb.shex.{NameTerm, ArcRule}
import rx.core.Var

import scala.collection.immutable.Map

/**
 * Parent class for all binders that bind parts of ArcRule
 * @param view
 * @param arc
 */
class ArcBinder(val view:BindableView, val arc:Var[ArcRule]) extends RDFBinder(view)  with PropertyPrinter{

  override protected def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] = {
    this.vocabPartial(value).orElse(this.arcPartial(el:HTMLElement,value.toLowerCase))
  }
  protected def arcPartial(el: HTMLElement,value:String): PartialFunction[String,Unit] ={

    case "data" if value=="name"=> arc.now.title match {
      case Some(tlt)=> this.setTitle(el,tlt)
      case None=> arc.now.name match {
        case NameTerm(name)=> this.setTitle(el,name.label)
        case _=>
      }
    }
  }

}

import org.denigma.binding.extensions._
import org.scalajs.jquery._
import org.scalax.semweb.shex.ArcRule
import rx.core.Var

import scala.scalajs.js


/**
 * Parent Selector for all selectors that bind
 * @param arc
 */
abstract class ArcSelector(val arc:Var[ArcRule]) extends SemanticSelector{

  def fillValues(arc:Var[ArcRule]):this.type

  type Value

  type Element //which element is changed by selector

   /**
   * transformts
   * @param value
   */
  def valueIntoElement(value:String):Element //transform value into an element

  def elementIntoValue(element:Element):Value

}
