package org.denigma.binding.binders

import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
import rx._

import scala.Predef
import scala.collection.immutable.Map
import scala.scalajs.js

object Binder{
  def apply(attribuetsBind:(HTMLElement,Map[String,String])=>Unit) = new BasicBinder {
    override def bindAttributes(el: HTMLElement, ats: Predef.Map[String, String]): Unit = attribuetsBind(el,ats)
  }

}
/*

trait BindingSyntax
class BasicBindingSyntax {

}
*/

/**
 *  A class that contains basic functions for all bindings
 */
trait BasicBinder
{

  def bindAttributes(el:HTMLElement,ats:Map[String, String] ):Unit

  protected def dataAttributesOnly(ats:Map[String,String]): Map[String, String] = ats.collect{
    case (key,value) if key.contains("data-") && !key.contains("data-view")=> (key.replace("data-",""),value)
  }

  protected def processUrl(url:String, relativeURI:Boolean = true):String =
    (url.indexOf("://"),url.indexOf("/"),url.indexOf("?"))
    match {
      case (-1,sl,q)=> sq.withHost(url)
      case (prot,sl,q)  if sl > -1 && sl<prot =>
        val st = prot+3
        sq.withHost(url.substring(url.indexOf("/",st)))

      case  other => if(url.contains("domain")) url.replace("domain",dom.location.host) else url
    }

  /**
   * Makes id for the binding element
   * @param el html element
   * @param title is used if the element does not have an ID
   * @return
   */
  def withID(el:HTMLElement,title:String): String = el.id match {
    case s if js.isUndefined(s) || s=="" ||  s==null /*|| s.isInstanceOf[js.prim.Undefined] */=>
      el.id = title + "#" +  math.round(10000*math.random) //to make length shorter
      el.id

    case id=>
      id

  }


  def bindVar[T](key:String,el:HTMLElement ,v:Var[T])(assign:(HTMLElement,Var[T])=>Unit): Obs  = {
    //TODO: deprecate
    val eid = this.withID(el, key) //assigns id
    Obs(v, eid, skipInitial = false) {  assign(el,v)  }
  }

  def bindRx[T](key:String,el:HTMLElement ,rx:Rx[T])(assign:(HTMLElement,T)=>Unit): Obs = {
    //TODO: deprecate
    val eid = this.withID(el, key)
    Obs(rx, eid, skipInitial = false) {
      val value = rx.now
      assign(el,value)
    }
  }


  /**
   * Creates and even handler that can be attached to different listeners
   * @param el element
   * @param par rx parameter
   * @param assign function that assigns var values to some element properties
   * @tparam TEV type of event
   * @tparam TV type of rx
   * @return
   */
  def makeEventHandler[TEV<:Event,TV](el:HTMLElement,par:Rx[TV])(assign:(TEV,Var[TV],HTMLElement)=>Unit):(TEV)=>Unit = ev=> par match {
    case v:Var[TV] => assign(ev,v,el)
    case _=> dom.console.error(s"rx is not Var in ${el.outerHTML}")
  }



  protected def otherPartial:PartialFunction[String,Unit] = {case _=>}

}
