package org.denigma.preview.tabs

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.views.{CollectionSeqView, BindableView}
import org.scalajs.dom.Element
import rx._
import rx.Ctx.Owner.Unsafe.Unsafe

import org.denigma.binding.extensions._

import scala.collection.immutable.Seq

case class TabItem(label: String, content: String) // content: Element)

object TabItemView {

  case class SimpleTabItemView(elem: Element, item: Rx[TabItem], selection: Var[Option[Rx[TabItem]]]) extends TabItemView

  def apply(elem: Element, item: Rx[TabItem], selection: Var[Option[Rx[TabItem]]]): TabItemView = SimpleTabItemView(elem, item, selection)
}


trait TabItemView extends BindableView {


  val item: Rx[TabItem]
  val selection: Var[Option[Rx[TabItem]]]

  val label: rx.Rx[String] = item.map(_.label)

  val content: rx.Rx[String] = item.map(_.content)

  lazy val active: Rx[Boolean] = Rx{
    val sel = selection()
    sel.isDefined && sel.get.now == item()
  }

  val onClick = Var(Events.createMouseEvent())
  onClick.triggerLater{
    selection() = Some(this.item)
  }
}


class TabsContentView(val elem: Element, val items: Rx[Seq[Rx[TabItem]]], val active: Var[Option[Rx[TabItem]]]) extends BasicTabsView

class TabsView(val elem: Element, val items: Rx[Seq[Rx[TabItem]]]) extends BasicTabsView{

  protected def defaultContent = ""
  protected def defaultLabel = ""

  val active: Var[Option[Item]] = Var(None)

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
        if (active.now.isEmpty && items.now.nonEmpty) active() = items.now.headOption
    }
    if (active.now.isEmpty && items.now.nonEmpty) active() = items.now.headOption  //TODO: refactor
  }

  override lazy val injector = defaultInjector
    .register("content"){
      case (el, args) =>  new TabsContentView(el, items, active).withBinder(new GeneralBinder(_))
    }
}

trait BasicTabsView extends CollectionSeqView {
  type Item = Rx[TabItem]
  type ItemView = TabItemView
  def active: Var[Option[Item]]

  override def newItemView(item: Item): ItemView = this.constructItemView(item){
    case (el, mp) => TabItemView(el, item, active).withBinder(new GeneralBinder(_))
  }
}
