package org.denigma.controls.selection

import org.denigma.binding.binders.Events
import org.denigma.binding.views.{BindableView, ItemsSetView}
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.HTMLElement
import rx.core.{Rx, Var}
import org.denigma.binding.extensions._
import rx.ops._

import scala.collection.immutable.SortedSet

trait TextSelectionView extends SelectionView{
  type Item = Var[TextSelection]
  type ItemView = OptionView

  override def items:Var[SortedSet[Item]] //either val or lazy val

  val onkeydown = Var(Events.createKeyboardEvent(),my("onkeydown"))
  val keyDownHandler = onkeydown.onChange(my("keydown_handler"))(event=>{
    if(input.now=="")
      event.keyCode match  {
        case KeyCode.Left=>  moveLeft()
        case KeyCode.Right=> moveRight()
        case KeyCode.Backspace => items.set(items.now.filterNot(i=>i.now.position==position.now-1))
        case KeyCode.Delete => items.set(items.now.filterNot(i=>i.now.position==position.now))
        case KeyCode.Enter => //items() = items.now.ins
        case other=>
      }
  })

}

