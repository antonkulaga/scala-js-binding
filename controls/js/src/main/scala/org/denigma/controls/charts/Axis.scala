package org.denigma.controls.charts

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.scalajs.dom.Element
import org.scalajs.dom.raw.HTMLElement
import rx.core.{Rx, Var}
import rx.ops._

import scala.collection.immutable._

case class Tick(name: String, value: Double)
object Tick {
  def linear(value: Double): Tick =Tick(value.toString, value)

  def logarithmic(value: Double): Tick =Tick(value.toString, Math.log(value))
}

case class LinearScale(title: String, start: Double, end: Double, stepSize: Double, length: Double, inverted: Boolean = false) extends Scale
{

  override def step(value: Double): Double = value + stepSize

  lazy val scale: Double = length / (end - start)

  def inverse(value: Double): Double = end - value + start

  def coord(value: Double): Double = if(inverted) inverse(value) * scale else value * scale

  //real coord to chart coord (it implies that 0 is the same
  override def chartCoord(coord: Double): Double = if(inverted) inverse(coord / scale) else coord / scale + start
}

trait Scale
{
  val title: String
  val start: Double
  val length: Double
  val end: Double
  def step(value: Double): Double

  lazy val startCoord: Double = coord(start)
  lazy val endCoord: Double = coord(end)

  def coord(chartCoord: Double): Double
  def chartCoord(coord: Double): Double

  val ticks: scala.List[Double] = points(start, end)

  def points(current: Double, end: Double, dots: List[Double] = List.empty): List[Double]  =
    if (current<end) points(step(current), end, current::dots) else (end::dots).reverse
}

class AxisView(val elem: Element, scale: Rx[Scale], style: Rx[LineStyles])
  extends BindableView with ItemsSeqView
{

  override type Item = Var[Tick]
  override type ItemView = TickView
  
  val title = scale.map(_.title)
  //val start: rx.Rx[Double] = scale.map(_.start)
  //val end: rx.Rx[Double] = scale.map(_.end)

  val startCoord = scale.map(_.startCoord)
  val endCoord = scale.map(_.endCoord)
  val length = Rx{scale().length}
  val ticks = scale.map(_.ticks)
  //val inverted = scale.map(_.inverted)

  val strokeWidth = style.map(_.strokeWidth)
  val strokeColor = style.map(_.strokeColor)
  lazy val tickLength = Var(10.0)
  lazy val half = length.map(_/2)

  override val items: Rx[Seq[Item]] = Rx{
    val sc = scale()
    val its = ticks()
    its.map{case i=>
      val name = s"$i"
      val value =  sc.coord(i)
      val tick = new Tick(name,value)
      //println(sc.title+s" $tick")
      Var(tick)
    }
  }

  override def newItemView(item: Item): TickView = this.constructItemView(item){
    (e,m) => new TickView(e, item, tickLength, style).withBinder(v => new GeneralBinder(v))
  }

}

class TickView(val elem: Element, tick: Rx[Tick], val tickLength: Rx[Double], styles: Rx[LineStyles]) extends BindableView{

  val label: rx.Rx[String] = tick.map(t=>t.name)
  val value: rx.Rx[Double] = tick.map(_.value)

  val strokeColor: rx.Rx[String] = styles.map(s=>s.strokeColor)
  val strokeWidth: rx.Rx[Double] = styles.map(s=>s.strokeWidth)

  val labelPadding: rx.Rx[Double] = tickLength.map(_+15)

}

/*
class ValueView(val elem:HTMLElement,tick:Rx[Tick]) extends BindableView{
  val label = tick.map(t=>t.name)
  val value = tick.map(t=>t.value)
}*/
