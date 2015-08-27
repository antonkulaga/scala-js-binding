package org.denigma.controls.selection

import org.denigma.binding.binders.Events
import org.denigma.binding.views.{BindableView, ItemsSetView}
import org.scalajs.dom.raw.HTMLElement
import rx.core.{Rx, Var}
import org.denigma.binding.extensions._
import rx.ops._

import scala.collection.immutable.SortedSet

trait TextSelectionView extends SelectionView{
  type Item = Var[TextSelection]
  type ItemView = OptionView

  val items:Var[SortedSet[Item]]


  val onkeydown = Var(Events.createKeyboardEvent(),my("onkeydown"))
  val keyDownHandler = onkeydown.onChange(my("keydown_handler"))(event=>{
    if(input.now=="")
      event.keyCode match  {
        case LeftKey(_)=>
          //println(s"input position is ${position.now} and shift is ${positionShift.now}")
          moveLeft()
        case RightKey(_)=> moveRight()
        case BackspaceKey(_) => items.set(items.now.filterNot(i=>i.now.position==position.now)) //note it can be buggy
        case DeleteKey(_) => items.set(items.now.filterNot(i=>i.now.position==position.now)) //note it can be buggy
        case EnterKey(_) => //items() = items.now.ins
        case other=>
      }
  })

}

