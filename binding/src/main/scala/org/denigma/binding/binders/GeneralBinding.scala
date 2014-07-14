package org.denigma.binding.binders

import org.denigma.binding.macroses.{BooleanRxMap, ClassToMap, StringRxMap}
import org.scalajs.dom._
import rx._

import scala.collection.immutable._


/**
 * Binds separate properties to HTML nodes
 */
trait GeneralBinding  extends JustBinding with VisibilityBinder with ClassBinder with PropertyBinder{

  def bools:Map[String,Rx[Boolean]]
  def strings:Map[String,Rx[String]]
  //def doubles:Map[String,Rx[Double]]


  //  def extractBooleans[T]:Map[String,Rx[Boolean]] = macro Binder.booleanBindings_impl[T]
  //  def extractStrings[T]:Map[String,Rx[String]] = macro Binder.stringBindings_impl[T]
  //  def extractDoubles[T]:Map[String,Rx[Double]] = macro Binder.doubleBindings_impl[T]


  def extractAll[T: ClassToMap](t: T): Map[String, Any] =  implicitly[ClassToMap[T]].asMap(t)
  def extractStringRx[T: StringRxMap](t: T): Map[String, Rx[String]] =  implicitly[StringRxMap[T]].asStringRxMap(t)
  def extractBooleanRx[T: BooleanRxMap](t: T): Map[String, Rx[Boolean]] =  implicitly[BooleanRxMap[T]].asBooleanRxMap(t)



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
      .orElse(this.loadIntoPartial(el,value))
      .orElse(this.otherPartial)(key.toString)
  }


  /**
   * Loads element into another one
   * @param el
   * @param value
   * @return
   */
  protected def loadIntoPartial(el:HTMLElement,value:String):PartialFunction[String,Unit] = {
    case "load-into" =>
      bindLoadInto(el,value, rel = true)
    case "load-abs-into" => bindLoadInto(el,value, rel = false)
  }

  protected def otherPartial:PartialFunction[String,Unit] = {case _=>}


  /**
   * Loads links into some view
   * @param element
   * @param into
   */
  def bindLoadInto(element:HTMLElement,into: String, rel:Boolean) =   element.onclick = this.makeGoToHandler(element,into,push = true, rel)




}

