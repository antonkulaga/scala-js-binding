package org.denigma.preview.charts

import scala.math._
import scalation.linalgebra.VectorD
import scalation.util.Error


object Solver extends Solver {

  def test() = {
    def derv1 (t: Double, y: Double) = 2.0 * t       // solution to differential equation is t^2
    val y0 = 0.0
    val t  = 6.0
    println ("\n==> at t = " + t + " y = " + integrate (derv1, y0, t))
    println ("\n==> t^2 = " + pow(6,2))
  }

}

trait VectorSolver extends Solver with Error {


  /** Function type for derivative functions: f (t, y) where y is a scalar
    */
  type DerivativeV = (Double, VectorD) => Double


  //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
  /** Compute y(t), a vector, governed by a system of differential equations using
    *  numerical integration of the derivative function f(t, y) using a 4th-order
    *  Runge-Kutta method to return the value of y(t) at time t.
    *  @param f     the array of derivative functions [f(t, y)] where y is a vector
    *  @param y0    the value of the y-function at time t0, y0 = y(t0)
    *  @param t     the time value at which to compute y(t)
    *  @param t0    the initial time
    *  @param step  the step size
    */
  def integrateVV (f: Array [DerivativeV], y0: VectorD, tEnd: Double,
                   tStart: Double = 0.0, step: Double = 0.01): VectorD =
  {
    val t_t0       = tEnd - tStart                                  // time interval
  val steps: Int = round(t_t0 / step).toInt             // number of steps
  var h          = t_t0 / steps.toDouble                   // adjusted step size
  var ti         = tStart                                      // initialize ith time ti to t0
  var y          = y0                                      // initialize y = f(t) to y0

    val a = new VectorD (y.dim)
    val b = new VectorD (y.dim)
    val c = new VectorD (y.dim)
    val d = new VectorD (y.dim)

    for (i <- 1 to steps) {

      val (dTi,dH,dY) = computeDeltasW(f,ti,h,tEnd,y)                                            // take the next step
      ti = ti + dTi
      h = h+dH
      y = y + dY
      ti += h                                              // take the next step
    } // for

    y         // the value of the function at time t, y = f(t)
  } // integrateVV


  @inline def computeDeltasW(f: Array [DerivativeV],tiCur:Double,hCur:Double,tEnd:Double, y:VectorD): (Double,Double,VectorD) =
  {
    val (ti,h) = avoidSkippingEnd(tiCur,hCur,tEnd)

    val a = new VectorD (y.dim)
    val b = new VectorD (y.dim)
    val c = new VectorD (y.dim)
    val d = new VectorD (y.dim)
    val dY = new VectorD (y.dim)
    for (j <- y.indices) a(j) = h * f(j)(ti, y)
    for (j <- y.indices) b(j) = h * f(j)(ti + 0.5*h, y + a(j)*0.5)
    for (j <- y.indices) c(j) = h * f(j)(ti + 0.5*h, y + b(j)*0.5)
    for (j <- y.indices) d(j) = h * f(j)(ti + h, y + c(j))
    for (j <- y.indices) dY(j) = (a(j) + 2.0*b(j) + 2.0*c(j) + d(j)) * sixth

    if (abs (y(0)) > ovf) flaw ("integrateVV", "probable overflow since y = " + y)
    //if (i % 1000 == 0) println("integrate: iteration " + i + " ti = " + ti + " y = " + y)
    (h,h-hCur,dY)
  }
}

trait Solver extends Error{


  /** Function type for derivative functions: f (t, y) where y is a scalar
    */
  type Derivative = (Double, Double) => Double


  protected val sixth = 1.0 / 6.0                           // one sixth
  protected val ovf   = Double.MaxValue / 10.0              // too big, may overflow

  //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
  /** Compute y(t) governed by a differential equation using numerical integration
    *  of the derivative function f(t, y) using a 4th-order Runge-Kutta method to
    *  return the value of y(t) at time t.
    *  @param f     the derivative function f(t, y) where y is a scalar
    *  @param y0    the value of the y-function at time t0, y0 = y(t0)
    *  @param tEnd     the time value at which to compute y(t)
    *  @param tStart    the initial time
    *  @param step  the step size
    */
  def integrate (f: Derivative, y0: Double, tEnd: Double,
                 tStart: Double = 0.0, step: Double = 0.01): Double =
  {
    val deltaT  = tEnd - tStart                                     // time interval
  val steps = round(deltaT / step).toInt                // number of steps
  var h     = deltaT / steps.toDouble                      // adjusted step size
  var ti    = tStart                                         // initialize ith time ti to t0
  var y     = y0                                         // initialize y = f(t) to y0

    for (i <- 1 to steps) {
      val (dTi,dH,dY) = computeDeltas(f,ti,h,tEnd,y)                                            // take the next step
      ti = ti + dTi
      h = h+dH
      y = y +dY
    } // for

    y                           // the value of the function at time t, y = f(t)
  }

  def interpolate(f: Derivative, y0: Double, tEnd: Double,
                  tStart: Double = 0.0, step: Double = 0.01): Array[Double] =
  {
    val deltaT  = tEnd - tStart                                     // time interval
  val steps = round(deltaT / step).toInt                // number of steps
  var h     = deltaT / steps.toDouble                      // adjusted step size
  var ti    = tStart                                         // initialize ith time ti to t0
  var y     = y0
    val arr = new Array[Double](steps)// initialize y = f(t) to y0

    for (i <- 1 to steps) {
      val (dTi,dH,dY) = computeDeltas(f,ti,h,tEnd,y)                                            // take the next step
      ti = ti + dTi
      h = h+dH
      y = y +dY
      arr(i) = y
    } // for
    arr                          // the value of the function at time t, y = f(t)
  }

  @inline def avoidSkippingEnd(ti:Double,h:Double,tEnd:Double):(Double,Double) = if (ti > tEnd)   (tEnd,h - ti - tEnd)  else (ti,h)// don't go past t

  @inline def computeDeltas(f: Derivative,tiCur:Double,hCur:Double,tEnd:Double, y:Double): (Double,Double,Double) = {
    val (ti,h) = avoidSkippingEnd(tiCur,hCur,tEnd)

    val a = h * f(ti, y)
    val b = h * f(ti + 0.5 * h, y + 0.5 * a)
    val c = h * f(ti + 0.5 * h, y + 0.5 * b)
    val d = h * f(ti + h, y + c)
    val dY = (a + 2.0 * b + 2.0 * c + d) * sixth

    if (abs(y) > ovf) flaw("integrate", "probable overflow since y = " + y)
    //if (i % 1000 == 0) println("integrate: iteration " + i + " ti = " + ti + " y = " + y)
    (h,h-hCur,dY)
  }
}