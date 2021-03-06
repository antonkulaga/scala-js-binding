package org.denigma.binding.macroses


import scala.collection.immutable.Map
import rx._
import scala.reflect.macros.whitebox
import scala.reflect.macros.whitebox.Context

object PropertyRxMappers {

  import scala.collection.immutable.Map
  import rx._
  import scala.reflect.macros.whitebox
  import scala.reflect.macros.whitebox.Context


  trait IntRxMap[T] {
    def asIntRxMap(t: T): Map[String, Rx[Int]]
  }

  object IntRxMap extends BinderObject {
    implicit def materialize[T]: IntRxMap[T] = macro impl[T]

    def impl[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[IntRxMap[T]] = {
      import c.universe._
      val mapExpr = extract[T, Rx[Int]](c)

      reify {
        new IntRxMap[T] {
          def asIntRxMap(t: T) = mapExpr.splice
        }
      }
    }
  }


  trait DoubleRxMap[T] {
    def asDoubleRxMap(t: T): Map[String, Rx[Double]]
  }

  object DoubleRxMap extends BinderObject {
    implicit def materialize[T]: DoubleRxMap[T] = macro impl[T]

    def impl[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[DoubleRxMap[T]] = {
      import c.universe._
      val mapExpr = extract[T, Rx[Double]](c)

      reify {
        new DoubleRxMap[T] {
          def asDoubleRxMap(t: T) = mapExpr.splice
        }
      }
    }
  }

  trait StringRxMap[T] {
    def asStringRxMap(t: T): Map[String, Rx[String]]
  }

  object StringRxMap extends BinderObject {
    implicit def materialize[T]: StringRxMap[T] = macro impl[T]

    def impl[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[StringRxMap[T]] = {
      import c.universe._
      val mapExpr = extract[T, Rx[String]](c)

      reify {
        new StringRxMap[T] {
          def asStringRxMap(t: T) = mapExpr.splice
        }
      }
    }
  }

  trait BooleanRxMap[T] {
    def asBooleanRxMap(t: T): Map[String, Rx[Boolean]]
  }

  object BooleanRxMap extends BinderObject {
    implicit def materialize[T]: BooleanRxMap[T] = macro impl[T]

    def impl[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[BooleanRxMap[T]] = {
      import c.universe._
      val mapExpr = extract[T, Rx[Boolean]](c)

      reify {
        new BooleanRxMap[T] {
          def asBooleanRxMap(t: T) = mapExpr.splice
        }
      }
    }
  }
}
