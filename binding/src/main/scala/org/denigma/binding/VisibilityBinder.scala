package org.denigma.binding

import org.denigma.views.BindingView
import scala.collection.immutable.Map
import rx._
import org.scalajs.dom.HTMLElement
import org.denigma.extensions._
import org.scalajs.dom

/**
 * Provides useful functions for visibility bindings (like showif/hideif)
 */
trait VisibilityBinder {
  self:JustBinding=>

  def bools:Map[String,Rx[Boolean]]

  def visibilityPartial(el:HTMLElement,value:String):PartialFunction[String,Unit] = {
    case "showif" => this.showIf(el, value, el.style.display)
    case "hideif" => this.hideIf(el, value, el.style.display)
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
