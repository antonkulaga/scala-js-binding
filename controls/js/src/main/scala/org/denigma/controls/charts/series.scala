package org.denigma.controls.charts


import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.extensions._
import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.scalajs.dom.Element
import org.scalajs.dom.ext.Color
import rx.Rx
import rx.core.Var
import rx.ops._

case class Point(x:Double,y:Double)

object PointValue {

  def apply(x:Double,y:Double,name:String/*,radius:Double,color:Color*/):PointValue = PointValue(Point(x,y),name)

}
case class PointValue(point:Point,name:String="",radius:Double=1,color:Color = Color.Green)

trait Series extends BindableView {

  val scaleX: Rx[Scale]
  val scaleY: Rx[Scale]

}

trait PointSeries extends Series with ItemsSeqView {

  val scaleX: Rx[Scale]
  val scaleY: Rx[Scale]

  override type Item = Var[PointValue]
  override type ItemView = PointValueView
}

class PointValueView(val elem:Element,point:Var[PointValue]) extends BindableView {

  val mouseLeave = Var(Events.createMouseEvent())
  val mouseEnter = Var(Events.createMouseEvent())
  val showLabel = Var(false)

  val x = point.map(p=>p.point.x)
  val y = point.map(p=>p.point.y)
  val label = point.map(p=>p.name+s" [${x.now} : ${y.now}]")
  val hasName = point.map(p=>p.name!="")
  val color = point.map(p=>p.color.toString())
  val radius = point.map(p=>p.radius)

  mouseEnter.handler{
    showLabel() = true
  }
  mouseLeave.handler{
    showLabel() = false
  }

}

