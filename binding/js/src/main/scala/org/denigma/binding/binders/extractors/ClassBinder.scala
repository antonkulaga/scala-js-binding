package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.BasicBinder
import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import rx._
import rx.ops._
import scala.collection.immutable.Map


/**
 * Does binding for classes
 */
trait ClassBinder {
  self:BasicBinder=>
  def strings:Map[String,Rx[String]]
  def bools:Map[String,Rx[Boolean]]


  protected def classIf(el:HTMLElement,className: String, cond:String) = for ( b<-bools.getOrError(cond) )
  {
    withID(el,s"class-$className-If_$cond")
    b.foreach{
      case false=>if(el.classList.contains(className)) el.classList.remove(className)
      case true=>if(!el.classList.contains(className)) el.classList.add(className)
    }
  }

  protected def classUnless(el:HTMLElement,className: String, cond:String) = for ( b<-bools.getOrError(cond) )
  {
    withID(el,s"class-$className-Unless_$cond")
    b.foreach{
      case true=>if(el.classList.contains(className)) el.classList.remove(className)
      case false=>if(!el.classList.contains(className)) el.classList.add(className)
    }
  }

  /**
   * Partial function for class
   * @param el Html element we bind to
   * @return
   */
  protected def classPartial(el:HTMLElement):PartialFunction[(String,String),Unit] = {
    case ("class" | "bind-class" ,value) => this.bindClass(el,value)
    case (str,value) if str.startsWith("class-")=>
      str.replace("class-","") match {
        case cl if cl.endsWith("-if")=>
          this.classIf(el,cl.replace("-if","").replace("bind-",""),value)
        case cl if cl.endsWith("-unless")=>
          this.classUnless(el,cl.replace("-unless","").replace("bind-",""),value)
        case _ =>
          dom.console.error(s"other class bindings are not implemented yet for $str")
      }
  }

  def bindClass(el:HTMLElement,rxName: String) = for ( str<-strings.getOrError(rxName) ) {
    str.zip.foreach{
      case (oldVal,newVal)=>
        for(o<-oldVal.split(" ") if el.classList.contains(o)) el.classList.remove(o)
        for(n<-newVal.split(" ") if !el.classList.contains(n))  el.classList.add(n)
    }
  }
}