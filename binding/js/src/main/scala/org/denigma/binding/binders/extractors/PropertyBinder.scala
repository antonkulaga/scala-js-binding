package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.{Events, BasicBinder}
import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.{HTMLTextAreaElement, HTMLInputElement, HTMLElement}
import org.scalajs.dom.{Event, KeyboardEvent}
import rx._

import scala.collection.immutable.Map

/**
 * Does binding for classes
 */
trait PropertyBinder {
  self:BasicBinder=>

  def strings:Map[String,Rx[String]]
  def bools:Map[String,Rx[Boolean]]

  /**
   * Partial function that is usually added to bindProperties
   * @param el html element
   * @return
   */
  protected def propertyPartial(el:HTMLElement):PartialFunction[(String,String),Unit] = {
    case (str,rxName) if str.contains("style-") => this.bindStyle(el,rxName,str.replace("bind-","").replace("style-",""))
    case (bname,rxName) if bname.startsWith("bind-")=>this.bindProperty(el,rxName,bname.replace("bind-",""))
    case ("html",rxName) => subscribeProperty[Event](el,rxName,"innerHTML",Events.change)
    case ("bind",rxName) => bind(el,rxName)
  }

  protected def bindStyle(el:HTMLElement,rxName:String,prop:String): Unit= {
    this.strings.get(rxName) match {
      case Some(str) =>
        ifNoID(el,s"${rxName}_${prop}_styles")
        import rx.ops._
        str.foreach(s=>el.style.dyn.updateDynamic(prop)(s))
       case None=>
        this.cannotFindStr(rxName,prop)
    }
  }

  import rx.ops._
  import org.denigma.binding.extensions._


  /**
   * Bind property
   * @param el html element
   * @param att value of attribute
   * @return
   */
  def bind(el:HTMLElement,att:String): Unit =  el match
  {
    case inp:HTMLInputElement=>
      el.attributes.get("type").map(_.value.toString) match {
        case Some("checkbox") =>
          ifNoID(el, att + "_checkbox")
          for (b <- bools.get(att)) b.foreach(v => el.attributes.setNamedItem(("checked" -> v.toString).toAtt))
        case tp =>
          ifNoID(el, att)
          //subscribeProperty[KeyboardEvent](el,att,"value",Events.keyup)
          subscribeValue(el,att,Events.keyup)
      }
    case area:HTMLTextAreaElement =>
      ifNoID(el, att+"_textarea")
      subscribeValue(el,att,Events.keyup)
      //subscribeProperty[KeyboardEvent](el,att,"value",Events.keyup)

    case other=> subscribeProperty[Event](el,att,"textContent",Events.change)

  }

  protected def cannotFindStr(rxName:String,prop:String) =
    dom.console.error(s"cannot find $rxName string reactive variable for prop $prop\n, all strings are: \n"+
    strings.mapValues(_.name).mkString(" | ")
  )

  /*
  Has fix for caret change
   */
  protected def subscribeValue(el:HTMLElement,rxName: String,event:String): Unit =  this.strings.get(rxName) match{
    case Some(str)=>
      str.foreach { case s =>
        el match {
          case inp:HTMLInputElement=>
            val (start,end) =(inp.selectionStart,inp.selectionEnd)
            inp.value = s
            inp.selectionStart = start
            inp.selectionEnd = end

          case area:HTMLTextAreaElement=>
            val (start,end) =(area.selectionStart,area.selectionEnd)
            area.value = s
            area.selectionStart = start
            area.selectionEnd = end

          case other=> el.dyn.value = s
        }

      }
      str.onVar { case v =>
        el.addEventListener[KeyboardEvent](event,(ev: KeyboardEvent) => {
        ev.currentTarget
           if(ev.target==ev.currentTarget) el.onExists("value")(value => v.set(value.toString)) }
        )
        v.set(el.dyn.selectDynamic("value").toString)
      }
    case None=> cannotFindStr(rxName,"value")
  }


  protected def subscribeProperty[TEvent<:dom.Event](el:HTMLElement,rxName: String,prop:String, event:String): Unit =  this.strings.get(rxName) match{
    case Some(str)=>
      str.foreach(s=>el.dyn.updateDynamic(prop)(s))
      str.onVar { case v =>
        el.addEventListener[TEvent](event,(ev: TEvent) => {
           if(ev.target==ev.currentTarget) el.onExists(prop)(value => v.set(value.toString)) }
        )
        v.set(el.dyn.selectDynamic(prop).toString)
      }
    case None=> cannotFindStr(rxName,prop)
  }

  protected def rxPropertyUpdate(el:HTMLElement,rxName: String,prop:String): Unit =  this.strings.get(rxName) match{
    case Some(str)=> str.foreach(s=>el.dyn.updateDynamic(prop)(s))
    case None=> cannotFindStr(rxName,prop)
  }


  protected def bindProperty(el:HTMLElement,rxName:String,prop:String) = strings.get(rxName) match {
    case Some(str)=>
      str.foreach{  case s=>
        el.attributes.setNamedItem((prop -> s).toAtt)
        el.dyn.updateDynamic(prop)(s)
      }
    case None=> cannotFindStr(rxName,prop)
  }

}