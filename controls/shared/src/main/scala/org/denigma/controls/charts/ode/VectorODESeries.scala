package org.denigma.controls.charts.ode

import org.denigma.controls.charts._


case class XYSeries(title:String, x: Int, y: Int, style: LineStyles = LineStyles.default)
                   (equations: ODEs, initial: Array[Double],fun: Point=>Point) extends Series
{
  override val points: List[Point] = {
    require(x < initial.length && y < initial.length, "XY should be withing results of the equations")
    val result = equations.compute(initial)
    result.map{
      case (ti ,  value)=> Point(value(x),value(y))
    }.toList
  }
}

/*
object PartialSeries {object PointValue {

  def apply(x: Double, y: Double, name: String/*,radius:Double,color:Color*/): PointValue = PointValue(Point(x, y), name)

}
*/


case class PartialSeries(title:String, x: Int, y: Int, style: LineStyles = LineStyles.default) (equations: ODEs, initial: Array[Double]) extends Series{
  override val points: List[Point] = {
    require(x < initial.length && y < initial.length, "XY should be withing results of the equations")
    val result = equations.compute(initial)
    result.map{
      case (ti ,  value)=> Point(value(x),value(y))
    }.toList
  }
}

object ODEs {

  def apply(start: Double, end: Double, step: Double)(devs: Array[(Double, Array[Double]) => Double]): ODEs = new ODEs{

    override def tEnd: Double = end

    override def tStart: Double = start

    val derivatives = devs
  }

}

trait ODEs extends VectorODESolver
{

  val derivatives: Array[(Double, Array[Double]) => Double]

  def compute(y0: Array[Double]): Array[(Double, Array[Double])] =
  {
    val tDelta  = tEnd - tStart  // time interval
    val steps = Math.round(tDelta / step).toInt
    val result = new Array[(Double, Array[Double])](steps)
    var h = tDelta / steps // adjusted step size
    var ti    =  tStart
    result(0) = ti -> y0
    for (i <- 1 to steps) {if (ti > tEnd) { h -= ti - tEnd; ti = tEnd }
      val y  = result(i -1)._2.clone()
      for (j <- y.indices) {  y(j) = y(j) + computeDeltaVec(derivatives(j), ti, h, y)  }
      result(i) = (ti,y)
      ti = ti + h
    }
    result
  }

  def computeXY(initial: Array[Double], xi: Int, yi: Int): List[Point] = {
    require(xi < initial.length && yi < initial.length, "XY should be withing results of the equations")
    this.compute(initial).map{ case (ti, value)=> Point(value(xi), value(yi))}.toList
  }

  def computeAll(initial: Array[Double], xi: Int, yi: Int): Array[Array[Point]] = this.compute(initial) match {
    case arr if arr.isEmpty => Array.empty
    case arr: Array[(Double, Array[Double])] =>
      val len = arr(0)._2.length
      val result = new Array[Array[Point]](len)
      for{ i <- 0 until len  } result(i) =  new Array[Point](arr.length)
      for{
        i <- arr.indices
        (ti, value) = arr(i)
        j <- value.indices
      } result(j)(i) = Point(ti,value(j))
      result
  }

}