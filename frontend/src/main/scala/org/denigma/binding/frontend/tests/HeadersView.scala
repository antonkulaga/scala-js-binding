package org.denigma.binding.frontend.tests

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.collections.ListCollectionView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import rx.Rx
import rx.core.Var

/**
 * To test if list is working correcty
 * @param elem
 * @param params
 */
class HeadersView(val elem:HTMLElement, val params:Map[String,Any]) extends ListCollectionView{





  override val items: Var[List[(String, String)]] = Var(List("one"->"is one","two"->"is two","three"->"is three"))

  override protected def attachBinders(): Unit = this.withBinders(new GeneralBinder(this))

  /**
   * is used to fill in all variables extracted by macro
   * usually it is just
   * this.extractEverything(this)
   */
  override def activateMacro(): Unit = this.extractors.foreach(e=>e.extractEverything(this))
}
