package org.denigma.controls.selection

import org.denigma.binding.extensions._
import org.denigma.binding.views.ItemsSetView
import rx.core.{Rx, Var}
import rx.ops._

trait SelectionView extends ItemsSetView{

  def my(str:String) = str+"_of_"+this.id //to name Vars for debugging purposes

  val input: Var[String] = Var("","input_of_"+this.id)

  val positionShift = Var(0,my("positionShift"))

  val position: Rx[Int] = Rx{
    items().size+positionShift()
  }

  val order: Rx[String] = position.map(_.toString)

  //lazy val ordered: rx.Rx[List[(Item, Int)]] = items.map(its=>its.toList.zipWithIndex)

  protected def moveLeft() = if(position.now > -1) positionShift.set(positionShift.now-1)

  protected def moveRight() = if(position.now<items.now.size) positionShift.set(positionShift.now+1)

  override protected def subscribeUpdates() = {
    template.style.display = "none"
    this.items.now.foreach(i=>this.addItemView(i,this.newItem(i))) //initialization of views
    updates.onChange("ItemsUpdates")(upd=>{
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
    })
  }

}
