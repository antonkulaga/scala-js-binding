package org.denigma.binding.binders.collections

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.BindableView
import rx._

import scala.collection.immutable.Map


object KeyValue {


  class StringBinder(view: BindableView, key: String, value: String) extends StringRxBinder(view, key, Var(value))

  class StringRxBinder(view: BindableView, key: String, value: Rx[String]) extends GeneralBinder(view) {
    strings = Map(key -> value, "key" -> Var(key), "value" -> value)
  }

  class BooleanBinder(view: BindableView, key: String, value: Boolean) extends BooleanRxBinder(view, key, Var(value))

  class BooleanRxBinder(view: BindableView, key: String, value: Rx[Boolean]) extends GeneralBinder(view) {
    strings = Map("key" -> Var(key))
    bools = Map[String, Rx[Boolean]](key -> value, "value" -> value)
  }


}