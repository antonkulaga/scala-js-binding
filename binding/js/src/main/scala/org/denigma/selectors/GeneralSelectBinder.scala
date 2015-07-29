package org.denigma.selectors

import org.denigma.binding.views.BindableView
import org.scalajs.dom.raw.HTMLElement
import rx.core.Var

import scala.collection.immutable.Map


trait GeneralSelectBinder
{
  type Element
  type View<:BindableView
  type Selector

  val view:View
  val model:Var[Element]
  var selectors = Map.empty[HTMLElement,Selector]
}