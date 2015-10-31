
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.2
 *  @date    Sat Oct 26 21:44:36 EDT 2013
 *  @see     LICENSE (MIT style license file).
 */

package scalation.util

import scala.util.Random

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `Sorting` class provides direct and indirect methods to:
 *  <p>
 *      find 'k'-th median ('k'-th smallest element) using QuickSelect 
 *      sort large arrays using QuickSort
 *      sort small arrays using SelectionSort
 *  <p>
 *  Direct methods are faster, but modify the array, while indirect methods
 *  are slower, but do not modify the array.  This class is generic.
 *  @see `SortingD` for a version of this class specialized for Doubles.
 *  @see `SortingI` for a version of this class specialized for Ints.
 *  @param a  the array to operate on
 */
class Sorting [T <% Ordered[T]] (a: Array [T])
{
    private val n = a.length                      // length of array a

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // Direct Median and Sorting
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Find the 'k'-median of the 'p' to 'r' partition of array 'a' using
     *  the QuickSelect algorithm.
     *  @see http://en.wikipedia.org/wiki/Quickselect
     *  @param p  the left cursor
     *  @param r  the right cursor
     *  @param k  the type of median (k-th smallest element)
     */
    def median (p: Int, r: Int, k: Int): T =
    {
        if (p == r) return a(p)
        swap (r, med3 (p, (p+r)/2, r))            // use median-of-3, comment out for simple pivot
        val q = partition (p, r)                  // partition into left (<=) and right (>=)
        if (q == k-1)     return a(q)             // found k-median
        else if (q > k-1) median (p, q - 1, k)    // recursively find median in left partition
        else              median (q + 1, r, k)    // recursively find median in right partition
    } // median

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Find the 'k'-median ('k'-th smallest element) of array 'a'.
     *  @param k  the type of median (e.g., k = (n+1)/2 is the median)
     */
    def median (k: Int = (n+1)/2): T = median (0, n-1, k)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Partition the array from 'p' to 'q' into a left partition (<= 'x') and
     *  a right partition (>= 'x').
     *  @param p  the left cursor
     *  @param q  the right cursor
     */
    def partition (p: Int, r: Int): Int =
    {
        val x = a(r)                            // pivot
        var i = p - 1
        for (j <- p until r if a(j) <= x) { i += 1; swap (i, j) }
        swap (i + 1, r)
        i + 1
    } // partition 

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Recursively sort the 'p' to 'r' partition of array 'a' using QuickSort.
     *  @see http://mitpress.mit.edu/books/introduction-algorithms
     *  @param p  the left cursor
     *  @param r  the right cursor
     */
    def qsort (p: Int, r: Int)
    {
        if (r - p > 5) {
            swap (r, med3 (p, (p+r)/2, r))      // use median-of-3, comment out for simple pivot
            val q = partition (p, r)            // partition into left (<=) and right (>=)
            qsort (p, q - 1)                    // recursively sort left partition
            qsort (q + 1, r)                    // recursively sort right partition
        } else {
            selsort (p, r)                      // use simple sort when small
        } // if
    } // qsort

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Sort array 'a' using QuickSort.
     */
    def qsort () { qsort (0, n-1) }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Sort the 'p' to 'r' partition of array 'a' using SelectionSort.
     *  @param p  the left cursor
     *  @param r  the right cursor
     */
    def selsort (p: Int, r: Int)
    {
        for (i <- p to r) {
            var k = i
            for (j <- i+1 to r if a(j) < a(k)) k = j
            if (i != k) swap (i, k)
        } // for
    } // selsort

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Sort array 'a' using SelectionSort.
     */
    def selsort () { selsort (0, n-1) }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // Indirect Median and Sorting
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly find the 'k'-median of the 'p' to 'r' partition of array 'a'
     *  using the QuickSelect algorithm.
     *  @see http://en.wikipedia.org/wiki/Quickselect
     *  @param rk  the rank order
     *  @param p   the left cursor
     *  @param r   the right cursor
     *  @param k   the type of median (k-th smallest element)
     */
    def median (rk: Array [Int], p: Int, r: Int, k: Int): T =
    {
        if (p == r) return a(rk(p))
        swap (rk, r, med3 (p, (p+r)/2, r))           // use median-of-3, comment out for simple pivot
        val q = partition (rk, p, r)                 // partition into left (<=) and right (>=)
        if (q == k-1)     return a(rk(q))            // found k-median
        else if (q > k-1) median (rk, p, q - 1, k)   // recursively find median in left partition
        else              median (rk, q + 1, r, k)   // recursively find median in right partition
    } // median

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly find the 'k'-median ('k'-th smallest element) of array 'a'.
     *  @param k  the type of median (e.g., k = (n+1)/2 is the median)
     */
    def imedian (k: Int = (n+1)/2): T =
    {
        val rk = Array.range (0, n)                  // rank order
        median (rk, 0, n-1, k)
    } // imedian

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly partition the array from 'p' to 'r' into a left partition
     *  (<= 'x') and a right partition (>= 'x').
     *  @param rk  the rank order
     *  @param p   the left cursor
     *  @param r   the right cursor
     */
    def partition (rk: Array [Int], p: Int, r: Int): Int =
    {
        val x = a(rk(r))                             // pivot
        var i = p - 1
        for (j <- p until r if a(rk(j)) <= x) { i += 1; swap (rk, i, j) }
        swap (rk, i + 1, r)
        i + 1
    } // partition

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Recursively and indirectly sort the 'p' to 'r' partition of array 'a'
     *  using  QuickSort.
     *  @param rk  the rank order
     *  @param p   the left cursor
     *  @param r   the right cursor
     */
    def qsort (rk: Array [Int], p: Int, r: Int)
    {
        if (r - p > 5) {
            swap (rk, r, med3 (p, (p+r)/2, r))       // use median-of-3, comment out for simple pivot
            val q = partition (rk, p, r)             // partition into left (<=) and right (>=)
            qsort (rk, p, q - 1)                     // recursively sort left partition
            qsort (rk, q + 1, r)                     // recursively sort right partition
        } else {
            selsort (rk, p, r)                       // use simple sort when small
        } // if
    } // qsort

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly sort array 'a' using QuickSort, returning the rank order.
     */
    def iqsort (): Array [Int] = 
    {
        val rk = Array.range (0, n)                  // rank order
        qsort (rk, 0, n-1)                           // re-order rank
        rk                                           // return rank
    } // iqsort

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly sort the 'p' to 'r' partition of array 'a' using SelectionSort.
     *  @param rk  the rank order
     *  @param p   the left cursor
     *  @param r   the right cursor
     */
    def selsort (rk: Array [Int], p: Int, r: Int)
    {
        for (i <- p to r) {
            var k = i
            for (j <- i+1 to r if a(rk(j)) < a(rk(k))) k = j
            if (i != k) swap (rk, i, k)
        } // for
    } // selsort

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly sort array 'a' using SelectionSort, returning the rank order.
     */
    def iselsort (): Array [Int] =
    {
        val rk = Array.range (0, n)                // rank order
        selsort (rk, 0, n-1)                       // re-order rank
        rk                                         // return rank
    } // iselsort

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Determine whether the array 'a' is sorted.
     */
    def isSorted: Boolean =
    {
        for (i <- 1 until n if a(i-1) > a(i)) {
            println ("isSorted: failed @ (i-1, a) = " + (i-1, a(i-1)))
            println ("isSorted: failed @ (i, a) = " + (i, a(i)))
            return false
        } // for
        true
    } // isSorted

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Determine whether the array 'a' is indirectly sorted.
     *  @param rk  the rank order
     */
    def isSorted (rk: Array [Int]): Boolean =
    {
        for (i <- 1 until n if a(rk(i-1)) > a(rk(i))) {
            println ("isSorted: failed @ (i-1, rk, a) = " + (i-1, rk(i-1), a(rk(i-1))))
            println ("isSorted: failed @ (i,   rk, a) = " + (i, rk(i), a(rk(i))))
            return false
        } // for
        true
    } // isSorted

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Swap the elements at 'i' and 'j', i.e., a(i) <-> a(j).
     *  @param i  the first index position
     *  @param j  the second index position
     */
    @inline private def swap (i: Int, j: Int) { val t = a(i); a(i) = a(j); a(j) = t }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly swap the elements at 'i' and 'j', i.e., rk(i) <-> rk(j).
     *  @param rk  the rank order
     *  @param i   the first index position
     *  @param j   the second index position
     */
    @inline private def swap (rk: Array [Int], i: Int, j: Int)
    {
        val t = rk(i); rk(i) = rk(j); rk(j) = t
    } // swap

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the index of the median of three elements.
     *  @param i  element 1
     *  @param j  element 2
     *  @param k  element 3
     */
    @inline private def med3 (i: Int, j: Int, k: Int): Int =
    {
        if (a(i) < a(j)) if (a(j) < a(k)) j else if (a(i) < a(k)) k else i
        else             if (a(j) > a(k)) j else if (a(i) > a(k)) k else i
    } // med3

} // Sorting class


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `Sorting` companion object provides shortcuts for calling methods from
 *  the `Sorting` class.
 */
object Sorting
{
    def median (a: Array [Double], k: Int): Double = (new Sorting (a)).median (k)

    def qsort (a: Array [Double]) { (new Sorting (a)).qsort () }

    def selsort (a: Array [Double]) { (new Sorting (a)).iselsort () }

    def imedian (a: Array [Double], k: Int): Double = (new Sorting (a)).imedian (k)

    def iqsort (a: Array [Double]): Array [Int] = (new Sorting (a)).iqsort ()

    def iselsort (a: Array [Double]): Array [Int] = (new Sorting (a)).iselsort ()

} // Sorting


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // SortingTest


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // SortingTest2

