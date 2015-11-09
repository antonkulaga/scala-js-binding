package org.denigma.controls.charts.ode

import org.denigma.controls.charts.{LineStyles, Series, Point}

case class ODESeries(title: String,
                      tStart: Double, tEnd: Double, yStart: Double,
                      override val step: Double = 0.5,
                      style:LineStyles = LineStyles.default
                       )(der: (Double, Double) => Double) extends Series with ODESolver
{

  def compute(f: (Double, Double) => Double): Array[Point] =
    {
      var h = step
      var ti    = tStart // initialize ith time ti to t0
      var y     = yStart
      val arr = new Array[Point](steps)// initialize y = f(t) to y0
      arr(0) = Point(ti, yStart)
      for(i <- 1 to steps) {
        if (ti > tEnd) { h -= (ti - tEnd); ti = tEnd }
        val dY = computeDelta(f, ti, h, y)   // take the next step
        y = y + dY
        arr(i) = Point(ti, y)
        ti = ti + h
      }
      arr
    }

  val points: List[Point] = compute(der).toList
}