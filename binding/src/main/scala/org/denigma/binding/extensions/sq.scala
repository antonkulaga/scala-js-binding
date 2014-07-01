package org.denigma.binding.extensions

import scala.Some

import org.scalajs.dom._

import org.scalajs.dom.Attr
import org.scalajs.dom
import dom.extensions._
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js
import scala.concurrent.Future
import org.scalajs.spickling.jsany._


import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.scalajs.spickling._
import scala.scalajs.js.prim.Undefined


/**
 * "ScalaQuery" helper for convenient DOM manipulation and other useful things
 */
object sq{


  /** *
    * transforma string into address with current host
    * @param str
    * @return
    */
  def h(str:String): String = "http://"+ dom.window.location.host+( if(str.startsWith("/") ) "" else "/")+str

  /**
   * Creates attribute
   * @param name
   * @param value
   * @return
   */
  def makeAttr(name:String,value:String): Attr = {
    val r = dom.document.createAttribute(name)
    r.value = value
    r
  }

  def byId(id:String): Option[HTMLElement] = dom.document.getElementById(id) match {
    case null=>None
    case v if v.isInstanceOf[Undefined] =>None
    case el=>Some(el)
  }

  def find(query:String): Option[Element] = dom.document.querySelector(query) match
  {
    case null=>None
    case el=>Some(el)

  }

  def query(query:String): NodeList = dom.document.querySelectorAll(query)


  /**
   * Puts pickled value
   * @param url
   * @param data
   * @param timeout
   * @param headers
   * @param withCredentials
   * @tparam T
   * @return
   */
  def put[T](url:String,data:T,timeout:Int = 0,
              headers: Seq[(String, String)] =("Content-Type", "application/json;charset=UTF-8")::Nil,
              withCredentials:Boolean = false
               )(implicit registry:PicklerRegistry) : Future[XMLHttpRequest] = {
    Ajax.apply("PUT", url, this.pack2String(data), timeout, headers, withCredentials)
  }


  def delete[T](url:String,data:T,timeout:Int = 0,
             headers: Seq[(String, String)] =("Content-Type", "application/json;charset=UTF-8")::Nil,
             withCredentials:Boolean = false
              )(implicit registry:PicklerRegistry) : Future[XMLHttpRequest] = {
    Ajax.apply("DELETE", url, this.pack2String(data), timeout, headers, withCredentials)
  }

  /**
   * Get method that does pickling,
   * WARNING: always make sure that picklers are registered before calling it
   * @param url address
   * @param timeout timeout
   * @param headers headers of the request
   * @param withCredentials
   * @return value of appropriate type
   */
  def get[T](url:String,timeout:Int = 0,
            headers: Seq[(String, String)] =("Content-Type", "application/json;charset=UTF-8")::Nil,
            withCredentials:Boolean = false
            )(implicit registry:PicklerRegistry) : Future[T] =
    this.pickleRequest[T](Ajax.apply("GET", url, "", timeout, headers, withCredentials))(registry)


  /**
   * Post method that does nice thing
   * @param url
   * @param data
   * @param timeout
   * @param headers
   * @param withCredentials
   * @return
   */
  def post[TIn,TOut](
            url:String,data:TIn,timeout:Int = 0,
            headers: Seq[(String, String)] =("Content-Type", "application/json;charset=UTF-8")::Nil,
            withCredentials:Boolean = false
            )(implicit registry:PicklerRegistry) : Future[TOut] =
    this.pickleRequest[TOut](Ajax.apply("POST", url,this.pack2String(data), timeout, headers, withCredentials))



  def withHost(str:String): String = {
    "http://"+dom.location.host+(if(str.startsWith("/") || str.startsWith("#")) str else "/"+str)
  }

  def pack2String[T](data:T)(implicit registry:PicklerRegistry) = {
    val p: js.Any = registry.pickle(data)
    g.JSON.stringify(p).toString

  }

  def pickleRequest[T](req:Future[XMLHttpRequest])(implicit registry:PicklerRegistry) : Future[T] = req.map{case r=>
    val v = js.JSON.parse(r.responseText).asInstanceOf[js.Any]

   registry.unpickle(v) match {
      case value:T=>value
      case _=>
        val ex = s"unpickling problem with $v"
        console.error(ex)
        throw new Exception(ex)
    }
  }


}