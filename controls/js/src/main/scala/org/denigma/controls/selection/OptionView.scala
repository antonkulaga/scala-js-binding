package org.denigma.controls.selection

import org.denigma.binding.binders.Events
import org.denigma.binding.views._
import org.denigma.controls.models.TextOption
import org.scalajs.dom.Element
import rx._
import rx.Ctx.Owner.Unsafe.Unsafe
//import rx.Ctx.Owner.voodoo
import org.denigma.binding.extensions._

class OptionView(val elem: Element, item: Var[TextOption]) extends BindableView
{
  val label: Rx[String] = item.map(_.label)
  val value: Rx[String] = item.map(_.value)
  val position: Rx[Int] = item.map(_.position)
  val preselected:Rx[Boolean] = item.map(_.preselected)

  def onSelect():Unit = {
    fire(SelectTextOptionEvent(item, this, this))
  }

  val select = Var(Events.createMouseEvent())

  select.onChange{
    case ev=> onSelect()
  }
}

//

case class SelectTextOptionEvent(item: Var[TextOption], origin: BindableView, latest: BasicView) extends ViewEvent{

  override def withCurrent(cur: BasicView): SelectTextOptionEvent = {
    copy(latest = cur)
  }

}
