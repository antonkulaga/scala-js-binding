package org.denigma.binding

import org.scalajs.dom.HTMLElement
import scala.collection.immutable._
import rx.Rx
import org.denigma.binding.macroses.ListRxMap
import org.scalajs.dom
/**
 * Trait that provides collection binding
 */
trait CollectionBinding{

  def extractListRx[T: ListRxMap](t: T): Map[String, Rx[List[Map[String, Any]]]] =  implicitly[ListRxMap[T]].asListRxMap(t)

  def lists: Map[String, Rx[List[Map[String, Any]]]]
}