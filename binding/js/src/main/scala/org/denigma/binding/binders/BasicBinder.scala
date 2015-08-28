package org.denigma.binding.binders

import org.denigma.binding.extensions._
import org.denigma.binding.views.IDGenerator
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
import rx._

import scala.collection.immutable.Map
import scala.scalajs.js

object Binder{
  def apply(elemBind:(HTMLElement,Map[String,String])=>PartialFunction[(String, String), Unit]) = new BasicBinder {
    override def bindAttributes(el: HTMLElement, ats: Map[String, String]): Unit = {
      val fun = elementPartial(el,ats)
      for((key,value)<-ats) fun(key,value)
    }

    override def elementPartial(el: HTMLElement, ats: Map[String, String]): PartialFunction[(String, String), Unit] = elemBind(el,ats)
  }
}


/**
 *  A class that contains basic functions for all bindings
 */
trait BasicBinder extends IDGenerator
{
  /**
   * binds an item of Map with vars
   * @param el html element to bind to
   * @param mp map with vars
   * @param attribute name of the binding attribute (for debugging)
   * @param key kay we want to bind to
   * @param bind functions that binds
   * @tparam T type parameter for Var
   * @return
   */
  protected def bindMapItem[T](el:HTMLElement,mp:Map[String,Var[T]],attribute:String,key:String)
                          (bind:(HTMLElement,Var[T])=>Unit) =
    mp.get(key) match{
      case Some(reactive)=> bind(el,reactive)
      case None=>
        dom.console.error(s"bindMapItem: cannot bind $attribute in ${el.outerHTML} to $key")
        dom.console.log("current map =" + mp.keys.toString())
    }

  def elementPartial(el: HTMLElement,ats:Map[String, String]): PartialFunction[(String,String),Unit]

  def bindAttributes(el:HTMLElement,ats:Map[String, String] ):Unit = {
    val fun: PartialFunction[(String, String), Unit] = elementPartial(el,ats).orElse{case other=>}
    ats.foreach(fun)
  }

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
}
