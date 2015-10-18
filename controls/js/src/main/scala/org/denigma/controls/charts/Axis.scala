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

object LinearScale {
  def apply(start:Rx[Double],end:Rx[Double],stepSize:Rx[Double])  = new LinearScale(start,end,stepSize)
  def apply(start:Double,end:Double,stepSize:Double)  = new LinearScale(Var(start),Var(end),Var(stepSize))

}
class LinearScale(val start:Rx[Double],val end:Rx[Double],stepSize:Rx[Double]) extends Scale{

  override def step(value: Double): Double = value+stepSize.now

}
trait Scale
{
  val start:Rx[Double]
  val end:Rx[Double]
  def step(value:Double):Double
  //def tick(value:Double):Tick

  val items = Rx{  points(start(),end())  }

  def points(current:Double,end:Double,dots:List[Double] = List.empty):List[Double]  =
    step(current) match {
      case v if v<end => points(v,end,v::dots)
      case other=> (end::dots).reverse
    }
}

class AxisView(val elem:Element,  scale:Scale, unit:String)
  extends BindableView with ItemsSeqView{

  override type Item = Var[Tick]
  override type ItemView = TickView
  lazy val tickLength = Var(10.0)

  override val items:Rx[Seq[Item]] = scale.items.map(_.map(i=>Var(new Tick(s"$i $unit",i))))

  //println(s"Axis works with \n${items.now.map(_.now.value).toList.mkString(" | ")}")

/*
  /**
   * Adds subscription
   */
  override protected def subscribeUpdates(): Unit = {

  }*/

  override def newItem(item: Item) = this.constructItemView(item){
    (e,m)=>new TickView(e,item,tickLength).withBinder(v=>new GeneralBinder(v))
  }

}

class TickView(val elem:Element,tick:Rx[Tick],val length:Rx[Double]) extends BindableView{
  val label = tick.map(t=>t.name)
  val value = tick.map(_.value)
  //println(cord2.now)

}

/*
class ValueView(val elem:HTMLElement,tick:Rx[Tick]) extends BindableView{
  val label = tick.map(t=>t.name)
  val value = tick.map(t=>t.value)
}*/
