package org.denigma.binding.macroses

import scala.collection.immutable.Map
import rx._
import scala.reflect.macros.Context



trait StringRxMap[T] {
  def asStringRxMap(t: T): Map[String,Rx[String]]
}

object StringRxMap extends BinderObject {
  implicit def materialize[T]: StringRxMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[StringRxMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Rx[String]](c)

    reify {
      new StringRxMap[T] {
        def asStringRxMap(t: T) = mapExpr.splice
      }
    }
  }
}

trait BooleanRxMap[T] {
  def asBooleanRxMap(t: T): Map[String,Rx[Boolean]]
}

object BooleanRxMap extends BinderObject {
  implicit def materialize[T]: BooleanRxMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[BooleanRxMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Rx[Boolean]](c)

    reify {
      new BooleanRxMap[T] {
        def asBooleanRxMap(t: T) = mapExpr.splice
      }
    }
  }
}

