package org.denigma.controls.selection


import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.extensions._
import org.denigma.binding.views._
import org.scalajs.dom._
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.HTMLElement
import rx.Rx
import rx.core.Var
import rx.ops._

class OptionView(val elem:HTMLElement,item:Var[TextSelection]) extends BindableView
{
  val label: Rx[String] = item.map(_.label)
  val value: Rx[String] = item.map(_.value)
  val position: Rx[Int] = item.map(_.position)
  val order: Rx[String] = item.map(_.position.toString)
  val preselected:Rx[Boolean] = item.map(_.preselected)

  def onSelect():Unit = {
    fire(SelectTextOptionEvent(item,this,this))
  }

  val select = Var(Events.createMouseEvent())

  select.onChange("onSelectClick",uniqueValue = true,skipInitial = true){
    case ev=> onSelect()
  }
}


case class SelectTextOptionEvent(item:Var[TextSelection],origin:BindableView,latest:BasicView) extends ViewEvent{

  override def withCurrent(cur: BasicView): SelectTextOptionEvent = {
    copy(latest = cur)
  }

}
