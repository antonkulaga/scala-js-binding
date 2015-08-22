package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.BasicBinder
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
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


  def eventsPartial(el:HTMLElement,value:String):PartialFunction[String,Unit] = {
    case key if key =="event-click" | key=="click" | key == "event-mouse-click" =>
      this.bindEventMap(el,mouseEvents,key,value)(bindClick)

    case key if key =="event-mouse-enter" | key=="mouse-enter" =>
      this.bindEventMap(el,mouseEvents,key,value)(bindMouseEnter)

    case key if key =="event-mouse-leave" | key=="mouse-leave" =>
      this.bindEventMap(el,mouseEvents,key,value)(bindMouseLeave)

    case key if key =="event-mouse-move" | key=="mouse-move" =>
      this.bindEventMap(el,mouseEvents,key,value)(bindMouseMove)

    case key if key =="event-mouse-over" | key=="mouse-over" =>
      this.bindEventMap(el,mouseEvents,key,value)(bindMouseOver)

    case key if key =="event-mouse-out" | key=="mouse-out" =>
      this.bindEventMap(el,mouseEvents,key,value)(bindMouseOver)

    case key if key=="event-onkeydown" | key=="event-keydown"=>
      this.bindEventMap(el,keyboardEvents,key,value)(bindKeyDown)

    case key if key=="event-onkeyup" | key=="event-keyup"=>
      this.bindEventMap(el,keyboardEvents,key,value)(bindKeyUp)

    case key if key=="event-onkeypress" | key=="event-keypress"=>
      this.bindEventMap(el,keyboardEvents,key,value)(bindKeyPress)

    case _ => //some other thing to do
  }

  def bindEvents(el:HTMLElement,ats:Map[String, String]) = for {  (key, value) <- ats  }  { this.eventsPartial(el,value)(key) }


  protected def bindEvent[TEvent<:dom.Event](el:HTMLElement,ev:Var[TEvent])(fun:(HTMLElement,js.Function1[TEvent,Unit])=>Unit) = {
    def eventHandler(m:TEvent):Unit = {  ev()= m   }
    fun(el,eventHandler _)
  }

  protected def bindEventMap[TEvent<:Event](el:HTMLElement,mp:Map[String,Var[TEvent]],key:String,value:String)
                                       (bind:(HTMLElement,Var[TEvent])=>Unit) =
    mp.get(value) match{
      case Some(ev)=> bind(el,ev)
      case None=>
        dom.console.error(s"cannot bind ${key} in ${el.outerHTML} to $value")
        dom.console.log("current events =" + mp.keys.toString())
    }

  protected def bindClick(el:HTMLElement,ev:Var[MouseEvent]) = bindEvent[MouseEvent](el,ev)((e,h)=>e.onclick = h)

  protected def bindMouseEnter(el:HTMLElement,ev:Var[MouseEvent]) = bindEvent[MouseEvent](el,ev)((e,h)=>e.onmouseenter = h)

  protected def bindMouseLeave(el:HTMLElement,ev:Var[MouseEvent]) = bindEvent[MouseEvent](el,ev)((e,h)=>e.onmouseleave = h)

  protected def bindMouseMove(el:HTMLElement,ev:Var[MouseEvent]) = bindEvent[MouseEvent](el,ev)((e,h)=>e.onmousemove = h)

  protected def bindMouseOver(el:HTMLElement,ev:Var[MouseEvent]) = bindEvent[MouseEvent](el,ev)((e,h)=>e.onmouseover = h)

  protected def bindMouseOut(el:HTMLElement,ev:Var[MouseEvent]) = bindEvent[MouseEvent](el,ev)((e,h)=>e.onmouseout = h)

  protected def bindMouseDown(el:HTMLElement,ev:Var[MouseEvent]) = bindEvent[MouseEvent](el,ev)((e,h)=>e.onmousedown = h)

  protected def bindMouseUp(el:HTMLElement,ev:Var[MouseEvent]) = bindEvent[MouseEvent](el,ev)((e,h)=>e.onmouseup = h)

  protected def bindKeyDown(el:HTMLElement,ev:Var[KeyboardEvent]) = bindEvent(el,ev)((e,h)=>e.onkeydown = h)

  protected def bindKeyUp(el:HTMLElement,ev:Var[KeyboardEvent]) = bindEvent(el,ev)((e,h)=>e.onkeyup = h)

  protected def bindKeyPress(el:HTMLElement,ev:Var[KeyboardEvent]) = bindEvent(el,ev)((e,h)=>e.onkeypress = h)


}
