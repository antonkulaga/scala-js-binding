package org.denigma.binding.views

import rx.{Ctx, Rx}
import org.denigma.binding.extensions._
import scala.collection.immutable._
//import rx.Ctx.Owner.voodoo
import rx.Ctx.Owner.Unsafe.Unsafe


trait ItemsSeqView extends CollectionView {

  val items: Rx[Seq[Item]]

  lazy val updates: Rx[SequenceUpdate[Item]] = items.updates

  protected def onMove(mv: Moved[Item]) = {
    val fr = itemViews.now(items.now(mv.from))
    val t = itemViews.now(items.now(mv.to))
    this.replace(t.viewElement, fr.viewElement)
  }

  /**
    * This function is very important as it monitors items updates
    */
  override protected def subscribeUpdates() = {
    template.hide()
    this.items.now.foreach(i => this.addItemView(i, this.newItemView(i)))
    updates.onChange(upd => {
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
      upd.moved.foreach(onMove)
    })
  }


}








