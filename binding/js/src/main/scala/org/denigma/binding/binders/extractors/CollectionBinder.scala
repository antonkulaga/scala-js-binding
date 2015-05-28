package org.denigma.binding.binders.extractors

import org.denigma.binding.macroses.ListRxMap
import rx.Rx

import scala.collection.immutable._
/**
 * Trait that provides collection binding
 */
trait CollectionBinder{

  def extractListRx[T: ListRxMap](t: T): Map[String, Rx[List[Map[String, Any]]]] =  implicitly[ListRxMap[T]].asListRxMap(t)

  def lists: Map[String, Rx[List[Map[String, Any]]]]
}