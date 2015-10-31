
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.2
 *  @date    Thu Feb 14 14:06:00 EST 2013
 *  @see     LICENSE (MIT style license file).
 */

package scalation.linalgebra

import scalation.linalgebra.Householder.house
import scalation.linalgebra.MatrixD.{eye, outer}
import scalation.util.Error

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `Bidiagonal` class is used to creates a bidiagonal matrix for matrix 'a'.
 *  It uses the Householder Bidiagonalization Algorithm to compute orthogonal
 *  matrices 'u' and 'v' such that 
 *  <p>
 *      u.t * a * v = b
 *      a  =  u * b * v.t
 *  <p>
 *  where matrix 'b' is bidiagonal, i.e, it will only have non-zero elements on
 *  its main diagonal and  its super-diagonal (the diagonal above the main).
 *  @param a  the m-by-n matrix to bidiagonalize
 */
class Bidiagonal (private var a: MatrixD)
      extends Error
{
    private val m = a.dim1         //  the number of rows
    private val n = a.dim2         //  the number of columns

    if (n > m) flaw ("constructor", "Bidiagonal requires m >= n")

    private val u = eye (m)        // initialize left orthogonal matrix to m-by-m identity matrix
    private val v = eye (n)        // initialize right orthogonal matrix to n-by-n identity matrix

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Use the Householder Bidiagonalization Algorithm to compute orthogonal
     *  matrices 'u' and 'v' such that 'u.t * a * v = b' where matrix 'b' is bidiagonal.
     *  This implementation computes 'b' in-place overwriting matrix 'a'.
     *  @see Matrix Computations:  Algorithm 5.4.2 Householder Bidiagonalization
     */
    def bidiagonalize (): Tuple3 [MatrixD, MatrixD, MatrixD] =
    {
        for (j <- 0 until n) {
            val (vu, bu) = house (a(j until m, j))                   // Householder (vector vu, scalar bu)
            val hu = eye (m-j) - outer (vu, vu * bu)                 // Householder matrix hu
            a(j until m, j until n) = hu * a(j until m, j until n)   // zero column j below main diagonal
//          for (i <- j+1 until m) a(i, j) = vu(i-j)                 // save key part of Householder vector vu
            u *= hu.diag (j)                                         // multiply u by Householder matrix hu

            if (j < n-2) {
                val (vv, bv) = house (a(j, j+1 until n))                     // Householder (vector vv, scalar bv)
                val hv = eye (n-j-1) - outer (vv, vv * bv)                   // Householder matrix hv
                a(j until m, j+1 until n) = a(j until m, j+1 until n) * hv   // zero row j past super-diagonal
//              for (k <- j+2 until n) a(j, k) = vv(k-j-1)                   // save key part of Householder vector vv
                v *= hv.diag (j+1)                                           // multiply v by Householder matrix hv
            } // if
        } // for

//      for (i <- 0 until m; j <- 0 until n if j != i && j != i+1) a(i, j) = 0.0  // clean matrix off diagonals
        (u, a, v)
    } // bidiagonalize

} // Bidiagonal


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `BidiagonalTest` object is used to test the `Bidiagonal` class.
 *  bidiagonalization answer = [ (21.8, -.613), (12.8, 2.24, 0.) ]
 *  @see books.google.com/books?isbn=0801854148  (p. 252)
 *  > run-main scalation.linalgebra.BidiagonalTest
 */
object BidiagonalTest extends App
{
    val a = new MatrixD ((4, 3), 1.0,  2.0,  3.0,     // orginal matrix
                                 4.0,  5.0,  6.0,
                                 7.0,  8.0,  9.0,
                                10.0, 11.0, 12.0)
    println ("a = " + a)

    val bid = new Bidiagonal (a)                      // Householder bidiagonalization
    val (u, b, v) = bid.bidiagonalize ()              // bidiagonalize a
    println ("(u, b, v) = " + (u, b, v))
    println ("ubv.t = " + u * b * v.t)                // should equal the original a

} // BidiagonalTest object

