package org.denigma.semantic.binders.shaped

import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.selectors.OccursSelector
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex.ArcRule
import rx.core.Var

import scala.collection.immutable.Map


class OccursBinder(view:BindableView, arc:Var[ArcRule]) extends ArcBinder(view,arc){
  var occurs = Map.empty[HTMLElement,OccursSelector]

  override protected def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] = {
    this.vocabPartial(value).orElse(this.arcPartial(el:HTMLElement,value.toLowerCase))
  }




  protected def arcPartial(el: HTMLElement,value:String): PartialFunction[String,Unit] =
  {
    case "data" if value=="occurs" =>

      this.bindVar("occurs", el: HTMLElement, this.arc) { (e,arc)=>
        val sel = this.occurs.getOrElse(el, {
          val s = new OccursSelector(el,arc)
          occurs = occurs + (el -> s)
          s
        })
        sel.fillValues(arc)

      }
  }



}
