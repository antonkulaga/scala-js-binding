package org.denigma.controls.charts.ode

object VectorODESolver {
  implicit def betterArray(array: Array[Double]): BetterArray = new BetterArray(array)
}



trait VectorODESolver  {

  def tEnd: Double
  def tStart: Double
  def step: Double = 0.1
  protected val sixth = 1.0 / 6.0    // one sixth

  /** Function type for derivative functions: f (t, y) where y is a scalar */
  type VectorDerivative = (Double, Array[Double]) => Double

  import VectorODESolver._

  def integrateVec(f: Array[VectorDerivative], y0: Array[Double]): Array[Double] =
  {
    val tDelta  = tEnd - tStart  // time interval
    val steps = Math.round(tDelta / step).toInt
    var h = tDelta / steps // adjusted step size
    var ti    =  tStart  // initialize ith time ti to t0
    var y = y0  // initialize y = f(t) to y0
    for (i <- 1 to steps) {
      if (ti > tEnd) { h -= ti - tEnd; ti = tEnd }
      //println("BEFORE: Ys "+y.prettyPrinted())
      for (j <- y.indices) {  y = y + computeDeltaVec(f(j), ti, h, y)  }
      //println(s"AFTER: I is $i Ys are ${y.prettyPrinted()}")
      ti = ti + h
    }
    y
  }

  @inline def computeDeltaVec(f: VectorDerivative, ti: Double, h: Double, y: Array[Double]): Double =
  {
    val a = h * f(ti, y)
    val b = h * f(ti + 0.5 * h, y + 0.5 * a)
    val c = h * f(ti + 0.5 * h, y + 0.5 * b)
    val d: Double = h * f(ti + h, y + c)
    (a + 2.0 * b + 2.0 * c + d) * sixth
  }

}



trait ODESolver{
  def tStart: Double
  def tEnd: Double
  def step: Double = 0.1
  def epsilon: Double = 1E-6

  lazy val tDelta: Double  = tEnd - tStart
  lazy val steps = (tDelta / step).toInt
  /**
    * Function type for derivative functions: f (t, y) where y is a scalar
    */
  type Derivative = (Double, Double) => Double


  protected val sixth = 1.0 / 6.0    // one sixth

  def integrate (f: Derivative, y0: Double): Double =
  {
    var h = step // adjusted step size
    var ti = tStart     // initialize ith time ti to t0
    var y  = y0     // initialize y = f(t) to y0
    for (i <- 1 to steps) {
      if (ti > tEnd) { h -= ti - tEnd; ti = tEnd }
      val dY: Double = computeDelta(f, ti, h, y)   // take the next step
      y = y + dY
      ti = ti + h
    }
    y    // the value of the function at time t, y = f(t)
  }

  @inline def computeDelta(f: Derivative, ti: Double, h: Double, y: Double): Double =
  {
    val a = h * f(ti, y)
    val b = h * f(ti + 0.5 * h, y + 0.5 * a)
    val c = h * f(ti + 0.5 * h, y + 0.5 * b)
    val d: Double = h * f(ti + h, y + c)
    (a + 2.0 * b + 2.0 * c + d) * sixth
  }


}