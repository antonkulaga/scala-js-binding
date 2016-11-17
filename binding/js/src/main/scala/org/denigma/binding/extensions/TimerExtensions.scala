package org.denigma.binding.extensions

import rx._
import rx.Ctx.Owner.Unsafe.Unsafe

import scala.concurrent.duration.FiniteDuration
import scala.scalajs.js


class TimerExtensions[T](val source: Rx[T]) extends AnyVal{

  def afterLastChange(time: FiniteDuration)(fun: T => Unit): Unit = {
    source.onChange{ s => js.timers.setTimeout(time) { if (source.now == s) fun(s) } }
  }

  def mapAfterLastChange[U](delay: FiniteDuration, initial: U)(fun: T => U): Var[U] = {
    val result = Var[U](initial)
    source.afterLastChange(delay){ value => result() = fun(value)}
    result
  }

  def mapAfterLastChange[U](delay: FiniteDuration)(fun: T => U): Var[U] = {
    mapAfterLastChange(delay, fun(source.now))(fun)
  }
}
