package org.denigma.preview.charts

import org.denigma.controls.charts.{PlotSeries, Point, LineStyles, Series}
import scalation.dynamics.Derivatives._
import scalation.dynamics.RungeKutta._
import scalation.dynamics.{Integrator, RungeKutta}
import math.{abs, E, pow, round}
import scalation.linalgebra.VectorD
import scalation.util.Error


case class ODESeries(title:String,
                      xMin:Double,xMax:Double, yStart:Double, stepSize:Double = 0.1,
                      style:LineStyles = LineStyles.default
                       )(der:(Double,Double)=>Double) extends PlotSeries with Solver
{

  lazy val length = xMax - xMin
  lazy val steps = round(length / stepSize).toInt                // number of steps

  def compute(f: Derivative): Array[Point] =
    {
    var h     = length / steps.toDouble                      // adjusted step size
    var ti    = xMin                                         // initialize ith time ti to t0
    var y     = yStart
    val arr = new Array[Point](steps)// initialize y = f(t) to y0
    arr(0) = Point(xMin,yStart)

    for (i <- 1 to steps) {
      val (dTi,dH,dY) = super.computeDeltas(f,ti,h,xMax,y)                                            // take the next step
      ti = ti + dTi
      h = h+dH
      y = y +dY
      arr(i) = Point(ti,y)

    } // for
      //println("ARRAY IS "+arr.toList.mkString(" AND "))
    arr                          // the value of the function at time t, y = f(t)
  }


  val points:List[Point] = compute(der).toList

}