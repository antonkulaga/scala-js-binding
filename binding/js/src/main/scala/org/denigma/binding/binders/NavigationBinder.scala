package org.denigma.binding.binders

import org.denigma.binding.extensions.{sq, _}
import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.ext.{Ajax, _}
import org.scalajs.dom.raw.{Element, HTMLElement, XMLHttpRequest}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.util.{Failure, Success}

class NavigationBinder(view: BindableView) extends ReactiveBinder{


  protected def processUrl(url:String, relativeURI:Boolean = true): String =
    (url.indexOf("://"),url.indexOf("/"),url.indexOf("?"))
    match {
      case (-1,sl,q)=> sq.withHost(url)
      case (prot,sl,q)  if sl > -1 && sl<prot =>
        val st = prot+3
        sq.withHost(url.substring(url.indexOf("/",st)))

      case  other => if(url.contains("domain")) url.replace("domain", dom.window.location.host) else url
    }

  /**
   * Loads links into some view
   * @param element
   * @param into
   */
  def makeGoToHandler(element: Element, into: String, push: Boolean = true, relativeURI: Boolean = true): js.Function1[MouseEvent, _] = {
    event: MouseEvent => {
      event.preventDefault()
      element.attributes.get("href") match {
        case Some(url) =>
          val uri = processUrl(url.value, relativeURI)
          Ajax.get(uri, headers = Map("X-PJAX" -> into)).onComplete {
            case Success(req: XMLHttpRequest) =>
              sq.byId(into) match {
                case Some(el: HTMLElement) => this.loadElementInto(el, req.responseText, uri)
                case None => dom.console.warn(s"cannot find $into element")
              }
            case Failure(th) => dom.console.error(s"there is a  ${th} problem with $uri ajax request\n with stack: ${th.stackString}")
          }
        case None => dom.console.error(s"there is not url here to load anything into")
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
  def loadElementInto(el: HTMLElement, newInnerHTML: String, uri: String): Unit = view.loadElementInto(el, newInnerHTML, uri)

  /**
   * Loads element into another one
   * @param el
   * @return
   */
  protected def loadIntoPartial(el: Element): PartialFunction[(String, String), Unit] = {
    case ("load-into", value) =>
      bindLoadInto(el, value, rel = true)
    case ("load-abs-into", value) => bindLoadInto(el, value, rel = false)
  }

  /**
   * Loads links into some view
   * @param element element
   * @param into where to load
   */
  def bindLoadInto(element: Element, into: String, rel: Boolean): Unit = element.addEventListener[MouseEvent](
    Events.click, this.makeGoToHandler(element, into, push = true, rel)
  )


  override def bindAttributes(el: Element, ats: Map[String, String]) = {
    this.bindDataAttributes(el, this.dataAttributesOnly(ats))
    true
  }

  def bindDataAttributes(el: Element, ats: Map[String, String]): Unit = {
    val fun: PartialFunction[(String, String), Unit] = elementPartial(el, ats).orElse{case other => }
    ats.foreach(fun)
  }

  override def elementPartial(el: Element, ats: Map[String, String]): PartialFunction[(String, String), Unit] = loadIntoPartial(el)

}
