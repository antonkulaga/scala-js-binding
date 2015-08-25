package org.denigma.binding

import org.scalajs.dom
import rx.ops
import rx.ops.RxExt

import scala.collection.immutable.Map
import scala.scalajs.js.Dynamic.{global => g}

/**
 * Useful implicit classes
 */
package object extensions extends AttributesOps with AnyJsExtensions with RxExt with Functions with EventsOps {

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
