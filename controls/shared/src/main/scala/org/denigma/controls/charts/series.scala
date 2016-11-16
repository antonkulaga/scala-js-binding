package org.denigma.controls.charts

import scala.collection.immutable.List


case class Point(x: Double, y: Double) {
  override def toString:String = s"[$x, $y]"
}


case class StepSeries(title: String,
                      xMin: Double, xMax: Double, stepLength: Double,
                      style: LineStyles = LineStyles.default
                     )(fun: Double => Point) extends Series
{

  lazy val length = xMax - xMin

  //lazy val stepLength = length / steps
  lazy val steps: Int = (length / stepLength).toInt

  val points: List[Point] = (for(
    i<- 0 until steps
  ) yield fun(xMin + i*stepLength) ).toList

}

case class LineSeries(title: String,
                      xMin: Double, xMax: Double,
                      style: LineStyles = LineStyles.default
                     )(fun: Double => Point) extends Series
{
  val points: List[Point] = List(fun(xMin), fun(xMax))
}


case class StaticSeries(title: String,
                        points: List[Point],
                        style: LineStyles = LineStyles.default) extends Series
{
  def withStrokeColor(color: String): StaticSeries = copy(style = style.copy(strokeColor = color))
  def withFillColor(color: String): StaticSeries = copy(style = style.copy(fill = color))

}

/**
  * Plot series to keep
  */
trait Series
{
  def title: String
  def points: List[Point]
  def style: LineStyles

  lazy val maxOpt = if(points.isEmpty) None else Some(Point(points.maxBy(p=>p.x).x, points.maxBy(p=>p.y).y))
  lazy val minOpt = if(points.isEmpty) None else Some(Point(points.minBy(p=>p.x).x, points.minBy(p=>p.y).y))

}
