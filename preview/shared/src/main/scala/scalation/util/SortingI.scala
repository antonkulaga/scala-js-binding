
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @builder scalation.util.bld.BldSorting
 *  @version 1.2
 *  @date    Sat Sep 26 20:25:19 EDT 2015
 *  @see     LICENSE (MIT style license file).
 */

package scalation.util

import scala.util.Random



//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `SortingI` class provides direct and indirect methods to:
 *  <p>
 *      find 'k'-th median ('k'-th smallest element) using QuickSelect 
 *      sort large arrays using QuickSort
 *      sort small arrays using SelectionSort
 *  <p>
 *  Direct methods are faster, but modify the array, while indirect methods are
 *  slower, but do not modify the array.  This class is specialized for Int.
 *  @see `Sorting` for a generic version of this class.
 *  @param a  the array to operate on
 */
class SortingI (a: Array [Int])
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
    def median (p: Int, r: Int, k: Int): Int =
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
    def median (k: Int = (n+1)/2): Int = median (0, n-1, k)

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
    def selsort (p: Int = 0, r: Int = n-1)
    {
        for (i <- p until r) {
            var k = i
            for (j <- i+1 to r if a(j) < a(k)) k = j
            if (i != k) swap (i, k)
        } // for
    } // selsort

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Swap the elements at 'i' and 'j', i.e., a(i) <-> a(j).
     *  @param i  the first index position
     *  @param j  the second index position
     */
    @inline private def swap (i: Int, j: Int) { val t = a(i); a(i) = a(j); a(j) = t }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the index of the median of three elements.
     *  @param i  element 1
     *  @param j  element 2
     *  @param k  element 3
     */
    @inline private def med3 (i: Int, j: Int, k: Int): Int =
    {
        if (a(i) < a(j))
            if (a(j) < a(k)) j else if (a(i) < a(k)) k else i
        else
            if (a(j) > a(k)) j else if (a(i) > a(k)) k else i
    } // med3

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Determine whether the array 'a' is sorted in ascending order.
     */
    def isSorted: Boolean =
    {
        for (i <- 1 until n if a(i-1) > a(i)) {
            println ("isSorted: failed @ (i-1, a) = " + (i-1, a(i-1)))
            println ("isSorted: failed @ (i, a)   = " + (i, a(i)))
            return false
        } // for
        true
    } // isSorted


    // Directly sorting in decreasing order ----------------------------------

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Partition the array from 'p' to 'q' into a left partition (<= 'x') and
     *  a right partition (>= 'x').
     *  For sorting in decreasing order.
     *  @param p  the left cursor
     *  @param q  the right cursor
     */
    def partition2 (p: Int, r: Int): Int =
    {
        val x = a(r)                            // pivot
        var i = p - 1
        for (j <- p until r if a(j) >= x) { i += 1; swap (i, j) }
        swap (i + 1, r)
        i + 1
    } // partition2

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Recursively sort the 'p' to 'r' partition of array 'a' using QuickSort.
     *  Sort in decreasing order.
     *  @see http://mitpress.mit.edu/books/introduction-algorithms
     *  @param p  the left cursor
     *  @param r  the right cursor
     */
    def qsort2 (p: Int, r: Int)
    {
        if (r - p > 5) {
            swap (r, med3 (p, (p+r)/2, r))      // use median-of-3, comment out for simple pivot
            val q = partition2 (p, r)           // partition into left (<=) and right (>=)
            qsort2 (p, q - 1)                   // recursively sort left partition
            qsort2 (q + 1, r)                   // recursively sort right partition
        } else {
            selsort2 (p, r)                     // use simple sort when small
        } // if
    } // qsort2

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Sort array 'a' using QuickSort.  Sort in decreasing order.
     */
    def qsort2 () { qsort2 (0, n-1) }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Sort the 'p' to 'r' partition of array 'a' using SelectionSort.
     *  Sort in decreasing order.
     *  @param p  the left cursor
     *  @param r  the right cursor
     */
    def selsort2 (p: Int = 0, r: Int = n-1)
    {
        for (i <- p until r) {
            var k = i
            for (j <- i+1 to r if a(j) > a(k)) k = j
            if (i != k) swap (i, k)
        } // for
    } // selsort2

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Determine whether the array 'a' is sorted in descending order.
     */
    def isSorted2: Boolean =
    {
        for (i <- 1 until n if a(i-1) < a(i)) {
            println ("isSorted2: failed @ (i-1, a) = " + (i-1, a(i-1)))
            println ("isSorted2: failed @ (i, a)   = " + (i, a(i)))
            return false
        } // for
        true
    } // isSorted2

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
    def imedian (rk: Array [Int], p: Int, r: Int, k: Int): Int =
    {
        if (p == r) return a(rk(p))
        iswap (rk, r, med3 (p, (p+r)/2, r))           // use median-of-3, comment out for simple pivot, ?imed3
        val q = ipartition (rk, p, r)                 // partition into left (<=) and right (>=)
        if (q == k-1)     return a(rk(q))             // found k-median
        else if (q > k-1) imedian (rk, p, q - 1, k)   // recursively find median in left partition
        else              imedian (rk, q + 1, r, k)   // recursively find median in right partition
    } // imedian

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly find the 'k'-median ('k'-th smallest element) of array 'a'.
     *  @param k  the type of median (e.g., k = (n+1)/2 is the median)
     */
    def imedian (k: Int = (n+1)/2): Int =
    {
        val rk = Array.range (0, n)                   // rank order
        imedian (rk, 0, n-1, k)
    } // imedian

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly partition the array from 'p' to 'r' into a left partition
     *  (<= 'x') and a right partition (>= 'x').
     *  @param rk  the rank order
     *  @param p   the left cursor
     *  @param r   the right cursor
     */
    def ipartition (rk: Array [Int], p: Int, r: Int): Int =
    {
        val x = a(rk(r))                              // pivot
        var i = p - 1
        for (j <- p until r if a(rk(j)) <= x) { i += 1; iswap (rk, i, j) }
        iswap (rk, i + 1, r)
        i + 1
    } // ipartition

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Recursively and indirectly sort the 'p' to 'r' partition of array 'a'
     *  using  QuickSort.
     *  @param rk  the rank order
     *  @param p   the left cursor
     *  @param r   the right cursor
     */
    def iqsort (rk: Array [Int], p: Int, r: Int)
    {
        if (r - p > 5) {
            iswap (rk, r, med3 (p, (p+r)/2, r))       // use median-of-3, comment out for simple pivot, ?imed3
            val q = ipartition (rk, p, r)             // partition into left (<=) and right (>=)
            iqsort (rk, p, q - 1)                     // recursively sort left partition
            iqsort (rk, q + 1, r)                     // recursively sort right partition
        } else {
            iselsort (rk, p, r)                       // use simple sort when small
        } // if
    } // iqsort

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly sort array 'a' using QuickSort, returning the rank order.
     */
    def iqsort (): Array [Int] = 
    {
        val rk = Array.range (0, n)                   // rank order
        iqsort (rk, 0, n-1)                           // re-order rank
        rk                                            // return rank
    } // iqsort

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly sort the 'p' to 'r' partition of array 'a' using SelectionSort.
     *  @param rk  the rank order
     *  @param p   the left cursor
     *  @param r   the right cursor
     */
    def iselsort (rk: Array [Int], p: Int, r: Int)
    {
        for (i <- p to r) {
            var k = i
            for (j <- i+1 to r if a(rk(j)) < a(rk(k))) k = j
            if (i != k) iswap (rk, i, k)
        } // for
    } // iselsort

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly sort array 'a' using SelectionSort, returning the rank order.
     */
    def iselsort (): Array [Int] =
    {
        val rk = Array.range (0, n)                   // rank order
        iselsort (rk, 0, n-1)                         // re-order rank
        rk                                            // return rank
    } // iselsort

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly swap the elements at 'i' and 'j', i.e., rk(i) <-> rk(j).
     *  @param rk  the rank order
     *  @param i   the first index position
     *  @param j   the second index position
     */
    @inline private def iswap (rk: Array [Int], i: Int, j: Int)
    {
        val t = rk(i); rk(i) = rk(j); rk(j) = t
    } // iswap

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the indirect index of the median of three elements.
     *  @param rk  the rank order
     *  @param i   element 1
     *  @param j   element 2
     *  @param k   element 3
     */
    @inline private def imed3 (rk: Array [Int], i: Int, j: Int, k: Int): Int =
    {
        if (a(rk(i)) < a(rk(j)))
            if (a(rk(j)) < a(rk(k))) j else if (a(rk(i)) < a(rk(k))) k else i
        else
            if (a(rk(j)) > a(rk(k))) j else if (a(rk(i)) > a(rk(k))) k else i
    } // imed3

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Determine whether the array 'a' is indirectly sorted in ascending order.
     *  @param rk   the rank order
     */
    def isiSorted (rk: Array [Int]): Boolean =
    {
        for (i <- 1 until n if a(rk(i-1)) > a(rk(i))) {
            println ("isiSorted: failed @ (i-1, rk, a) = " + (i-1, rk(i-1), a(rk(i-1))))
            println ("isiSorted: failed @ (i,   rk, a) = " + (i, rk(i), a(rk(i))))
            return false
        } // for
        true
    } // isiSorted

    // Indirectly sorting in decreasing order --------------------------------

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly sort array 'a' using QuickSort.
     *  Sort in decreasing order.
     */
    def iqsort2 (): Array [Int] =
    {
        val rk = Array.range (0, n)                             // rank order
        println ("iqsort2 method not yet implemented")          // FIX
        rk
    } // iqsort2

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly sort the 'p' to 'r' partition of array 'a' using SelectionSort.
     *  Sort in decreasing order.
     *  @param rk  the rank order
     *  @param p   the left cursor
     *  @param r   the right cursor
     */
    def iselsort2 (rk: Array [Int], p: Int, r: Int)
    {
        for (i <- p to r) {
            var k = i
            for (j <- i+1 to r if a(rk(j)) > a(rk(k))) k = j
            if (i != k) iswap (rk, i, k)
        } // for
    } // iselsort2

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly sort array 'a' using SelectionSort, returning the rank order.
     *  Sort in decreasing order.
     */
    def iselsort2 (): Array [Int] =
    {
        val rk = Array.range (0, n)                   // rank order
        iselsort2 (rk, 0, n-1)                        // re-order rank
        rk                                            // return rank
    } // iselsort2

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Determine whether the array 'a' is indirectly sorted in ascending order.
     *  @param rk   the rank order
     */
    def isiSorted2 (rk: Array [Int]): Boolean =
    {
        for (i <- 1 until n if a(rk(i-1)) < a(rk(i))) {
            println ("isiSorted2: failed @ (i-1, rk, a) = " + (i-1, rk(i-1), a(rk(i-1))))
            println ("isiSorted2: failed @ (i,   rk, a) = " + (i, rk(i), a(rk(i))))
            return false
        } // for
        true
    } // isiSorted2

} // SortingI class


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `SortingI` companion object provides shortcuts for calling methods from
 *  the `SortingI` class.
 */
