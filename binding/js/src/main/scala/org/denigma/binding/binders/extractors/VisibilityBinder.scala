package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.BasicBinder
import org.denigma.binding.extensions._
import org.scalajs.dom.raw.HTMLElement
import rx._

import scala.collection.immutable.Map

/**
 * Provides useful functions for visibility bindings (like showif/hideif)
 */
trait VisibilityBinder {
  self:BasicBinder=>

  def bools:Map[String,Rx[Boolean]]

  def visibilityPartial(el:HTMLElement,value:String):PartialFunction[String,Unit] = {
    case "showif" | "show-if" => this.showIf(el, value, el.style.display)
    case "hideif" | "hide-if" => this.hideIf(el, value, el.style.display)
  }


  /**
   * Shows element if condition is satisfied
   * @param element Element that should be shown
   * @param show
   * @param disp
   */
  def showIf(element:HTMLElement,show: String,disp:String) =  for ( b<-bools.getOrError(show) ) this.bindRx("showIf",element,b){
    case (el,sh)=>
      el.style.display = if(sh) disp else "none"
    //el.style.visibility = if(sh) "visible" else "hidden"
  }

  def hideIf(element:HTMLElement,hide: String,disp:String) = for ( b<-bools.getOrError(hide) ) this.bindRx("showIf",element,b){
    case (el,h)=>
      el.style.display = if(h) "none" else disp
    //el.style.visibility = if(h) "hidden" else "visible"
  }




}
