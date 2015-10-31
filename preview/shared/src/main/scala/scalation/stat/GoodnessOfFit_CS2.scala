
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller, Mustafa Nural
 *  @version 1.2
 *  @date    Mon Oct 19 18:53:29 EDT 2015
 *  @see     LICENSE (MIT style license file).
 *-----------------------------------------------------------------------------
 *  `GoodnessOfFit_CS2`: Chi-square goodness of fit test for equal probability intervals
 *  @see also `GoodnessOfFit_CS2`, `GoodnessOfFit_KS`
 */

package scalation.stat

import scala.math.sqrt

import scalation.linalgebra.VectorD
import scalation.random.Quantile.chiSquareInv
import scalation.random.{Distribution, Parameters, Quantile}
import scalation.util.Error

import GoodnessOfFit_CS._

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `GoodnessOfFit__CS2` class is used to fit data to probability distibutions.
 *  Suggestions: each interval should have 'E_i = n*p_i >= 5' and intervals >= sqrt (n).
 *  It uses the Chi-square goodness of fit test with equal probability intervals.
 *  @see www.eg.bucknell.edu/~xmeng/Course/CS6337/Note/master/node66.html
 *  Compute the following for each interval and sum over all intervals.
 *  <p>
 *      (O_i - E_i)^2 / E_i
 *  <p>
 *  where O_i and E_i are the observed and expected counts for interval 'i', respectively. 
 *  @param d             the sample data points
 *  @param dmin          the minimum value for d
 *  @param dmax          the maximum value for d
 *  @param iCDF          the inverse Cummulative Distribution Function
 *  @param parms         the parameters for the ICDF
 *  @param intervals     the number of intervals for the data's histogram
 *  @param makeStandard  whether to transform the data to zero mean and unit standard deviation
 */
class GoodnessOfFit__CS2 (private var d: VectorD, dmin: Double, dmax: Double, iCDF: Distribution,
                      parms: Parameters = null, intervals: Int = 10, makeStandard: Boolean = true)
      extends Error
{
    private val DEBUG   = true                                     // debug flag
    private val EPSILON = 1E-9                                     // number close to zero
    private val n       = d.dim                                    // number of sample data points
    private val endPt   = equalProbabilityInterval (intervals)     // for interval endpoints
    if (DEBUG) println ("endPt = " + endPt)

    if (makeStandard) d = d.standardize
    if (n < 5 * intervals) flaw ("constructor", "not enough data to fit distribution")

    private val histo = new Array [Int] (intervals)                // histogram
    for (i <- 0 until n) {
        val j = endPt.indexWhere (d(i) < _)                        // first index where d(i) < endpoint
        if (0 <= j && j < intervals) histo(j) += 1                 // add to count for interval j
        else println ("lost value = " + d(i))
    } // for

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Perform a Chi-square goodness of fit test, matching the histogram of the
     *  given data 'd' with the random variable's probability function pf (pdf).
     *  @param met  the discrepancy metric to use (defaults to pearson)
     */
    def fit (met: Metric = pearson): Boolean =
    {
        println ("-------------------------------------------------------------")
        println ("Test goodness of fit for " + iCDF.getClass.getSimpleName ())
        println ("-------------------------------------------------------------")

        var x    = 0.0              // x coordinate
        var o    = 0.0              // observed value: height of histogram
        val e    = n / intervals    // "equal probability" expected value: pf (x)
        var chi2 = 0.0              // ChiSquare statistic
        var nz   = 0                // number of nonzero intervals

        for (j <- 0 until intervals) {
            x = endPt(j)
            o = histo(j)
            if (e >= 4) { chi2 += met (o, e); nz += 1 }              // big enough
            println ("\thisto (" + x + ") = " + o + " : " + e + " ")
        } // for

        nz -= 1                              // degrees of freedom (dof) is one less
        if (nz < 2)  flaw ("fit", "insufficient degrees of freedom")
        if (nz > 49) nz = 49
        val cutoff = Quantile.chiSquareInv (0.95, nz)
        println ("\nchi2 = " + chi2 + " : chi2(0.95, " + nz + ") = " + cutoff)
        chi2 <= cutoff
    } // fit

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Determine the interval endpoint values for the "equal probability" interval case.
     *  @param intervals  the number of intervals
     */
    def equalProbabilityInterval (intervals: Int): VectorD =
    {
        VectorD (for (y <- 1 to intervals) yield iCDF (y / intervals.toDouble, parms))
    } // equalProbabilityInterval

} // GoodnessOfFit__CS2 class


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // GoodnessOfFit__CS2Test object

