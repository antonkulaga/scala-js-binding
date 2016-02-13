package org.denigma.binding.extensions

import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.{Attr, _}

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success, Try}


/**
 * "ScalaQuery" helper for convenient DOM manipulation and other useful things
 */
object sq
{


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
    //case v if v.isInstanceOf[Undefined] =>None
    case el:HTMLElement=>Some(el)
    case _=>dom.console.error(s"element for $id is not an html element");  None
  }

  def find(query:String): Option[Element] = dom.document.querySelector(query) match
  {
    case null=>None
    case el=>Some(el)

  }

  def query(query:String): NodeList = dom.document.querySelectorAll(query)


  def withHost(str:String): String = {
    "http://"+dom.window.location.host+(if(str.isPartOfUrl) str else "/"+str)
  }


  /**
   * Unpickles request result
   * @param url
   * @param request
   * @param unpickle
   * @tparam TOut
   * @return
   */
  def unpickleRequestResult[TOut](url:String,request:Future[XMLHttpRequest])(unpickle:String=>Try[TOut]): Future[TOut] =
    request.flatMap{r=>
      unpickle(r.responseText) match {
        case Success(v)=> Future.successful(v)
        case Failure(f)=>
          dom.console.error(s"cannot unpickle result of $url with ${f.getMessage} error")
          Future.failed(f)
      }
    }

  def tryGet[TOut]( url:String,timeout:Int = 0, headers: Map[String, String] =  Map.empty, withCredentials:Boolean = true)
                  (unpickle:String=>Try[TOut]): Future[TOut] = {

    val request: Future[XMLHttpRequest] =Ajax.apply("GET", url, "",  timeout,headers , withCredentials,"")
    this.unpickleRequestResult(url,request)(unpickle)
  }

  def tryPut[TIn]( url:String,data:TIn,timeout:Int = 0,
                   headers: Map[String, String] =  Map("Content-Type" -> "text/plain;charset=UTF-8"),
                   withCredentials:Boolean = true)
                  (pickle:TIn=>String): Future[XMLHttpRequest] =   Ajax.apply("PUT", url, pickle(data),  timeout,headers , withCredentials,"")


  def tryDelete[TIn](url:String,data:TIn,timeout:Int = 0,
                headers: Map[String, String] =Map("Content-Type" -> "application/json;charset=UTF-8"),
                withCredentials:Boolean = true
                 )(pickle:TIn=>String): Future[XMLHttpRequest] = {
    Ajax.apply("DELETE", url, pickle(data), timeout, headers, withCredentials,"")
  }




  /**
   * Posts with pickling and unpickling (with Prickle)
   * @param url URL of the request
   * @param data data to post to the server
   * @param timeout max timeout that is allowed
   * @param headers http headers for the request
   * @param withCredentials
   * @param pickle function that pickles data to send to the server
   * @param unpickle function that unpickles server response
   * @tparam TIn input type
   * @tparam TOut outpit type (form response)
   * @return
   */
  def tryPost[TIn,TOut]( url: String, data: TIn, timeout: Int = 0,
                         headers: Map[String, String] =       Map("Content-Type" -> "text/plain;charset=UTF-8"),
                         withCredentials: Boolean = false)(pickle: TIn => String)(unpickle : String => Try[TOut]): Future[TOut] = {
    val request: Future[XMLHttpRequest] = Ajax.apply("POST", url,pickle(data), timeout, headers, withCredentials,"text/plain;charset=UTF-8")
    unpickleRequestResult(url,request)(unpickle)

  }



}