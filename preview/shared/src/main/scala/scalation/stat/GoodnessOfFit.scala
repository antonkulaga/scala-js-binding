
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.2
 *  @date    Sat Jan 26 22:05:46 EST 2013
 *  @see     LICENSE (MIT style license file).
 */

package scalation.stat

import math.{floor, round, sqrt}

import scalation.linalgebra.VectorD
import scalation.random.{Quantile, Variate}
import scalation.util.Error

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `GoodnessOfFit` class is used to fit data to probability distibutions.
 *  @param d          the sample data points
 *  @param dmin       the minimum value for d
 *  @param dmax       the maximum value for d
 *  @param intervals  the number of intervals for the data's histogram
 */
class GoodnessOfFit (d: VectorD, dmin: Double, dmax: Double, intervals: Int = 10)
      extends Error
{
    private val EPSILON = 1E-9                                  // number close to zero
    private val n       = d.dim                                 // number of sample data points
    private val ratio   = intervals / (dmax - dmin + EPSILON)   // intervals to data range ratio

    if (n < 5 * intervals) flaw ("constructor", "not enough data to fit distribution")

    private val histo = new Array [Int] (intervals)             // histogram
    for (i <- 0 until n) {
        val j = floor ((d(i) - dmin) * ratio).toInt
        if (0 <= j && j < intervals) histo(j) += 1              // add to count for interval j
        else println ("lost value = " + d(i))
    } // for

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Perform a Chi-square goodness of fit test, matching the histogram of the
     *  given data d with the random variable's probability function pf (pdf).
     *  @param rv  the random variate to test
     */
    def fit (rv: Variate): Boolean =
    {
        println ("-------------------------------------------------------------")
        println ("Test goodness of fit for " + rv.getClass.getSimpleName ())
        println ("-------------------------------------------------------------")

        var x    = 0.0            // x coordinate
        var o    = 0.0            // observed value: height of histogram
        var e    = 0.0            // expected value: pf (x)
        var chi2 = 0.0            // ChiSquare statistic
        var nz   = 0              // number of nonzero intervals

        for (j <- 0 until intervals) {
            x = j / ratio + dmin
            o = histo(j)
            e = round (n * rv.pf (x + .5) / ratio)
            if (e >= 4) { chi2 += (o - e)*(o - e) / e; nz += 1 }         // big enough
            println ("\thisto (" + x + ") = " + o + " : " + e + " ")
        } // for

        nz -= 1                              // degrees of freedom (dof) is one less
        if (nz < 2)  flaw ("fit", "insufficient degrees of freedom")
        if (nz > 49) nz = 49
        val cutoff = Quantile.chiSquareInv (0.95, nz)
        println ("\nchi2 = " + chi2 + " : chi2(0.95, " + nz + ") = " + cutoff)
        chi2 <= cutoff
    } // fit

} // GoodnessOfFit class


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // GoodnessOfFitTest object

