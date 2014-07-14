package org.denigma.binding.views

import org.scalajs.dom.HTMLElement

trait IView {

  def id:String

  def viewElement: HTMLElement

  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  def bindView(el:HTMLElement):Unit


  def unbindView():Unit

}
