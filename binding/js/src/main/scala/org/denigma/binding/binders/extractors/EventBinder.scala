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

  protected def keyboardEvents: Map[String, Var[KeyboardEvent]]

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
    case key if noDash(key).contains(Events.click) => Events.click
    case key if noDash(key).contains(Events.mouseover) => Events.mouseover
    case key if noDash(key).contains(Events.mouseout) => Events.mouseout
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

  def eventsPartial(el: Element): PartialFunction[(String, String), Unit] = keyboardEventsPartial(el).orElse(mouseEventsPartial(el)).orElse(otherEventsPartial(el))

}
