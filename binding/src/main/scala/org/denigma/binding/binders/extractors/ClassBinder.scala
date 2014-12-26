package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.BasicBinding
import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import rx._

import scala.collection.immutable.Map


/**
 * Does binding for classes
 */
trait ClassBinder {
  self:BasicBinding=>
  def strings:Map[String,Rx[String]]
  def bools:Map[String,Rx[Boolean]]

  /**
   * Shows only if condition is true
   * @param element html element
   * @param className name of the class that will be added if conditional is true
   * @param cond conditional rx
   */
  protected def classIf(element:HTMLElement,className: String, cond:String) = for ( b<-bools.getOrError(cond) ) this.bindRx(className,element,b){
    case (el,cl) if el.classList.contains(className)=>
      if(!cl) el.classList.remove(className)
    case (el,cl) =>
      if(cl) el.classList.add(className)
  }

  protected def classUnless(element:HTMLElement,className: String, cond:String) = for ( b<-bools.getOrError(cond) ) this.bindRx(className,element,b){
    case (el,cl) if el.classList.contains(className)=>if(cl) el.classList.remove(className)
    case (el,cl) =>if(!cl) el.classList.add(className)
  }


  /**
   * Partial function for class
   * @param el
   * @param value
   * @return
   */
  protected def classPartial(el:HTMLElement,value:String):PartialFunction[String,Unit] = {
    case "class" => this.bindClass(el,value)
    case str if str.startsWith("class-")=> str.replace("class-","") match {
      case cl if cl.endsWith("-if")=>
        this.classIf(el,cl.replace("-if",""),value)
      case cl if cl.endsWith("-unless")=>
        this.classUnless(el,cl.replace("-unless",""),value)
      case _ =>
        dom.console.error(s"other class bindings are not implemented yet for $str")
    }
  }

  def bindClass(element:HTMLElement,prop: String) = for ( str<-strings.get(prop) ) this.bindRx(prop,element,str.zip){
    case (el,(oldVal,newVal)) =>
      if(el.classList.contains(oldVal))el.classList.remove(oldVal)
      el.classList.add(newVal)
    case _ => dom.console.error(s"error in bindclass for ${prop}")
  }
}
