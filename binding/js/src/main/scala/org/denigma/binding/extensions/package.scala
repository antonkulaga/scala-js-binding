package org.denigma.binding

import org.scalajs.dom
import rx.Rx

import scala.collection.immutable.Map

/**
 * Useful implicit classes
 */
package object extensions extends AttributesOps
  with AnyJsExtensions
  with RxExt
  with Functions
  with ElementOps
  with EventsOps
  with DataOps
{

  //implicit def toAnyRxW[T](source: Rx[T]): AnyRxW[T] = new AnyRxW[T](source)


  implicit class OptionOpt[T](source: Option[T]){

    def orError(str: String): Unit = if (source.isEmpty) dom.console.error(str)

  }

  implicit class ThrowableOpt(th: Throwable) {
    def stackString: String = th.getStackTrace.foldLeft("")((acc, el) => acc+"\n"+el.toString)
  }


  implicit class MapOpt[TValue](source: Map[String, TValue]) {

    def getOrError(key: String): Option[TValue] = {
      val g = source.get(key)
      if(g.isEmpty) dom.console.error(s"failed to find item with key $key, all keys are: [${source.keySet.toList.mkString(", ")}]")
      g
    }
  }



}
