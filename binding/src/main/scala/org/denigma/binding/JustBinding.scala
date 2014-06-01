package org.denigma.binding

import org.scalajs.dom.{Event, HTMLElement}
import org.scalajs.dom.extensions.Ajax
import scala.util.Success
import rx._
import org.scalajs.dom._
import scala.collection.mutable
import org.scalajs.dom
import org.denigma.binding.macroses.{ClassToMap, StringRxMap}
import org.denigma.extensions._

import dom.extensions._
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.util.Success
import scala.util.Failure
import scala.Some
import scala.scalajs.js.{Dynamic}


/**
 * Just a basic subclass for all bindings
 */
abstract class JustBinding {


  /**
   * Loads links into some view
   * @param element
   * @param into
   */
  def makeGoToHandler(element:HTMLElement,into: String, push:Boolean = true, relativeURI:Boolean = true):js.Function1[MouseEvent, _]  =  {event:MouseEvent=>{

      event.preventDefault()
      element.attributes.get("href") match {
        case Some(url) =>
          val uri = processUrl(url.value,relativeURI)

          val pjax = ("X-PJAX",into)

          Ajax.get(uri, headers = List(pjax)).onComplete {
            case Success(req: XMLHttpRequest) => sq.byId(into) match {
              case Some(el: HTMLElement) =>this.loadElementInto(el,uri,req.responseText)
              case None => dom.console.error(s"cannot find $into element")
            }

            case Failure(th) => dom.console.error(s"there is a problem with ${uri} ajax request")
          }


        case None=> dom.console.error(s"there is not url here to load anything into")
      }
    false
    }
  }


  /**
   *
   * Loads element into another one
   * @param el element
   * @param uri uri (for push state)
   * @param newInnerHTML new content of the inner html
   */
  def loadElementInto(el:HTMLElement, newInnerHTML:String,uri:String = "") = {
    val params = js.Dynamic.literal( "html" -> newInnerHTML)
    if(uri!="")  dom.window.history.pushState(params,dom.document.title,uri)
    el.innerHTML = newInnerHTML
  }

  protected def processUrl(url:String, relativeURI:Boolean = true):String =
    if(url.contains("://")) {
      val st = url.indexOf("://")+3
      sq.withHost(url.substring(url.indexOf("/",st)))
    }
    else
      sq.withHost(url)


    /**
   * Loads
   * @param uri
   * @param into
   */
  def loadInto(uri:String,into:String): Unit = {
    val pjax = ("X-PJAX",into)

    Ajax.get(uri, headers = List(pjax)).onComplete {
      case Success(req) => sq.byId(into) match {
        case Some(el) =>
          el.innerHTML = req.responseText
          val params = js.Dynamic.literal( "html" -> req.responseText)

          dom.window.history.pushState(params,dom.document.title,uri)

        case None => dom.console.error(s"cannot find $into element")
      }

      case Failure(th) => dom.console.error(s"there is a problem with ${uri} ajax request")
    }
  }

  /**
   * Makes id for the binding element
   * @param el html element
   * @param title title of id
   * @return
   */
  def makeId(el:HTMLElement,title:String) = el.id match {
    case s if s=="" ||  s==null || s.isInstanceOf[js.prim.Undefined]=>
      el.id = title + "_" + math.random
      el.id

    case id=>
      id

  }


  /**
   * Binds value to reactive property
   * @param key key to witch to bind to
   * @param el html element
   * @param rx reactive variable
   * @param assign assign function that assigns value to html element
   * @tparam T type param
   * @return
   */
  def bindRx[T](key:String,el:HTMLElement ,rx:Rx[T])(assign:(HTMLElement,T)=>Unit) = {
    val eid = this.makeId(el, key)
    lazy val obs: Obs = Obs(rx, eid, skipInitial = false) {
      dom.document.getElementById(eid) match {
        case null =>
          dom.console.info(s"$eid was not find, killing observable...")
          obs.kill()

        case element: HTMLElement =>
          val value = rx.now
          //el.dyn.obs = obs.asInstanceOf[js.Dynamic]
          assign(element, value)
      }
    }
    val o = obs //TO MAKE LAZY STUFF WORK
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
    case _=> dom.console.error(s"rx is not Var")
  }


}
