package org.denigma.controls.selection


import org.denigma.binding.binders.Events
import org.denigma.binding.extensions._
import org.denigma.binding.views._
import org.denigma.controls.models.TextOption
import org.scalajs.dom.raw.HTMLElement
import rx.Rx
import rx.core.Var
import rx.ops._

class OptionView(val elem:HTMLElement,item:Var[TextOption]) extends BindableView
{
  val label: Rx[String] = item.map(_.label)
  val value: Rx[String] = item.map(_.value)
  val position: Rx[Int] = item.map(_.position)
  val preselected:Rx[Boolean] = item.map(_.preselected)

  def onSelect():Unit = {
    fire(SelectTextOptionEvent(item,this,this))
  }

  val select = Var(Events.createMouseEvent())

  select.onChange("onSelectClick",uniqueValue = true,skipInitial = true){
    case ev=> onSelect()
  }
}

//

case class SelectTextOptionEvent(item:Var[TextOption],origin:BindableView,latest:BasicView) extends ViewEvent{

  override def withCurrent(cur: BasicView): SelectTextOptionEvent = {
    copy(latest = cur)
  }

}
