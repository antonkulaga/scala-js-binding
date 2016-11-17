package org.denigma.controls.commons

import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.scalajs.dom

trait OptimizedView{
  self: BindableView =>

  def checkVisibility(): Boolean = parent match {
    case Some(par) =>
      par.elem.intersects(elem)
    case None =>
      dom.console.error("cannot find a parent for the page")
      false
  }

  def makeVisible(): Unit

  def hide(): Unit

}
