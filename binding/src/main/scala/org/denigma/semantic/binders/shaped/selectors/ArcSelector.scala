package org.denigma.semantic.binders.shaped.selectors

import org.denigma.binding.extensions._
import org.denigma.semantic.binders.selectors.Selector
import org.scalajs.jquery._
import org.scalax.semweb.shex.ArcRule
import rx.core.Var

import scala.scalajs.js


abstract class ArcSelector(val arc:Var[ArcRule]) extends Selector{

   def fillValues(arc:Var[ArcRule]):this.type

   type Value

   type Element //which element is changed by selector

   val sel: js.Dynamic = jQuery(el).dyn.selectize(selectParams(el))

   /**
    * transformts
    * @param value
    */
   def valueIntoElement(value:String):Element //transform value into an element

   def elementIntoValue(element:Element):Value

 }
