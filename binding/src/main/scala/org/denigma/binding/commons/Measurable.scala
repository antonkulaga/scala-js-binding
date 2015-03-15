package org.denigma.binding.commons

import scala.scalajs.js
import org.scalajs.dom

/**
 * Trait that measures time of function execution
 */
trait Measurable {

  def measure(name:String)(fun:()=>Unit):(String,Number) = {
    val start = new js.Date().getTime()
    fun()
    val end = new js.Date().getTime()
    val time: Number = end - start
    (name,time)
  }

  def alertedMeasure(name:String)(fun:()=>Unit): Unit = {
    val tn: (String, Number) = this.measure(name)(fun)
    dom.alert(s"function ${tn._1} exectured for ${tn._2}")
  }

  def loggedMeasure(name:String)(fun:()=>Unit): Unit = {
    val tn: (String, Number) = this.measure(name)(fun)
    dom.console.log(s"function ${tn._1} exectured for ${tn._2}")
  }
}
