package org.denigma.binding

import org.scalajs.dom
import rx._
import rx.ops.RxExt

import scala.annotation.tailrec
import scala.collection.immutable.Map
import scala.concurrent.duration.{FiniteDuration, Duration}
import scala.scalajs.js.Dynamic.{global => g}





/**
 * Useful implicit classes
 */
package object extensions extends AttributesOps with AnyJsExtensions with RxExt with Functions with EventsOps {

  implicit def toAnyRxW[T](source:Rx[T]):AnyRxW[T] = new AnyRxW[T](source)

  /*implicit class AnyRx[T,M](source:Rx[T])
  {
    import rx.ops._
    def mapAfter[A](f: T => A) = source.map(fun)
    scalajs.js.timers
  }
  */
  implicit class OptionOpt[T](source:Option[T]){

    def orError(str:String) = if(source.isEmpty) dom.console.error(str)

  }

  implicit class ThrowableOpt(th:Throwable) {
    def stackString = th.getStackTrace.foldLeft("")( (acc,el)=>acc+"\n"+el.toString)
  }


  implicit class MapOpt[TValue](source:Map[String,TValue]) {

    def getOrError(key:String) = {
      val g = source.get(key)
      if(g.isEmpty) dom.console.error(s"failed to find item with key $key")
      g
    }
  }
}
