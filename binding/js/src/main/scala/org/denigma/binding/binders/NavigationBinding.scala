package org.denigma.binding.binders

import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom.{Attr, MouseEvent}
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.{XMLHttpRequest, HTMLElement}

import scala.scalajs.js
import scala.util.{Failure, Success}
import org.denigma.binding.extensions._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.scalajs.dom.ext._


class NavigationBinding(view:BindableView) extends BasicBinding{


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

        Ajax.get(uri, headers = Map("X-PJAX"->into)).onComplete {
          case Success(req: XMLHttpRequest) =>
            sq.byId(into) match {
              case Some(el: HTMLElement) =>
                this.loadElementInto(el,req.responseText,uri)

              case None =>
                dom.console.warn(s"cannot find $into element")
            }

          case Failure(th) => dom.console.error(s"there is a  ${th} problem with $uri ajax request\n with stack: ${th.stackString}")
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
  def loadElementInto(el: HTMLElement, newInnerHTML: String, uri: String): Unit = view.loadElementInto(el,newInnerHTML,uri)



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



  /**
   * Loads links into some view
   * @param element
   * @param into
   */
  def bindLoadInto(element:HTMLElement,into: String, rel:Boolean) =   element.onclick = this.makeGoToHandler(element,into,push = true, rel)

  override def bindAttributes(el: HTMLElement, ats: Map[String, String]): Unit = {
    this.bindDataAttributes(el,this.dataAttributesOnly(ats))
  }

  def bindDataAttributes(el: HTMLElement, ats: Map[String, String]) =  for {
    (key, value) <- ats
  }{
    loadIntoPartial(el,value).orElse(this.otherPartial)(key.toString)//key.toString is the most important!
  }

  override def id: String = view.id
}
