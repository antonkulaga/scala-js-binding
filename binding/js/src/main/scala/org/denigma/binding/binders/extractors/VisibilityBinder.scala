package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.ReactiveBinder
import org.denigma.binding.extensions._
import org.scalajs.dom.raw.{Element, HTMLElement}
import rx._
import scala.collection.immutable.Map
import rx.Ctx.Owner.Unsafe.Unsafe

/**
 * Provides useful functions for visibility bindings (like showif/hideif)
 */
trait VisibilityBinder {
  self:ReactiveBinder=>

  def bools:Map[String,Rx[Boolean]]


  def visibilityPartial(el:Element):PartialFunction[(String,String),Unit] = {
    case ("showif" | "show-if" , rxName) => this.showIf(el, rxName, el.style.display)
    case ("hideif" | "hide-if", rxName) =>
      //println(s"hide is is strange ${el.outerHTML} and value is ${value}")
      this.hideIf(el, rxName, el.style.display)
  }

  def showIf(element: Element,rxName: String, disp: String) =  for ( b<-bools.getOrError(rxName, element.outerHTML) ){
    //ifNoID(element,"showif_"+rxName)
    b.foreach(sh=>element.style.display = if(sh) disp else "none")
  }

  def hideIf(element: Element, rxName: String, disp: String) =  for ( b<-bools.getOrError(rxName, element.outerHTML) ){
    //ifNoID(element,"hideif_"+rxName)
    b.foreach(h=>element.style.display = if(h) "none" else disp)
  }


}
