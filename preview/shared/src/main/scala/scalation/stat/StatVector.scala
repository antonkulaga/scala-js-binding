
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.2
 *  @date    Wed Aug 26 18:41:26 EDT 2009
 *  @see     LICENSE (MIT style license file).
 */

package scalation.stat

import math.{ceil, sqrt}

import scalation.linalgebra.{VectorD, VectorI}
import scalation.math.double_exp
import scalation.random.Quantile.studentTInv
import scalation.util.SortingD.imedian

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `StatVector` value class provides methods for computing statistics
 *  on data vectors.  Both maximum likelihood and unbiased estimators are
 *  supported.  Unbiased should only be used on sample (not population) data.
 *  Ex:  It can be used to support the Method of Independent Replications (MIR).
 *  For efficiency, `StatVector` is a value class that enriches the `VectorD`.
 *  The corresponding implicit conversion in the `stat` package object.
 *  @see http://stackoverflow.com/questions/14861862/how-do-you-enrich-value-classes-without-overhead
 *-----------------------------------------------------------------------------
 *  @param self  the underlying object to be accessed via the 'self' accessor
 */
class StatVector (val self: VectorD)
      extends AnyVal
{
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the 'k'-median ('k'-th smallest value) of 'self' vector.  Setting
     *  'k = (dim+1)/2' gives the regular median.
     *  @param k  the type of median (k-th smallest value)
     */
    def median (k: Int = (self.dim+1)/2): Double = imedian (self(), k)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the averaged median, which is the median when 'dim' is odd and
     *  the average of the median and the next'k'-median when 'dim' is even.
     */
    def amedian: Double = if (self.dim % 2 == 0) (median () + median ((self.dim+2)/2)) / 2.0
                          else                    median ()

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the sample covariance of 'self' vector with vector 'y'.
     *  @param y  the other vector
     */
    def cov (y: VectorD): Double = ((self dot y) - self.sum * y.sum / self.nd) / (self.nd-1.0)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the population covariance of 'self' vector with vector 'y'.
     *  @param y  the other vector
     */
    def pcov (y: VectorD): Double = ((self dot y) - self.sum * y.sum / self.nd) / self.nd

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the 'k'-lag auto-covariance of 'self' vector.
     *  @param k  the lag parameter
     */
    def acov (k: Int = 1): Double =
    {
        val mu  = self.mean
        val num = self.dim - k
        var sum = 0.0
        for (i <- 0 until num) sum += (self(i) - mu) * (self(i+k) - mu)
        sum / num
    } // acov

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute Pearson's correlation of 'self' vector with vector 'y'.
     *  @param y  the other vector
     */
    def corr (y: VectorD): Double = cov (y) / sqrt (self.variance * y.variance)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the population Pearson's correlation of 'self' vector with vector 'y'.
     *  @param y  the other vector
     */
    def pcorr (y: VectorD): Double = pcov (y) / sqrt (self.pvariance * y.pvariance)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute Spearman's rank correlation of 'self' vector with vector 'y'.
     *  @see  http://en.wikipedia.org/wiki/Spearman%27s_rank_correlation_coefficient
     *  @param y  the other vector
     */
    def scorr (y: VectorD): Double = 
    {
        1.0 - 6.0 * (self.rank - y.rank).normSq / (self.nd * (self.nd*self.nd - 1.0))
    } // scorr

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the 'k'-lag auto-correlation of 'self' vector.
     *  @param k  the lag parameter
     */
    def acorr (k: Int = 1): Double = acov (k) / self.variance

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the standard deviation of 'self' vector.
     */
    def stddev: Double = sqrt (self.variance)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the mean square (ms) of 'self' vector.
     */
    def ms: Double = self.normSq / self.nd

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the root mean square (rms) of 'self' vector.
     */
    def rms: Double = sqrt (ms)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the skewness of 'self' vector.  Negative skewness indicates the
     *  distribution is elongated on the left, zero skewness indicates it is
     *  symmetric, and positive skewness indicates it is elongated on the right.
     *  @see http://www.mathworks.com/help/stats/skewness.html
     */
    def skew: Double =
    {
        val s = (self - self.mean).sum~^3 / (self.nd * stddev~^3)
        s * sqrt (self.nd * (self.nd-1.0)) / (self.nd-2.0)
    } // skew

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the product of the critical value from the t-distribution and the
     *  standard deviation of the vector.
     *  @param p  the confidence level
     */
    def t_sigma (p: Double = .95): Double =
    {
        val df = self.dim - 1                           // degrees of freedom
        if (df < 1) println ("interval, must have at least 2 observations")
        val pp = 1.0 - (1.0 - p) / 2.0             // e.g., .95 --> .975 (two tails)
        val t  = studentTInv (pp, df)
        t * stddev
    } // t_sigma

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the confidence interval half-width for the given confidence level.
     *  The Confidence Interval (CI) is on the mean, i.e., CI = [mean +/- interval].
     *  @param p  the confidence level
     */
    def interval (p: Double = .95): Double = t_sigma (p) / sqrt (self.nd)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compute the relative precision, i.e., the ratio of the confidence interval
     *  half-width and the mean.
     *  @param p  the confidence level
     */
    def precision (p: Double = .95): Double = interval (p) / self.mean

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Determine if the Confidence Interval (CI) on the mean is tight enough.
     *  @param threshold  the cut-off value for CI to be considered tight
     *  @param p          the confidence level
     */
    def precise (threshold: Double = .2, p: Double = .95): Boolean = precision (p) <= threshold

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Produce a standardized version of the vector by subtracting the mean and
     *  dividing by the standard deviation (e.g., Normal -> Standard Normal).
     */
    def standardize: VectorD = (self - self.mean) / self.stddev

} // StatVector class


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // StatVectorTest object


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // StatVectorTest2 object

