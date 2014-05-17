package org.denigma.binding.macroses

import scala.collection.immutable.Map
import rx._
import org.scalajs.dom
import scala.reflect.macros.Context

trait KeyEventMap[T] {
  def asKeyEventMap(t:T):Map[String,Var[dom.KeyboardEvent]]
}

object KeyEventMap extends BinderObject {
  implicit def materialize[T]: KeyEventMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[KeyEventMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Var[dom.KeyboardEvent]](c)

    reify {
      new KeyEventMap[T] {
        def asKeyEventMap(t: T) = mapExpr.splice
      }
    }
  }
}

trait TextEventMap[T] {
  def asTextEventMap(t:T):Map[String,Var[dom.TextEvent]]
}

object TextEventMap extends BinderObject {
  implicit def materialize[T]: TextEventMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[TextEventMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Var[dom.TextEvent]](c)

    reify {
      new TextEventMap[T] {
        def asTextEventMap(t: T) = mapExpr.splice
      }
    }
  }
}

trait MouseEventMap[T] {
  def asMouseEventMap(t:T):Map[String,Var[dom.MouseEvent]]
}

object MouseEventMap extends BinderObject {
  implicit def materialize[T]: MouseEventMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[MouseEventMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Var[dom.MouseEvent]](c)
    reify {
      new MouseEventMap[T] {
        def asMouseEventMap(t: T) = mapExpr.splice
      }
    }
  }
}

trait FocusEventMap[T] {
  def asFocusEventMap(t:T):Map[String,Var[dom.FocusEvent]]
}

object FocusEventMap extends BinderObject {
  implicit def materialize[T]: FocusEventMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[FocusEventMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Var[dom.FocusEvent]](c)
    reify {
      new FocusEventMap[T] {
        def asFocusEventMap(t: T) = mapExpr.splice
      }
    }
  }
}


trait DragEventMap[T] {
  def asDragEventMap(t:T):Map[String,Var[dom.DragEvent]]
}

object DragEventMap extends BinderObject {
  implicit def materialize[T]: DragEventMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[DragEventMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Var[dom.DragEvent]](c)
    reify {
      new DragEventMap[T] {
        def asDragEventMap(t: T) = mapExpr.splice
      }
    }
  }
}

trait EventMap[T] {
  def asEventMap(t:T):Map[String,Var[dom.Event]]
}

object EventMap extends BinderObject {
  implicit def materialize[T]: EventMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[EventMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Var[dom.Event]](c)
    reify {
      new EventMap[T] {
        def asEventMap(t: T) = mapExpr.splice
      }
    }
  }
}

trait UIEventMap[T] {
  def asUIEventMap(t:T):Map[String,Var[dom.UIEvent]]
}

object UIEventMap extends BinderObject {
  implicit def materialize[T]: UIEventMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[UIEventMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Var[dom.UIEvent]](c)
    reify {
      new UIEventMap[T] {
        def asUIEventMap(t: T) = mapExpr.splice
      }
    }
  }
}

trait WheelEventMap[T] {
  def asWheelEventMap(t:T):Map[String,Var[dom.WheelEvent]]
}

object WheelEventMap extends BinderObject {
  implicit def materialize[T]: WheelEventMap[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Expr[WheelEventMap[T]] = {
    import c.universe._
    val mapExpr = extract[T,Var[dom.WheelEvent]](c)
    reify {
      new WheelEventMap[T] {
        def asWheelEventMap(t: T) = mapExpr.splice
      }
    }
  }
}