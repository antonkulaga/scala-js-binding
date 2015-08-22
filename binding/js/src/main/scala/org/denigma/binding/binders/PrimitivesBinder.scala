package org.denigma.binding.binders

import org.denigma.binding.binders.extractors.{ClassBinder, PropertyBinder, VisibilityBinder}
import org.scalajs.dom.raw._
import rx._

import scala.collection.immutable._


/**
 * Binds separate properties to HTML nodes
 */
trait PrimitivesBinder  extends BasicBinder with VisibilityBinder with ClassBinder with PropertyBinder{

  def bools:Map[String,Rx[Boolean]]
  def strings:Map[String,Rx[String]]


  def makeTextHandler(el:HTMLElement,par:Rx[String]):(KeyboardEvent)=>Unit = this.makeEventHandler(el,par){ (ev,v,elem)=>
    if(elem.textContent.toString!=v.now) {
      v()=elem.textContent.toString
    }
  }

  def bindProperties(el:HTMLElement,ats:Map[String, String]): Unit = for {
    (key, value) <- ats
  }{
    this.visibilityPartial(el,value)
      .orElse(this.classPartial(el,value))
      .orElse(this.propertyPartial(el,key.toString,value))
      .orElse(this.otherPartial)(key.toString)
  }

}

