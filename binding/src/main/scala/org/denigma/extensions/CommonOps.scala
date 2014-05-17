package org.denigma.extensions

import scala.collection.immutable._
import org.scalajs.dom

/**
 * Useful for errors
 */
trait CommonOps {

  implicit class OptionOpt[T](source:Option[T]){

    def orError(str:String) = if(source.isEmpty) dom.console.error(str)

  }

  implicit class MapOpt[TValue](source:Map[String,TValue]) {

    def getOrError(key:String) = {
      val g = source.get(key)
      if(g.isEmpty) dom.console.error(s"failed to find item with key $key")
      g
    }

  }

}
