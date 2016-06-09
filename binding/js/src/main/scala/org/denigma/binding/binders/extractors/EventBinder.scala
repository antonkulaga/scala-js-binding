package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.{Events, ReactiveBinder}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.{KeyboardEvent, HTMLElement}
import rx._

import scala.scalajs.js

/**
 * Extracts various events
 */
trait EventBinder extends ReactiveBinder
{
  import org.denigma.binding.extensions._

  protected def mouseEvents: Map[String, Var[MouseEvent]]

  protected def wheelEvents: Map[String, Var[WheelEvent]]

  protected def keyboardEvents: Map[String, Var[KeyboardEvent]]

  protected def dragEvents: Map[String, Var[DragEvent]]

  protected def focusEvents: Map[String, Var[FocusEvent]]

  protected def events: Map[String, Var[Event]]

  protected def noDash(key: String) = key.replace("-", "")

  protected def keyboardEventFromKey: PartialFunction[String, String] = {
    case key if noDash(key).contains(Events.keyup) => Events.keyup
    case key if noDash(key).contains(Events.keydown) => Events.keydown
    case key if noDash(key).contains(Events.keypress) => Events.keypress
  }

  protected def mouseEventFromKey: PartialFunction[String, String] = {
    case key if noDash(key).contains(Events.mouseenter) => Events.mouseenter
    case key if noDash(key).contains(Events.mouseleave) => Events.mouseleave
    case key if noDash(key).contains(Events.mouseup) => Events.mouseup
    case key if noDash(key).contains(Events.mousedown) => Events.mousedown
    case key if noDash(key).contains(Events.mouseover) => Events.mouseover
    case key if noDash(key).contains(Events.mouseout) => Events.mouseout
    case key if noDash(key).contains(Events.dblclick) => Events.dblclick
    case key if noDash(key).contains("doubleclick") => Events.dblclick
    case key if noDash(key).contains(Events.click) => Events.click
  }

  protected def mouseWheelFromKey: PartialFunction[String, String] = {
    case key if noDash(key).contains(Events.mousewheel) => Events.mousewheel
    case key if noDash(key).contains(Events.wheel) => Events.wheel
  }

  protected def dragFromKey: PartialFunction[String, String] = {
    case key if noDash(key).contains(Events.beforecopy) => Events.beforecopy
    case key if noDash(key).contains(Events.beforecut) => Events.beforecut
    case key if noDash(key).contains(Events.beforepaste) => Events.beforepaste
    case key if noDash(key).contains(Events.copy) => Events.copy
    case key if noDash(key).contains(Events.cut) => Events.cut
    case key if noDash(key).contains(Events.paste) => Events.paste
    case key if noDash(key).contains(Events.dragend) => Events.dragend
    case key if noDash(key).contains(Events.dragenter) => Events.dragenter
    case key if noDash(key).contains(Events.dragleave) => Events.dragleave
    case key if noDash(key).contains(Events.dragover) => Events.dragover
    case key if noDash(key).contains(Events.dragstart) => Events.dragstart
    case key if noDash(key).contains(Events.drop) => Events.drop
    case key if noDash(key).contains(Events.drag) => Events.drag
  }


  protected def focusFromKey: PartialFunction[String, String] = {
    case key if noDash(key).contains(Events.focusin) => Events.focusin
    case key if noDash(key).contains(Events.focusout) => Events.focusout
    case key if noDash(key).contains(Events.focus) => Events.focus
  }

  protected def otherEventFromKey: PartialFunction[String, String] = {
    case key if noDash(key).contains(Events.change) => Events.change
  }

  protected def mouseEventsPartial(el: Element): PartialFunction[(String, String), Unit] = {
    case (key, value) if mouseEventFromKey.isDefinedAt(key) =>
      val event: String = mouseEventFromKey(key)
      this.bindMapItem(el, mouseEvents, key, value)((e, v) =>
        e.addEventListener[MouseEvent](event, {ev: MouseEvent=>v()= ev })
      )
  }


  protected def wheelEventsPartial(el: Element): PartialFunction[(String, String), Unit] = {
    case (key, value) if mouseWheelFromKey.isDefinedAt(key) =>
      val event: String = mouseWheelFromKey(key)
      this.bindMapItem(el, wheelEvents, key, value)((e, v) =>
        e.addEventListener[WheelEvent](event, {ev: WheelEvent=>v()= ev })
      )
  }

  protected def dragEventsPartial(el: Element): PartialFunction[(String, String), Unit] = {
    case (key, value) if dragFromKey.isDefinedAt(key) =>
      val event: String = dragFromKey(key)
      this.bindMapItem(el, dragEvents, key, value)((e, v) =>
        e.addEventListener[DragEvent](event, {ev: DragEvent=>v()= ev })
      )
  }

  protected def focusEventsPartial(el: Element): PartialFunction[(String, String), Unit] = {
    case (key, value) if focusFromKey.isDefinedAt(key) =>
      val event: String = focusFromKey(key)
      this.bindMapItem(el, focusEvents, key, value)((e, v) =>
        e.addEventListener[FocusEvent](event, {ev: FocusEvent=>v()= ev })
      )
  }


  protected def keyboardEventsPartial(el: Element): PartialFunction[(String, String), Unit] = {
    case (key, value) if keyboardEventFromKey.isDefinedAt(key) =>
      val event = keyboardEventFromKey(key)
      this.bindMapItem(el, keyboardEvents, key, value)((e, v) =>
        e.addEventListener[KeyboardEvent](event, {ev: KeyboardEvent => v()= ev })
      )
  }

  protected def otherEventsPartial(el: Element): PartialFunction[(String, String), Unit] = {
    case (key, value) if noDash(key).contains(Events.change) =>
      this.bindMapItem(el, events, key, value)((e, v) =>
        e.addEventListener[Event](Events.change, {ev: Event => v()= ev })
      )
  }

  def eventsPartial(el: Element): PartialFunction[(String, String), Unit] = keyboardEventsPartial(el)
    .orElse(mouseEventsPartial(el))
    .orElse(wheelEventsPartial(el))
    .orElse(dragEventsPartial(el))
    .orElse(focusEventsPartial(el))
    .orElse(otherEventsPartial(el))

}
