package org.denigma.preview


import java.util.Date

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.{BindableView, ItemsSetView}
import org.scalajs.dom.raw.Element
import rx.core.Var

import scala.collection.immutable.SortedSet

case class Device(name: String = "undefined", port: String)

object Measurement
{
  implicit val ordering = new Ordering[Measurement]{
    override def compare(x: Measurement, y: Measurement): Int = {
      x.date.compareTo(y.date) match {
        case 0 if x!=y =>  -1
        case other: Any => other
      }
    }
  }

  implicit val varOrdering = new Ordering[Var[Measurement]]{
    override def compare(x: Var[Measurement], y: Var[Measurement]): Int = {
      ordering.compare(x.now, y.now)
    }
  }
}


case class Measurement(sample: Sample = Sample("unknown", "unknown"), diode: String = "unknown", value: Double, date: Date = new Date())

case class Sample(name: String, Description: String = "")

import rx.ops._
class MeasurementView(val elem: Element, measurement: Var[Measurement]) extends BindableView
{
  val sample = measurement.map(m => m.sample.name)
  val datetime = measurement.map(m => m.date.getTime.toString)
  val diode = measurement.map(m => m.diode)
  val value = measurement.map(m => m.value.toString)
}

class Experiments(val elem: Element) extends BindableView with ItemsSetView
{
  override type Item = Var[Measurement]

  override type ItemView = MeasurementView

  override val items: Var[SortedSet[Item]] =
    Var(SortedSet(
      Var(Measurement(Sample("sample1"), "diode1", 3004.0)),
      Var(Measurement(Sample("sample2"), "diode2", 3030.0)),
      Var(Measurement(Sample("sample3"), "diode3", 3020.0)),
      Var(Measurement(Sample("sample4"), "diode4", 3010.0))
    ))

  override def newItem(item: Item): MeasurementView = this.constructItemView(item){
    case (el, mp) => new ItemView(el, item).withBinder(new GeneralBinder(_))
  }

}