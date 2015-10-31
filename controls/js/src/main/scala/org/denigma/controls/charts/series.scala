package org.denigma.controls.charts


import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.extensions._
import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.scalajs.dom.Element
import org.scalajs.dom.ext.Color
import rx._
import rx.core.{Rx, Var}
import rx.ops._

case class Point(x:Double,y:Double)

object PointValue {

  def apply(x:Double,y:Double,name:String/*,radius:Double,color:Color*/):PointValue = PointValue(Point(x,y),name)

}
case class PointValue(point:Point,name:String="",radius:Double=1,color:Color = Color.Green)

trait Plot extends BindableView {

  val scaleX: Rx[Scale]
  val scaleY: Rx[Scale]

  def paddingX:Rx[Double]
  def paddingY:Rx[Double]

  lazy val left = paddingX
  lazy val top = paddingY

  lazy val right = Rx{left() + scaleX().length}
  lazy val bottom = Rx{top() + scaleY().length}
  lazy val width  =  Rx(right()+paddingX()) //width of whole SVG
  lazy val height  = Rx(bottom()+paddingY()) //height of whole SVG


  def position(point:Point) = point.copy(x = scaleX.now.coord(point.x),y = scaleY.now.coord(point.y))

  lazy val zeroX = Rx{ scaleX().startCoord + top()}
  lazy val zeroY = Rx{ scaleY().startCoord  + left()}

}

trait PointPlot extends Plot with ItemsSeqView {

  override type Item = Var[PointValue]
  override type ItemView = PointValueView
}

class PointValueView(val elem:Element,point:Var[PointValue], style:Rx[LineStyles]) extends BindableView {


  val lineColor = style.map(s=>s.strokeColor)
  val strokeWidth = style.map(s=>s.strokeWidth)

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

