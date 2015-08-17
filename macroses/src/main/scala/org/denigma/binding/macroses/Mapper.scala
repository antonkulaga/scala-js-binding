package org.denigma.binding.macroses


import scala.collection.immutable._
import scala.language.experimental.macros
import rx._
import scala.reflect.macros.whitebox

import org.scalajs.dom
import scalatags.Text._


/**
 * Just some experiments
 */

trait TypeClass[T,R]{
  def asMap(t:T):Map[String,R]
}

object TypeClass extends BinderObject{
  implicit def materialize[T,R]: TypeClass[T,R] = macro impl[T,R]

  def impl[T: c.WeakTypeTag,R: c.WeakTypeTag](c: whitebox.Context): c.Expr[TypeClass[T,R]] = {
    import c.universe._
    val mapExpr = extract[T,R](c)

    reify {
      new TypeClass[T,R] {
        def asMap(t: T) = mapExpr.splice
      }
    }
  }

}

/**
 * Trait for materialization
 * @tparam T
 */
trait ClassToMap[T] {
  def asMap(t: T): Map[String,Any]
}


object ClassToMap extends BinderObject {
  implicit def materialize[T]: ClassToMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[ClassToMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Any](c)

    reify {
      new ClassToMap[T] {
        def asMap(t: T) = mapExpr.splice
      }
    }
  }

}


trait TagRxMap[T] {
  def asTagRxMap(t: T): Map[String,Rx[Tag]]
}

object TagRxMap extends BinderObject {
  implicit def materialize[T]: TagRxMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[TagRxMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Rx[Tag]](c)

    reify {
      new TagRxMap[T] {
        def asTagRxMap(t: T) = mapExpr.splice
      }
    }
  }
}

class BinderObject  {


  def extract[T: c.WeakTypeTag,TE:  c.WeakTypeTag](c: whitebox.Context) = {
    import c.universe._

    val mapApply = Select(reify(Map).tree,TermName("apply"))

    val we = weakTypeOf[TE]

    val pairs = weakTypeOf[T].members.collect {
      case m: MethodSymbol if (m.isVal || m.isCaseAccessor || m.isGetter) && m.returnType.<:<(we) =>
        val name = c.literal(m.name.decodedName.toString)
        val value = c.Expr[T](Select(Ident(TermName("t")), m.name))
        reify(name.splice -> value.splice).tree
    }

    c.Expr[Map[String, TE]](Apply(mapApply, pairs.toList))
  }
}

