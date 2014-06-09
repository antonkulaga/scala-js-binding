package org.denigma.binding.macroses

import scala.collection.immutable.{List, Map}
import rx._
import scala.reflect.macros.Context


trait MapRxMap[T] {
  def asMapRxMap(t: T): Map[String,Rx[Map[String,Any]]]
}

object MapRxMap extends BinderObject
{
  implicit def materialize[T]: MapRxMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[MapRxMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Rx[Map[String,Any]]](c)

    reify {
      new MapRxMap[T] {
        def asMapRxMap(t: T) = mapExpr.splice
      }
    }
  }
}


trait ListRxMap[T] {
  def asListRxMap(t: T): Map[String,Rx[List[Map[String,Any]]]]
}

object ListRxMap extends BinderObject {
  implicit def materialize[T]: ListRxMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[ListRxMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Rx[List[Map[String,Any]]]](c)

    reify {
      new ListRxMap[T] {
        def asListRxMap(t: T): Map[String, Rx[List[Map[String, Any]]]] = mapExpr.splice
      }
    }
  }


}
