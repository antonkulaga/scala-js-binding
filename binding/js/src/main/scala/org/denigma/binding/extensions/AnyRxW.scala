package org.denigma.binding.extensions

import rx._

import scala.concurrent.duration.FiniteDuration

@deprecated
class AnyRxW[T](source: Rx[T])(implicit ctx: Ctx.Owner)  {

  import scalajs.js
  def delayed(time: FiniteDuration) = {
    val v = Var(source.now) //UGLY BUT WORKS
    js.timers.setTimeout(time)( v() = source.now)
    v
  }

  def afterLastChange(time: FiniteDuration): Var[T] = {
    val v = Var(source.now) //UGLY BUT WORKS
    def waitChange(value:T): Unit ={
      js.timers.setTimeout(time){
        if(source.now==value)
          v() = value
        else
          waitChange(source.now)
      }
    }
    source.foreach(s=> if(s!=v.now) waitChange(s))
    v
  }
}
