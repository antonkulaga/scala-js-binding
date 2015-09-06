package org.denigma.controls.selection

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.views._
import org.scalajs.dom.raw.HTMLElement
import rx.Rx
import rx.core.Var
import rx.ops._
import org.denigma.binding.extensions._

class OptionView(val elem:HTMLElement,item:Var[TextSelection],val params:Map[String,Any]) extends BindableView
{
  import rx.ops._
  val label: Rx[String] = item.map(_.label)
  val value: Rx[String] = item.map(_.value)
  val position: Rx[Int] = item.map(_.position)
  val order: Rx[String] = item.map(_.position.toString)

  def onSelect():Unit = {
    fire(SelectTextOption(item,this,this))
  }

  val select = Var(Events.createMouseEvent())

  select.onChange("onSelectClick",uniqueValue = true,skipInitial = true){
    case ev=> onSelect()
  }
}

case class SelectTextOption(item:Var[TextSelection],origin:ReactiveView,latest:BasicView) extends ViewEvent{

  override def withCurrent(cur: BasicView): SelectTextOption = {
    copy(latest = cur)
  }

}

class SelectOptionsView(val elem:HTMLElement,
                        val items: Rx[scala.collection.immutable.SortedSet[Var[TextSelection]]],
                        val params:Map[String,Any])
  extends ItemsSetView
{

  type Item = Var[TextSelection]

  type ItemView = OptionView

  val hasOptions: Rx[Boolean] = items.map{case ops=> ops.nonEmpty}

  override protected def warnIfNoBinders(asError:Boolean) = if(asError) super.warnIfNoBinders(asError)

  override def newItem(item: Item): OptionView = constructItemView(item){
    case (el,mp)=>new OptionView(el,item, mp).withBinder{new GeneralBinder(_)}
  }

}
