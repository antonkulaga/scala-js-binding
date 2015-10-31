
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.2
 *  @date    Fri Jan 29 18:36:48 EST 2010
 *  @see     LICENSE (MIT style license file).
 */

package scalation.dynamics

import math.exp

import scalation.linalgebra.{Eigenvalue, Eigenvector, MatrixD, VectorD}
import scalation.util.Error

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `LinearDiffEq` class may be used for solving a system of linear differential
 *  equations that are ordinary and first-order with constant coefficients of the form
 *  <p>
 *  d/dt y(t) = a * y(t)
 *  <p>
 *  'y(t)' is the vector function of time and 'a' is the coefficient matrix.  The
 *  initial value vector 'y0 = y(0)' must also be given.  Note, higher-order differential
 *  equations may be converted to first-order by introducing additional variables.
 *  The above equation is the homogeneous case.
 *  Caveats: the following cases are not currently handled:
 *  (i) The non-homogeneous equation: d/dt y(t) = a * y(t) + f(t).
 *  (ii) Complex or repeated eigenvalues.
 *  @param a   the coefficient matrix
 *  @param y0  the initial value vector
 */
class LinearDiffEq (a: MatrixD, y0: VectorD)
      extends Error
{
     {
         if (a.dim2 != y0.dim) flaw ("constructor", "incompatible dimensions")
     } // primary constructor

     /** Vector of eigenvalues
      */
     private val e = (new Eigenvalue (a)).getE

     /** Matrix of eigenvectors
      */
     private val v = (new Eigenvector (a, e)).getV

     /** Vector of constants
      */
     private val c = v.solve (y0)

     /** Matrix of tranformed/final constants
      */
     private val k = v ** c

     //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     /** Apply the exponential 'exp' function to each element of a vector.
      *  @param v  the vector to apply the exp function to
      */
     def expV (v: VectorD): VectorD =
     {
         val z = new VectorD (v.dim)
         for (i <- 0 until z.dim) z(i) = exp (v(i))
         z
     } // expV

     //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     /** Evaluate the solution for y(t) at time t.
      *  @param t  the time point
      */
     def eval (t: Double): VectorD = k * expV (e * t)

     //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     /** Print the solution to the differential equation.
      */
     def print
     {
         println ("---------------------------------------")
         println ("System of Linear Differential Equations")
         println ("Solve: y(t)' = a * y(t) where y(0) = y0")
         println ("coefficient matrix    a  = " + a)
         println ("initial state vector y0 = " + y0)
         println ("eigenvalue vector     e  = " + e)
         println ("eigenvector matrix    v  = " + v)
         println ("constant vector       c  = " + c)
         println ("constant matrix       k  = " + k)
         println ("---------------------------------------")
     } // print

} // LinearDiffEq class


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // LinearDiffEqTest object

