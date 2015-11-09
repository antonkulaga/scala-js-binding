package org.denigma.preview.charts

object VectorODESolver {
  implicit def betterArray(array: Array[Double]): BetterArray = new BetterArray(array)
}



trait VectorODESolver extends ODESolver {

  import VectorODESolver._

  /** Function type for derivative functions: f (t, y) where y is a scalar */
  type DerivativeV = (Double, Array[Double]) => Double


  def integrateVec(f: Array[DerivativeV], y0: Array[Double]): Array[Double] =
  {
    val tDelta  = tEnd - tStart  // time interval
    val steps = Math.round(tDelta / step).toInt
    var h = tDelta / steps // adjusted step size
    var ti    =  tStart  // initialize ith time ti to t0
    var y = y0  // initialize y = f(t) to y0
    for (i <- 1 to steps) {
      println("Y BEFORE: "+y.prettyPrinted())
      val (dTi, dH, dY) = computeDeltasVec(f, ti, h,  y)   // take the next step
      println(s"dTi = $dTi dH = $dH dY = ${dY.prettyPrinted()}")
      println(s"I is $i Y AFTER are ${y.prettyPrinted()}")
      ti = ti + dTi
      h = h + dH
      y = y + dY
    } // for

    y    // the value of the function at time t, y = f(t)
  } // integrateVV

  @inline def computeDeltaVec(f: DerivativeV, ti: Double, h: Double, y: Array[Double]): Double =
  {
    val a = h * f(ti, y)
    val b = h * f(ti + 0.5 * h, y + 0.5 * a)
    val c = h * f(ti + 0.5 * h, y + 0.5 * b)
    val d: Double = h * f(ti + h, y + c)
    (a + 2.0 * b + 2.0 * c + d) * sixth
  }

  @inline def computeDeltasVec(f: Array[DerivativeV], tiCur: Double, hCur: Double, y: Array[Double]): (Double, Double, Array[Double]) =
  {
    val (ti,h) = avoidSkippingEnd(tiCur, hCur, tEnd)
    val dY = new Array[Double](y.length)
    for (j <- y.indices)
    {
      dY(j) = computeDeltaVec(f(j), ti, h, y)
    }
    //if(dY.exists(d=>d.isNaN)) throw new Exception("DY IS NAN = "+dY.prettyPrinted())
    //if(dY.exists(d=>d.isNaN)) throw new Exception("DY IS INFINITY = "+dY.prettyPrinted())
    (h, h-hCur, dY)
  }
}

trait ODESolver{
  def tStart: Double = 0.0
  def tEnd: Double
  def step: Double = 1

  def epsilon = 1E-6

  lazy val tDelta: Double  = tEnd - tStart
  lazy val steps = (tDelta / step).toInt
  /** Function type for derivative functions: f (t, y) where y is a scalar
    */
  type Derivative = (Double, Double) => Double


  protected val sixth = 1.0 / 6.0    // one sixth

  def integrate (f: Derivative, y0: Double): Double =
  {
    var h = tDelta / steps // adjusted step size
    var ti = tStart     // initialize ith time ti to t0
    var y  = y0     // initialize y = f(t) to y0
    for (i <- 1 to steps) {
      val (dTi, dH, dY) = computeDeltas(f, ti, h,  y)   // take the next step
      ti = ti + dTi
      h = h + dH
      y = y + dY
    } // for

    y    // the value of the function at time t, y = f(t)
  }

  def interpolate(f: Derivative, y0: Double): Array[Double] =
  {
    var h = tDelta / steps // adjusted step size
    var ti    = tStart     // initialize ith time ti to t0
    var y     = y0
    val arr = new Array[Double](steps)// initialize y = f(t) to y0
    for(i <- 1 to steps) {
      val (dTi, dH, dY) = computeDeltas(f, ti, h,  y)   // take the next step
      ti = ti + dTi
      h = h+dH
      y = y +dY
      arr(i) = y
    } // for
    arr   // the value of the function at time t, y = f(t)
  }

  @inline def avoidSkippingEnd(ti: Double, h: Double, tEnd: Double): (Double, Double) =
    if (ti > tEnd) (tEnd, h - (ti - tEnd)) else (ti, h) // don't go past t

  @inline def computeDelta(f: Derivative, ti: Double, h: Double, y: Double): Double =
  {
    val a = h * f(ti, y)
    val b = h * f(ti + 0.5 * h, y + 0.5 * a)
    val c = h * f(ti + 0.5 * h, y + 0.5 * b)
    val d: Double = h * f(ti + h, y + c)
    (a + 2.0 * b + 2.0 * c + d) * sixth
  }

  @inline def computeDeltas(f: Derivative, tiCur: Double, hCur: Double, y: Double): (Double, Double, Double) = {
    val (ti, h) = avoidSkippingEnd(tiCur, hCur, tEnd)
    val dY = computeDelta(f,ti,h,y)
    (ti+h, h-hCur, dY)
  }

}