package org.denigma.binding.binders

import org.scalajs.dom
import org.scalajs.dom.extensions._
import org.scalajs.dom.{Event, HTMLElement, KeyboardEvent}
import rx._
import org.denigma.binding.extensions._

import scala.collection.immutable.Map

/**
 * Does binding for classes
 */
trait PropertyBinder {
  self:JustBinding=>
  def strings:Map[String,Rx[String]]
  def bools:Map[String,Rx[Boolean]]


  /**
   * Partial function that is usually added to bindProperties
   * @param el
   * @param key
   * @param value
   * @return
   */
  protected def propertyPartial(el:HTMLElement,key:String,value:String):PartialFunction[String,Unit] = {
    case bname if bname.startsWith("bind-")=>this.bindAttribute(el,key.replace("bind-",""),value,this.strings)
    case "bind" => this.bindProperty(el,key,value)
    case "html" => this.bindInnerHTML(el,key,value)
  }


  /**
   * Bind property
   * @param el
   * @param key
   * @param att
   * @param binder  binding view which collection we search, this by default
   * @return
   */
  def bindProperty(el:HTMLElement,key:String,att:String)(implicit binder:PropertyBinder = this): Boolean = (key.toString,el.tagName.toLowerCase().toString) match
  {
    case ("bind","input")=>
      el.attributes.get("type").map(_.value.toString) match {
        case Some("checkbox") =>binder.bools.get(att) match {
          case Some(b)=>  binder.bindCheckBox(el,key,b)
            true
          case None => false
        }

        case _ => binder.strings.get(att) match {
          case Some(str)=>
            el.onkeyup =binder.makePropHandler[KeyboardEvent](el,str,"value")
            binder.bindInput(el,key,str)
            true
          case None=>false
        }

      }
    case ("bind","textarea")=>
      binder.strings.get(att) match {
        case Some(str)=>
          el.onkeyup = binder.makePropHandler(el,str,"value")
          this.bindText(el,key,str)
          true

        case None=>false
      }
    case ("bind",other)=> binder.strings.get(att) match {
      case Some(str) =>
        el.onkeyup = binder.makePropHandler(el,str,"value")
        binder.bindText(el,key,str)
        true
      case None=>false
    }

    case _=>
      false
      //dom.console.error(s"unknown binding for $key with attribute ${att}")

  }


  protected def bindCheckBox(el:HTMLElement,key:String,rx:Rx[Boolean]) = this.bindRx(key,el:HTMLElement,rx){ (el,value)=>
    el.attributes.setNamedItem( ("checked" -> value.toString ).toAtt )
  }

  protected def bindInput(el:HTMLElement,key:String,str:Rx[String]): Unit = this.bindRx(key,el:HTMLElement,str){ (el,value)=>
    if(el.dyn.value!=value) el.dyn.value=value
  }

  protected def bindText(el:HTMLElement,key:String,str:Rx[String]) = this.bindRx(key,el:HTMLElement,str){ (el,value)=>
    el.textContent = value
  }


  /**
   * Creates a handler that changes rx value according to element property
   * @param el
   * @param par
   * @param pname property name
   * @tparam T
   * @return
   */
  def makePropHandler[T<:Event](el:HTMLElement,par:Rx[String],pname:String):(T)=>Unit = this.makeEventHandler[T,String](el,par){ (ev,v,elem)=>
    elem \ pname  match {
      case Some(pvalue)=>
        if(v.now!=pvalue.toString) {
          v()=pvalue.toString
        }

      case None => dom.console.error(s"no attributed for $pname")
    }
  }


  /**
   * Creates attribute handler for the event
   * @param el html element
   * @param par parameter
   * @param atname attribute name
   * @tparam T type of event
   * @return
   */
  def makeAttHandler[T<:Event](el:HTMLElement,par:Rx[String],atname:String):(T)=>Unit = this.makeEventHandler[T,String](el,par){ (ev,v,elem)=>
    elem.attributes.get(atname) match {
      case Some(att)=>
        if(v.now!=att.value.toString) {
          v()=att.value
        }

      case None => dom.console.error(s"no attributed for $atname")
    }
  }

  /**
   * Binds html property
   * @param el
   * @param key
   * @param att
   */
  def bindInnerHTML(el:HTMLElement,key:String,att:String): Unit=    this.strings.get(att).foreach{str=>
    el.onchange = this.makePropHandler(el,str,"innerHTML")
    this.bindInner(el,key,str)
  }


  def bindInner(el:HTMLElement,key:String,str:Rx[String]) = this.bindRx(key,el:HTMLElement,str){ (el,value)=>
    el.innerHTML = value
  }


  /**
   * @param el element
   * @param key key
   * @param value value
   * @param mp map
   */
  def bindAttribute(el:HTMLElement,key:String,value:String,mp:Map[String,Rx[String]]): Boolean =  mp.get(value) match
  {
    case Some(str)=>
      this.bindRx(key, el: HTMLElement, str) {
        (el, value) =>
          //dom.console.info((key -> value.toString).toAtt.toString)
          el.attributes.setNamedItem((key -> str.now).toAtt)
          el.dyn.updateDynamic(key)(str.now) //TODO: check if redundant
      }
      true

    case _=>false
    //dom.console.error(s"unknown binding for $key with attribute $value")

  }

}
