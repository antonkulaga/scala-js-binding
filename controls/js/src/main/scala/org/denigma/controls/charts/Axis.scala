package org.denigma.controls.charts

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.scalajs.dom.Element
import org.scalajs.dom.raw.HTMLElement
import rx.core.{Rx, Var}
import rx.ops._

import scala.collection.immutable._

case class Tick(name:String,value:Double)
object Tick {
  def linear(value:Double):Tick =Tick(value.toString,value)
  def logarithmic(value:Double):Tick =Tick(value.toString,Math.log(value))

}

case class LinearScale(start:Double,end:Double,stepSize:Double, inverted:Boolean = false) extends Scale{

  override def step(value: Double): Double = value+stepSize

  def coord(cord:Double) = cord
}

trait Scale
{
  val start:Double
  val end:Double
  val inverted:Boolean
  def step(value:Double):Double

  def inverse(value:Double) = end - value + start

  def coord(cord:Double):Double

  val ticks = points(start,end)

  def points(current:Double,end:Double,dots:List[Double] = List.empty):List[Double]  =
    if(current<end) points(step(current),end,current::dots) else (end::dots).reverse
}

class AxisView(val elem:Element,  scale:Rx[Scale], unit:String)
  extends BindableView with ItemsSeqView
{

  override type Item = Var[Tick]
  override type ItemView = TickView
  lazy val tickLength = Var(10.0)

  val start: rx.Rx[Double] = scale.map(_.start)
  val end: rx.Rx[Double] = scale.map(_.end)
  val ticks = scale.map(_.ticks)
  val inverted = scale.map(_.inverted)

  override val items:Rx[Seq[Item]] = Rx{
    val its = ticks()
    its.map{case i=>
      val name = s"$i $unit"
      val value =  if(inverted()) scale.now.inverse(i) else i
      Var(new Tick(name,value))
    }
  }


  override def newItem(item: Item) = this.constructItemView(item){
    (e,m)=>new TickView(e,item,tickLength).withBinder(v=>new GeneralBinder(v))
  }

}

class TickView(val elem:Element,tick:Rx[Tick],val length:Rx[Double]) extends BindableView{
  val label = tick.map(t=>t.name)
  val value = tick.map(_.value)

}

/*
class ValueView(val elem:HTMLElement,tick:Rx[Tick]) extends BindableView{
  val label = tick.map(t=>t.name)
  val value = tick.map(t=>t.value)
}*/
