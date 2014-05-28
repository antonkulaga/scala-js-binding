package org.denigma.binding

import org.denigma.binding.macroses._
import org.scalajs.dom._
import org.scalajs.dom
import rx._
import scala.scalajs.js
import scala.collection.mutable
import scala.collection.immutable._

/**
 * Extracts various events
 */
trait EventBinding  extends JustBinding
{
  def extractEvents[T: EventMap](t: T): Map[String, Var[Event]] =  implicitly[EventMap[T]].asEventMap(t)
  def extractMouseEvens[T: MouseEventMap](t: T): Map[String, Var[MouseEvent]] =  implicitly[MouseEventMap[T]].asMouseEventMap(t)
  def extractTextEvents[T: TextEventMap](t: T): Map[String, Var[TextEvent]] =  implicitly[TextEventMap[T]].asTextEventMap(t)
  def extractKeyEvents[T: KeyEventMap](t: T): Map[String, Var[KeyboardEvent]] =  implicitly[KeyEventMap[T]].asKeyEventMap(t)
  def extractUIEvents[T: UIEventMap](t: T): Map[String, Var[UIEvent]] =  implicitly[UIEventMap[T]].asUIEventMap(t)
  def extractWheelEvents[T: WheelEventMap](t: T): Map[String, Var[WheelEvent]] =  implicitly[WheelEventMap[T]].asWheelEventMap(t)
  def extractFocusEvents[T: FocusEventMap](t: T): Map[String, Var[FocusEvent]] =  implicitly[FocusEventMap[T]].asFocusEventMap(t)

  def mouseEvents: Map[String, Var[MouseEvent]]

  //def textEvents:Map[String,Var[TextEvent]]

  def bindEvents(el:HTMLElement,ats:Map[String, String]) = for {
    (key, value) <- ats
    }
    {
      key.toString match {
        case "event-click" => this.mouseEvents.get(value) match {
          case Some(ev)=>this.bindClick(el,key,ev)
          case _ => dom.console.error(s"cannot bind click event for ${value}")
        }

        case _ => //some other thing to do
      }
    }

  def bindClick(el:HTMLElement,key:String,ev:Var[MouseEvent]) {

    def clickHandler(m:MouseEvent) = {
      ev()= m
    }

    el.onclick = clickHandler _
  }

  def createEvent() = dom.document.createEvent("Event")

  def createTextEvent() = dom.document.createEvent("TextEvent").asInstanceOf[TextEvent]

  def createMouseEvent() = dom.document.createEvent("MouseEvent").asInstanceOf[MouseEvent]





}