object SortingI
{
    // Direct median and sorting --------------------------------------------

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Find the median value in the array.
     *  @param a  the array to be examined
     */
    def median (a: Array [Int], k: Int): Int = (new SortingI (a)).median (k)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Fast, ascending, unstable sort.
     *  @param a  the array to be sorted
     */
    def qsort (a: Array [Int]) { (new SortingI (a)).qsort () }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Slow, ascending, stable sort.
     *  @param a  the array to be sorted
     */
    def selsort (a: Array [Int]) { (new SortingI (a)).selsort () }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Fast, descending, unstable sort.
     *  @param a  the array to be sorted
     */
    def qsort2 (a: Array [Int]) { (new SortingI (a)).qsort2 () }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Slow, descending, stable sort.
     *  @param a  the array to be sorted
     */
    def selsort2 (a: Array [Int]) { (new SortingI (a)).selsort2 () }

    // Indirect median and sorting -------------------------------------------

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Indirectly find the median value in the array.
     *  @param a  the array to be examined
     */
    def imedian (a: Array [Int], k: Int): Int = (new SortingI (a)).imedian (k)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Fast, ascending, unstable indirect sort.
     *  @param a  the array to be sorted
     */
    def iqsort (a: Array [Int]): Array [Int] = (new SortingI (a)).iqsort ()

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Slow, ascending, stable indirect sort.
     *  @param a  the array to be sorted
     */
    def iselsort (a: Array [Int]): Array [Int] = (new SortingI (a)).iselsort ()

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Fast, descending, unstable indirect sort.
     *  @param a  the array to be sorted
     */
//  def iqsort2 (a: Array [Int]) { (new SortingI (a)).iqsort2 () }   // FIX: implement

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Slow, descending, stable indirect sort.
     *  @param a  the array to be sorted
     */
    def iselsort2 (a: Array [Int]): Array [Int] = (new SortingI (a)).iselsort2 ()

} // SortingI


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // SortingITest


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // SortingITest2


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // SortingITest3


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // SortingITest4Test

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // SortingITest5 object

