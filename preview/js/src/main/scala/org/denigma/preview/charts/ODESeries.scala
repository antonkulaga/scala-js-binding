package org.denigma.preview.charts

import org.denigma.controls.charts.{PlotSeries, Point, LineStyles, Series}

case class ODESeries (title:String,
                      xMin:Double,xMax:Double, stepLength:Int,
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