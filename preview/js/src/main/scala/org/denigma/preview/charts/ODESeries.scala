package org.denigma.preview.charts

import org.denigma.controls.charts.{LineStyles, PlotSeries, Point}

import scala.math.round

case class ODESeries(title:String,
                      xMin:Double, xMax:Double, yStart:Double, stepSize:Double = 1,
                      style:LineStyles = LineStyles.default
                       )(der:(Double, Double) => Double) extends PlotSeries with ODESolver
{


  def compute(f: Derivative): Array[Point] =
    {
    var h: Double = tDelta / steps.toDouble                      // adjusted step size
    var ti: Double = xMin                                         // initialize ith time ti to t0
    var y: Double = yStart
    val arr = new Array[Point](steps)// initialize y = f(t) to y0
    arr(0) = Point(xMin,yStart)

    for (i <- 1 to steps) {
      val (dTi: Double, dH: Double, dY: Double) = super.computeDeltas(f, ti, h, y)                                            // take the next step
      ti = ti + dTi
      h = h + dH
      y = y + dY
      arr(i) = Point(ti, y)
    }
    arr
  }

  val points: List[Point] = compute(der).toList

  override def tEnd: Double = xMax
}