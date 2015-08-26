package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.BasicBinder
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.{KeyboardEvent, HTMLElement}
import rx._

import scala.scalajs.js

/**
 * Extracts various events
 */
trait EventBinder  extends BasicBinder
{
  import org.denigma.binding.extensions._

  protected def mouseEvents: Map[String, Var[MouseEvent]]

  protected def keyboardEvents:Map[String,Var[KeyboardEvent]]


  def eventsPartial(el:HTMLElement):PartialFunction[(String,String),Unit] = {
    case (key,value) if key =="event-click" | key=="click" | key == "event-mouse-click" =>
      this.bindMapItem(el,mouseEvents,key,value) { case (e, v) =>
        e.onclick = (ev:MouseEvent)=>v() = ev
      }

    case (key,value) if key =="event-mouse-enter" | key=="mouse-enter" =>
      this.bindMapItem(el,mouseEvents,key,value) { case (e, v) =>
        e.onmouseenter = (ev:MouseEvent)=>v() = ev
      }

    case (key,value) if key =="event-mouse-leave" | key=="mouse-leave" =>
      this.bindMapItem(el,mouseEvents,key,value) { case (e, v) =>
        e.onmouseleave = (ev:MouseEvent)=>v() = ev
      }

    case (key,value) if key =="event-mouse-move" | key=="mouse-move" =>
      this.bindMapItem(el,mouseEvents,key,value) { case (e, v) =>
        e.onmousemove = (ev:MouseEvent)=>v() = ev
      }

    case (key,value) if key =="event-mouse-over" | key=="mouse-over" =>
      this.bindMapItem(el,mouseEvents,key,value) { case (e, v) =>
        e.onmouseover = (ev:MouseEvent)=>v() = ev
      }
    case (key,value) if key =="event-mouse-out" | key=="mouse-out" =>
      this.bindMapItem(el,mouseEvents,key,value) { case (e, v) =>
        e.onmouseout = (ev:MouseEvent)=>v() = ev
      }
    case (key,value) if key=="event-onkeydown" | key=="event-keydown"=>
      this.bindMapItem(el,keyboardEvents,key,value) { case (e, v) =>
          e.onkeydown = (ev:KeyboardEvent)=>v() = ev
      }

    case (key,value) if key=="event-onkeyup" | key=="event-keyup"=>
      this.bindMapItem(el,keyboardEvents,key,value) { case (e, v) =>
        e.onkeyup = (ev:KeyboardEvent)=>v() = ev
      }
    case (key,value) if key=="event-onkeypress" | key=="event-keypress"=>
      this.bindMapItem(el,keyboardEvents,key,value) { case (e, v) =>
        e.onkeypress = (ev:KeyboardEvent)=>v() = ev
      }

    case (key,value) if key.contains("event") =>
      dom.console.error(s"unknown event $key with value $value")
  }
}
