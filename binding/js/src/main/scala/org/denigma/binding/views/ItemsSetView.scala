package org.denigma.binding.views
import org.denigma.binding.extensions._
import rx.{Ctx, Rx}
import rx.Ctx.Owner.voodoo


import scala.collection.immutable._
trait ItemsSetView extends CollectionView{

  def items: Rx[SortedSet[Item]]

  lazy val updates: Rx[SetUpdate[Item]] = items.updates

  override protected def subscribeUpdates() = {
    template.hide()
    this.items.now.foreach(i => this.addItemView(i, this.newItemView(i)) ) //initialization of views
    updates.onChange(upd=>{
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
    })
  }

}
