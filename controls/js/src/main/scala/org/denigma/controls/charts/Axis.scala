package org.denigma.controls.charts

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.{BindableView, CollectionSeqView, CollectionSortedMapView}
import org.scalajs.dom
import org.scalajs.dom.Element
import rx._
//import rx.Ctx.Owner.voodoo
import rx.Ctx.Owner.Unsafe.Unsafe


import scala.collection.immutable._

case class Tick(name: String, value: Double)

object Tick {

  def linear(value: Double): Tick = Tick(value.toString, value)

  def logarithmic(value: Double): Tick = Tick(value.toString, Math.log(value))
}

case class LinearScale(title: String, start: Double, end: Double, stepSize: Double, length: Double, inverted: Boolean = false, precision:Int = 3) extends WithLinearScale
{

  if(stepSize > Math.abs(start - end)) dom.console.error(s"stepSize is larger then ")

  override def points(current: Double, end: Double, dots: List[Double] = List.empty): List[Double]  = {
    val tick = step(current)
    if (current<end) points(truncateAt(tick, precision), end, current::dots) else (truncateAt(end, precision)::dots).reverse
  }

  /**
    *
    * @param max maximum value of the point coordinate
    * @param stretchMult makes end strechMult times more then maximum value
    * @param shrinkMult shrinks the scale if maximum is much larger then end
    * @return
    */
  def stretched(max: Double, stretchMult: Double = 1.1, shrinkMult: Double = -1): LinearScale = if(max > end) {
    val newEnd = max * stretchMult
    val st = Math.abs(newEnd - start) / (ticks.length - 2)
    this.copy(end = newEnd, stepSize = st)
  } else if( shrinkMult > 0 && Math.abs(max - start) > 0.0 && end > max * shrinkMult){
    val newEnd = max
    val st = Math.abs(newEnd - start) / (ticks.length - 2)
    this.copy(end = newEnd, stepSize = st)
  } else this //does not change anything

}

trait WithLinearScale extends Scale {

  def stepSize: Double

  def inverted: Boolean

  override def step(value: Double): Double = value + stepSize

  lazy val scale: Double = length / Math.abs(end - start)

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

  /**
    * Cuts values of ticks with some precision
 *
    * @param n
    * @param p
    * @return
    */
  def truncateAt(n: Double, p: Int): Double = if(p>0) { val s = math pow (10, p); (math floor n * s) / s } else n

}
/*
class AxisView(val elem: Element, scale: Rx[Scale], style: Rx[LineStyles])
  extends BindableView with CollectionSeqView
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

*/

class AxisView(val elem: Element, scale: Rx[Scale], style: Rx[LineStyles])
  extends BindableView with CollectionSortedMapView
{

  override type Key = Int
  override type Value = Tick
  override type ItemView = TickView

  val title = scale.map(_.title)

  val startCoord = scale.map(_.startCoord)
  val endCoord = scale.map(_.endCoord)
  val length = scale.map(_.length)

  val strokeWidth = style.map(_.strokeWidth)
  val strokeColor = style.map(_.strokeColor)
  lazy val tickLength = Var(10.0)
  lazy val half = length.map(_/2)

  override def items: Rx[SortedMap[Int, Tick]] = scale.map{ sc =>
    val list: List[(Int, Tick)] = sc.ticks.zipWithIndex.map{ case (tk, index) => (index, Tick(tk.toString, sc.coord(tk)))}
    SortedMap[Int, Tick](list:_*)
  }

  override def updateView(view: ItemView, key: Int, old: Tick, current: Tick): Unit = {
    view.tick() = current
  }

  override def newItemView(key: Int, value: Tick): ItemView = this.constructItemView(key){
    case (el, _) => new TickView(el, Var(value), tickLength, style).withBinder(v=> new GeneralBinder(v))
  }
}

class TickView(elem: Element, val tick: Var[Tick], tickLength: Rx[Double], styles: Rx[LineStyles]) extends BasicTickView(elem, tick, tickLength, styles)




class BasicTickView(val elem: Element, tick: Rx[Tick], val tickLength: Rx[Double], styles: Rx[LineStyles]) extends BindableView{

  val label: rx.Rx[String] = tick.map(t=>t.name)
  val value: rx.Rx[Double] = tick.map(_.value)

  val strokeColor: rx.Rx[String] = styles.map(s=>s.strokeColor)
  val strokeWidth: rx.Rx[Double] = styles.map(s=>s.strokeWidth)

  val labelPadding: rx.Rx[Double] = tickLength.map(_+15)

}