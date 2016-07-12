package org.denigma.binding.views

import org.scalajs.dom.raw.Element
import rx.{Ctx, Rx}
import org.denigma.binding.extensions._
import scala.collection.immutable._
//import rx.Ctx.Owner.voodoo
import rx.Ctx.Owner.Unsafe.Unsafe


trait ItemsSeqView extends CollectionView {

  val items: Rx[Seq[Item]]

  lazy val zipped = items.zipped

  @inline protected def reDraw(curRev: List[Item], added: Set[Item], insertBefore: Element): Unit =  curRev match {
    case Nil =>
    case head :: tail if added.contains(head) =>
      val v = this.newItemView(head)
      insertItemView(head, v, insertBefore)
      reDraw(tail, added - head, v.viewElement)
    case head :: tail =>
      val view = itemViews.now(head)
      template.parentElement.insertBefore(view.viewElement, insertBefore)
      reDraw(tail, added, view.viewElement)
  }

  override protected def subscribeUpdates() = {
    template.hide()
    this.items.now.foreach(i => this.addItemView(i, this.newItemView(i)))
    zipped.onChange{
      case (from, to) if from == to => //do nothing
      case (prev, cur) if prev !=cur =>
        val removed = prev.diff(cur)
        for(r <- removed) removeItemView(r)
        val added = cur.toSet.diff(prev.toSet)
        val revCur = cur.toList.reverse
        reDraw(revCur, added, template)
    }
  }

}








