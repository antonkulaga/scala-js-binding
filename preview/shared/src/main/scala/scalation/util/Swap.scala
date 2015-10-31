
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.2
 *  @date    Sat Nov  5 19:29:13 EDT 2011
 *  @see     LICENSE (MIT style license file).
 */

package scalation.util

import collection.mutable.ArrayBuffer

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `Swap` class provides a method to swap elements in an Array or ArrayBuffer.
 *  Note, ArrayBuffer is resizable (similar to Java's ArrayList).
 */
object Swap
{
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Swap two elements in a regular array.
     *  @param  i  first element to swap
     *  @param  j  second element to swap
     */
    def swap [T] (a: Array [T], i: Int, j: Int)
    {
        val t = a(i); a(i) = a(j); a(j) = t
    } // swap

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Swap two elements in a resizable array.
     *  @param  i  first element to swap
     *  @param  j  second element to swap
     */
    def swap [T] (a: ArrayBuffer [T], i: Int, j: Int)
    {
        val t = a(i); a(i) = a(j); a(j) = t
    } // swap

} // Swap object


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // SwapTest object

