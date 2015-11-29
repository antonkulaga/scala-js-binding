package org.denigma.controls.tabs

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.views.{ItemsSeqView, BindableView}
import org.scalajs.dom.Element
import rx.core.{Var, Rx}
import rx.ops._
import org.denigma.binding.extensions._

import scala.collection.immutable.Seq

case class TabItem(label: String, content: String) // content: Element)

case class TabItemView(elem: Element, item: Rx[TabItem], selection: Var[Option[Rx[TabItem]]]) extends BindableView {
  val content = item.map(_.content)
  val label = item.map(_.label)

  val active = Rx{
    val sel = selection()
    sel.isDefined && sel.get.now == item()
  }

  val onClick = Var(Events.createMouseEvent())
  onClick.handler{
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
    updates.onChange("ItemsUpdates")(upd => {
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
      upd.moved.foreach(onMove)
      if (active.now.isEmpty && items.now.nonEmpty) active() = items.now.headOption
    })
    if (active.now.isEmpty && items.now.nonEmpty) active() = items.now.headOption  //TODO: refactor
  }

  override lazy val injector = defaultInjector
    .register("content"){
      case (el, args) =>  new TabsContentView(el, items, active).withBinder(new GeneralBinder(_))
    }
}

trait BasicTabsView extends ItemsSeqView {
  type Item = Rx[TabItem]
  type ItemView = TabItemView
  def active: Var[Option[Item]]

  override def newItemView(item: Item): ItemView = this.constructItemView(item){
    case (el, mp) => TabItemView(el, item, active).withBinder(new GeneralBinder(_))
  }
}
