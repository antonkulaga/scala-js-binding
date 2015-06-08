package org.denigma.selectors

import org.denigma.binding.commons.ILogged
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.selectize.Selectize
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import rx.core.Var

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll


trait GeneralSelectBinder
{
  type Element
  type View<:BindableView
  type Selector

  val view:View
  val model:Var[Element]
  var selectors = Map.empty[HTMLElement,Selector]
}