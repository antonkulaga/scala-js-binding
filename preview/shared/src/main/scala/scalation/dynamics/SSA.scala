
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.2
 *  @date    Sun Sep 16 22:35:14 EDT 2012
 *  @see     LICENSE (MIT style license file).
 *  @see http://en.wikipedia.org/wiki/Gillespie_algorithm
 */

// U N D E R   D E V E L O P M E N T

package scalation.dynamics

import scalation.linalgebra.{MatrixD, MatrixI, VectorD}
import scalation.util.Error

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `SSA` class implements the Gillespie Stochastic Simulation Algorithm (SSA).
 *  @param c   the matrix giving sub-volume connectivity
 *  @param r   the matrix indicating which the reactions that are active in each sub-volume
 *  @param z   the matrix giving stoichiometry for all reactions
 *  @param x   the matrix giving species population per volume
 *  @param t0  the start time for the simulation
 */
class SSA (c: MatrixI, r: MatrixI, z: MatrixI, x: MatrixD, t0: Double = 0.0)
      extends Error
{
    val L = c.dim1      // the number of sub-volumes
    val R = r.dim2      // the number of possible reactions
    val S = z.dim2      // the number of species (e.g., types of molecules)

    if (c.dim2 != L)                flaw ("constructor", "wrong dimensions for c matrix")
    if (r.dim1 != L)                flaw ("constructor", "wrong dimensions for x matrix")
    if (z.dim1 != R)                flaw ("constructor", "wrong dimensions for x matrix")
    if (x.dim1 != L || x.dim2 != S) flaw ("constructor", "wrong dimensions for x matrix")

    val cut = (.003, 3.0, 100.0)                                 // cut-off values
    val e   = for (l <- 0 until L) yield r(l).sum + c(l).sum     // reaction + diffusion events
    var t   = t0                                                 // the simulation clock (current time)

    println ("e = " + e)

    val a = Array.ofDim [VectorD] (L)
    for (l <- 0 until L) {
        val a_l = new VectorD (e(l))
        for (j <- 0 until e(l)) a_l(j) = .1 * x(l, j)    // formula is application dependent
        a(l) = a_l
    } // for

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     */
    def simulate (tf: Double)
    {
    } // simulate

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     */
    override def toString = "a = " + a.deep

} // SSA class


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // SSATest object

