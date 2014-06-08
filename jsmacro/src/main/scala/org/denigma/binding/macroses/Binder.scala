package org.denigma.binding.macroses


import scala.language.experimental.macros
import rx._
import org.scalajs.dom.Attr
import scala.reflect.runtime.universe._
import scalatags.Text.Tag


object Binder {


  import scala.reflect.macros.Context


  def extractByType_impl[T: c.WeakTypeTag,TE: TypeTag](c: Context) =
  {
    import c.universe._

    val mapApply = Select(reify(Map).tree, newTermName("apply"))

    val pairs = weakTypeOf[T].members.collect {
      case m: MethodSymbol if m.returnType.<:<(weakTypeOf[TE]) =>
        val name = c.literal(m.name.decoded)
        val value = c.Expr(Select(c.resetAllAttrs(c.prefix.tree), m.name))
        reify(name.splice -> value.splice).tree
    }

    c.Expr[Map[String, TE]](Apply(mapApply, pairs.toList))
  }

  def htmlBindings_impl[T: c.WeakTypeTag](c: Context) = this.extractByType_impl[T,Rx[Tag]](c)
  def attrBindings_impl[T: c.WeakTypeTag](c: Context) = this.extractByType_impl[T,Rx[Attr]](c)
  def stringBindings_impl[T: c.WeakTypeTag](c: Context) = this.extractByType_impl[T,Rx[String]](c)
  def booleanBindings_impl[T: c.WeakTypeTag](c: Context) = this.extractByType_impl[T,Rx[Boolean]](c)
  def doubleBindings_impl[T: c.WeakTypeTag](c: Context) = this.extractByType_impl[T,Rx[Double]](c)


}

/**
 * Just for the stuff that should be binded
 */
class Bindable

