package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.BasicBinder
import org.denigma.binding.extensions._
import org.scalajs.dom.raw.HTMLElement
import rx._
import rx.ops._
import scala.collection.immutable.Map

/**
 * Provides useful functions for visibility bindings (like showif/hideif)
 */
trait VisibilityBinder {
  self:BasicBinder=>

  def bools:Map[String,Rx[Boolean]]


  def visibilityPartial(el:HTMLElement):PartialFunction[(String,String),Unit] = {
    case ("showif" | "show-if" , rxName) => this.showIf(el, rxName, el.style.display)
    case ("hideif" | "hide-if", rxName) =>
      //println(s"hide is is strange ${el.outerHTML} and value is ${value}")
      this.hideIf(el, rxName, el.style.display)
  }

  def showIf(element:HTMLElement,rxName: String,disp:String) =  for ( b<-bools.getOrError(rxName) ){
    withID(element,"showif_"+rxName)
    b.foreach(sh=>element.style.display = if(sh) disp else "none")
  }

  def hideIf(element:HTMLElement,rxName: String,disp:String) =  for ( b<-bools.getOrError(rxName) ){
    withID(element,"hideif_"+rxName)
    b.foreach(h=>element.style.display = if(h) "none" else disp)
  }


}
