package org.denigma.binding.extensions

import rx.Rx

import scala.concurrent.duration.FiniteDuration
import scala.scalajs.js


class TimerExtensions[T](val source: Rx[T]) extends AnyVal{

  def afterLastChange(time: FiniteDuration)(fun: T => Unit): Unit = {
    source.onChange{
      case s =>
        js.timers.setTimeout(time) { if (source.now == s) fun(s) }
    }
  }
}
