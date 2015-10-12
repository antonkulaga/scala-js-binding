package org.denigma.binding.views

import org.denigma.binding.binders.{Binder, BinderForViews}
import org.denigma.binding.extensions._
import rx.Rx
import rx.ops.{SetUpdate, Moved, SequenceUpdate}
import scala.collection.immutable._


trait ItemsSeqView extends CollectionView {

  val items:Rx[Seq[Item]]

  lazy val updates: Rx[SequenceUpdate[Item]] =items.updates

  protected def onMove(mv:Moved[Item]) = {
    val fr = itemViews(items.now(mv.from))
    val t = itemViews(items.now(mv.to))
    this.replace(t.viewElement,fr.viewElement)
  }

  override protected def subscribeUpdates() = {
    template.style.display = "none"
    this.items.now.foreach(i=>this.addItemView(i,this.newItem(i)))
    updates.onChange("ItemsUpdates")(upd=>{
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
      upd.moved.foreach(onMove)
    })
  }


}








