package org.denigma.controls.selection

import org.denigma.binding.extensions._
import org.denigma.binding.views.ItemsSetView
import rx._
import rx.Ctx.Owner.Unsafe.Unsafe


trait SelectionView extends ItemsSetView{

  def my(str: String) = str+"_of_"+this.id //to name Vars for debugging purposes

  val input: Var[String] = Var("")

  val inputSize = input.map(i=>i.length+1) //to set size

  val positionShift = Var(0)

  val position: Rx[Int] = Rx{
    items().size + positionShift()
  }

  protected def moveLeft() = if(position.now > -1) positionShift.set(positionShift.now-1)

  protected def moveRight() = if(position.now<items.now.size) positionShift.set(positionShift.now+1)

  override protected def subscribeUpdates() = {
    template.style.display = "none"
    this.items.now.foreach(i=>this.addItemView(i,this.newItemView(i))) //initialization of views
    updates.onChange(upd=>{
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
    })
  }

}
