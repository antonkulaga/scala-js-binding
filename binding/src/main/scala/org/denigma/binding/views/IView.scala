package org.denigma.binding.views

import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.dom.extensions._
import scala.scalajs.js
import scala.util.{Failure, Success, Try}

trait ILogged {

  def error(errorText:String) = dom.console.error(errorText)

  def warning(warning:String) = dom.console.warn(warning)

  def info(message:String) = dom.console.info(message:String)

}

trait IView extends ILogged{

  def id:String

  def viewElement: HTMLElement

  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  def bindView(el:HTMLElement):Unit

  def parseHTML(string:String): Option[HTMLElement] ={
   val p = new  DOMParser()
    Try {
      p.parseFromString(string, "text/html")
    } match {
      case Success(doc)=>
        doc.body.children.collectFirst{case html:HTMLElement=>html}

      case Failure(th)=>
        this.error(th.toString)
        None
    }
  }


  def unbindView():Unit

}

class  DOMParser extends js.Object {

  def parseFromString(string:String, tp:String):dom.Document = ???




}
