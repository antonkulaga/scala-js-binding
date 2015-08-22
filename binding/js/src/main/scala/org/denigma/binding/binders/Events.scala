package org.denigma.binding.binders

import org.scalajs.dom
import org.scalajs.dom._

object Events {

  def createEvent() = dom.document.createEvent("Event")

  def createTouchEvent() = dom.document.createEvent("TouchEvent").asInstanceOf[TouchEvent]

  def createTextEvent() = dom.document.createEvent("TextEvent").asInstanceOf[TextEvent]

  def createKeyboardEvent() = dom.document.createEvent("KeyboardEvent").asInstanceOf[KeyboardEvent]

  def createMouseEvent() = dom.document.createEvent("MouseEvent").asInstanceOf[MouseEvent]

  def createMessageEvent() = dom.document.createEvent("MessageEvent").asInstanceOf[MessageEvent]

  def createErrorEvent() =  dom.document.createEvent("ErrorEvent").asInstanceOf[ErrorEvent]

  def createCloseEvent() = dom.document.createEvent("CloseEvent").asInstanceOf[CloseEvent]

  def createStorageEvent() = dom.document.createEvent("StorageEvent").asInstanceOf[StorageEvent]

  def createWheelEvent() = dom.document.createEvent("WheelEvent").asInstanceOf[WheelEvent]


}
