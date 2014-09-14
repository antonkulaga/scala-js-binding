package org.denigma.semantic.binders.shaped

import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.RDFBinder
import org.denigma.semantic.rdf.PropertyPrinter
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex.ArcRule
import rx.core.Var

import scala.collection.immutable.Map


abstract class ArcBinder(val view:BindableView, val arc:Var[ArcRule]) extends RDFBinder(view)  with PropertyPrinter{

  override protected def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] = {
    this.vocabPartial(value).orElse(this.arcPartial(el:HTMLElement,value.toLowerCase))
  }
  protected def arcPartial(el: HTMLElement,value:String): PartialFunction[String,Unit]

}

