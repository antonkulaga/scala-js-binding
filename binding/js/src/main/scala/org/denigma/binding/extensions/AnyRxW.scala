package org.denigma.binding.extensions

import rx.core.{Var, Rx}

import scala.concurrent.duration.FiniteDuration
import rx.ops._
class AnyRxW[T](source:Rx[T]) {

  import scalajs.js
  def delayed(time:FiniteDuration) = {
    val v = Var(source.now) //UGLY BUT WORKS
    js.timers.setTimeout(time)(v()=source.now)
    v
  }

  def afterLastChange(time:FiniteDuration): Var[T] = {
    val v = Var(source.now) //UGLY BUT WORKS
    def waitChange(value:T): Unit ={
      js.timers.setTimeout(time){
        if(source.now==value)
          v() = value
        else
          waitChange(source.now)
      }
    }
    source.foreach(s=>if(s!=v.now)waitChange(s))
    v
  }
}
