package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.{Events, ReactiveBinder}
import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.{HTMLTextAreaElement, HTMLInputElement, HTMLElement}
import org.scalajs.dom.{Event, KeyboardEvent}
import rx._
import rx.ops._
import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.Any

/**
 * Does binding for classes
 */
trait PropertyBinder {
  self:ReactiveBinder=>
  import js.Any._

  implicit def any2String(value:js.Any):String = value.toString
  implicit def any2Double(value:js.Any):Double = value.toString.toDouble
  implicit def any2Int(value:js.Any):Int = value.toString.toInt
  implicit def any2Bool(value:js.Any):Boolean = value.toString.toLowerCase == "true"


  def strings:Map[String,Rx[String]]
  def bools:Map[String,Rx[Boolean]]
  def doubles:Map[String,Rx[Double]]
  def ints:Map[String,Rx[Int]]

  def allValues =strings ++ bools ++ doubles ++ ints

  /**
   * Partial function that is usually added to bindProperties
   * @param el html element
   * @return
   */
  protected def propertyPartial(el:HTMLElement):PartialFunction[(String,String),Unit] = {
    case (str,rxName) if str.contains("style-") => this.bindStyle(el,rxName,str.replace("bind-","").replace("style-",""))
    case (bname,rxName) if bname.startsWith("bind-")=>this.bindProperty(el,rxName,bname.replace("bind-",""))
    case ("html",rxName) =>
      strings.get(rxName) match {
        case Some(str)=>
          val prop = "innerHTML"
          str.foreach(s=>el.dyn.updateDynamic(prop)(s))
          str.onVar { case v =>
            el.addEventListener(Events.change,(ev: Event) => {
             if(ev.target==ev.currentTarget) el.onExists(prop)(value => v.set(value.toString)) }
          )
            v.set(el.dyn.selectDynamic(prop).toString)
          }
        case None => dom.console.error(s"cannot find $rxName for innerHtml")
      }
    case ("bind",rxName) => bind(el,rxName)
  }

  protected def bindStyle(el:HTMLElement,rxName:String,prop:String): Unit= {
    if(strings.contains(rxName))
      stylePropertyOnRx(el,prop,strings(rxName))
    else
    if(doubles.contains(rxName))
      stylePropertyOnRx(el,prop,doubles(rxName))
    else
    if(ints.contains(rxName))
      stylePropertyOnRx(el,prop,ints(rxName))
    else
    if(bools.contains(rxName))
      stylePropertyOnRx(el,prop,bools(rxName))
    else
      this.cannotFind(rxName,prop,allValues)
  }


  /**
   * Bind property
   * @param el html element
   * @param rxName value of attribute
   * @return
   */
  def bind(el:HTMLElement,rxName:String): Unit =  el match
  {
    case inp:HTMLInputElement=>
      el.attributes.get("type").map(_.value.toString) match {
        case Some("checkbox") =>
          //ifNoID(el, att + "_checkbox")
          for (b <- bools.get(rxName)) b.foreach(v => el.attributes.setNamedItem(("checked" -> v.toString).toAtt))
        case tp =>
          //ifNoID(el, att)
          //subscribeProperty[KeyboardEvent](el,att,"value",Events.keyup)
          subscribeInputValue(el,rxName,Events.keyup,strings)
      }
    case area:HTMLTextAreaElement =>
      //ifNoID(el, att+"_textarea")
      subscribeInputValue(el,rxName,Events.keyup,strings)
        .orElse(subscribeInputValue(el,rxName,Events.keyup,doubles))
        .orElse(subscribeInputValue(el,rxName,Events.keyup,ints))
        .orElse(subscribeInputValue(el,rxName,Events.keyup,bools)).orError(s"cannot find ${rxName} in ${allValues}")

    //subscribeProperty[KeyboardEvent](el,att,"value",Events.keyup)

    case other=>
      strings.get(rxName) match {
        case Some(value)=>
          val prop = "innerHTML"
          propertyOnRx(el,prop,value)
          varOnEvent[String,Event](el,prop,value,Events.change)
        case None => dom.console.error(s"cannot find $rxName for innerHtml")
      }
      //subscribeProperty[Event](el,att,"textContent",Events.change)

  }

  protected def cannotFind[T](rxName:String,prop:String,mp:Map[String,Rx[T]]) =
    dom.console.error(s"cannot find $rxName reactive variable for prop $prop\n, all values are: \n"+
      mp.mapValues(_.name).mkString(" | ")
    )

  /*protected def cannotFindStr(rxName:String,prop:String) =
    dom.console.error(s"cannot find $rxName string reactive variable for prop $prop\n, all strings are: \n"+
    strings.mapValues(_.name).mkString(" | ")
  )*/

  protected def subscribeInputValue[T](el:HTMLElement,rxName: String,event:String,mp:Map[String,Rx[T]])
                                      (implicit js2var:js.Any=>T,var2js:T=>js.Any): Option[Rx[T]] =
    mp.get(rxName) map {
      case value=>
          val prop = "value"
          el match {
            case inp:HTMLInputElement=>
              value.foreach{
                s=>
                  val (start,end) =(inp.selectionStart,inp.selectionEnd)
                  inp.value = s.toString
                  inp.selectionStart = start
                  inp.selectionEnd = end
              }

            case area:HTMLTextAreaElement=>
              value.foreach{
                s=>
                  val (start,end) =(area.selectionStart,area.selectionEnd)
                  area.value = s.toString
                  area.selectionStart = start
                  area.selectionEnd = end
              }

            case other=> propertyOnRx(el,prop,value)
          }
          varOnEvent[T,KeyboardEvent](el,prop,value,event)(js2var,var2js)
          //propertyOnRx(el,prop,value)
          value
  }


  /**
   * subscribes property to Rx, if Rx is Var then changes Var when specified event fires
   */
  protected def varOnEvent[T,TEvent<:dom.Event](el:HTMLElement,prop:String,value:Rx[T], event:String)
                                                      (implicit js2var:js.Any=>T,var2js:T=>js.Any): Unit =
  {
    value.onVar { case v =>
      el.addEventListener[TEvent](event,(ev: TEvent) => {
             if(ev.target==ev.currentTarget) el.onExists(prop)(value => v.set(js2var(value))) }
          )
      v.set(var2js(el.dyn.selectDynamic(prop)))
    }
  }



  /**
   * subscribes property to changes of Rx
   */
  protected def propertyOnRx[T](el:HTMLElement,prop:String,value:Rx[T])(implicit conv:T=>js.Any): Unit =
  {
    value.foreach{  case v=>
      el.attributes.setNamedItem((prop -> v.toString).toAtt)
      el.dyn.updateDynamic(prop)(v)
    }
  }

  protected def stylePropertyOnRx[T](el:HTMLElement,prop:String,value:Rx[T])(implicit conv:T=>js.Any): Unit =
  {
    value.foreach{  case v=>   el.style.dyn.updateDynamic(prop)(v)  }
  }

  //TODO: fix this ugly piece of code
  protected def bindProperty(el:HTMLElement,rxName:String,prop:String) = {
    if(strings.contains(rxName)){
      propertyOnRx(el,prop,strings(rxName))(js.Any.fromString)
    }
    else if(doubles.contains(rxName)){
      propertyOnRx(el,prop,doubles(rxName))(js.Any.fromDouble)
    }
    else if(ints.contains(rxName)){
      propertyOnRx(el,prop,ints(rxName))(js.Any.fromInt)
    }
    else if(bools.contains(rxName)){
      propertyOnRx(el,prop,bools(rxName))(js.Any.fromBoolean)
    } else cannotFind(rxName,prop,allValues)
  }

}