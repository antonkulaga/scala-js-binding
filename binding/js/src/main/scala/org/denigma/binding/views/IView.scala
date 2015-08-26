package org.denigma.binding.views

import org.denigma.binding.binders.WithID
import org.denigma.binding.commons.DOMParser
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.raw._

import scala.util.{Failure, Success, Try}



trait IView extends WithID{

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
        dom.console.error(th.toString)
        None
    }
  }


  def unbindView():Unit

}


