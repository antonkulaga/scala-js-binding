package org.denigma.controls.charts

import org.denigma.binding.views.BindableView
import org.scalajs.dom._
import rx.core.Rx
import rx.ops._

import scala.collection.immutable._


trait ConnectedSeries {

  def series:List[Series]
}

case class StepSeries(title:String,
                      xMin:Double,xMax:Double, stepLength:Double,
                      style:LineStyles = LineStyles.default
                       )(fun:Double=>Point) extends PlotSeries
{

  lazy val length = xMax - xMin

  //lazy val stepLength = length / steps
  lazy val steps:Int = (length / stepLength).toInt

  val points:List[Point] = (for(
    i<- 0 until steps
  ) yield fun(xMin + i*stepLength) ).toList

}

case class LineSeries(title:String,
                        xMin:Double,xMax:Double,
                        style:LineStyles = LineStyles.default
                       )(fun:Double=>Point) extends PlotSeries
{
  val points:List[Point] = List(fun(xMin),fun(xMax))
  println(points.mkString(" "))
}

trait PlotSeries extends Series {

  val xMin:Double
  val xMax:Double

}

case class StaticSeries(title:String,points:List[Point],style:LineStyles = LineStyles.default) extends Series

trait Series
{
  val title:String
  val points:List[Point]
  val style:LineStyles
}

class SeriesView(elem:Element,series:Rx[Series],transform:Rx[Point=>Point],closed:Boolean = false) extends PathView(
  elem,
  Rx{series().points.map(transform())},
  series.map(s=>s.style),
  closed
  )
{
  val title = series.map(s=>s.title)
}

class PathView(val elem:Element, val points:Rx[List[Point]], style:Rx[LineStyles],closed:Boolean = true) extends BindableView {

  val strokeColor: rx.Rx[String] = style.map(s=>s.strokeColor)
  val strokeWidth: rx.Rx[Double] = style.map(s=>s.strokeWidth)
  val strokeOpacity: rx.Rx[Double] = style.map(s=>s.opacity)

  val path: rx.Rx[String] = points.map(points2Path)

  def points2Path(items:List[Point]): String = items match {
    case Point(sx,sy)::tail=> tail.foldLeft(s"M$sx $sy") {
      case (acc, Point(x,y))=> acc+s" L$x $y"
    } + (if(closed)" Z" else "")
    case Nil=> if(closed)" Z" else ""
  }


  /*
   M = moveto
   L = lineto
   H = horizontal lineto
   V = vertical lineto
   C = curveto
   S = smooth curveto
   Q = quadratic Bézier curve
   z = smooth quadratic Bézier curveto
   A = elliptical Arc
   Z = closepath*/
}
