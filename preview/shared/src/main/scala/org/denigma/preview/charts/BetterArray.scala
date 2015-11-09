package org.denigma.preview.charts

/**
  * Created by antonkulaga on 11/9/15.
  */
class BetterArray(val arr: Array[Double]) extends AnyVal {
  def +(arr2: Array[Double]): Array[Double] = {
    if(arr2.length > arr.length) throw new Exception("length of the second array is longer, cannot add it!")
    val newArr: Array[Double] = arr.clone()
    for(i <- arr2.indices){
      newArr(i) +=  arr2(i)
    }
    newArr
  }

  def prettyPrinted(): String = "["+arr.toList.mkString(" , ")+"]"

  def -(arr2: Array[Double]): Array[Double] = {
    if (arr2.length > arr.length) throw new Exception("length of the second array is longer, cannot add it!")
    val newArr: Array[Double] = arr.clone()
    for(i <- arr2.indices){
      newArr(i) -= arr2(i)
    }
    newArr
  }

  def +(s: Double): Array[Double] =
  {
    val c = arr.clone()
    for (i <- arr.indices) c(i) +=  s
    c
  }

  def -(s: Double): Array[Double] = this + -s

  def *(s: Double): Array[Double] =
  {
    val c = arr.clone()
    for (i <- arr.indices) c(i) *=  s
    c
  }

  def /(s: Double): Array[Double] =
  {
    if(s==0.0) throw new Exception("devision of extended Array by zero")
    val c = arr.clone()
    for (i <- arr.indices) c(i) /=  s
    c
  }

}
