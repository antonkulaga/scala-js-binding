package org.denigma.binding.frontend

import org.denigma.binding.views.BindableView
import org.scalajs.dom.raw.HTMLElement
import rx.core.Var

/**
 * TODO: complete the test
 * @param elem
 * @param params
 */
class TestCollection(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView
{

  val string1 = Var("string1works!")

  val boolean1 = Var(true)

  val href = Var("http://link.works.com")

  override protected def attachBinders(): Unit = binders = BindableView.defaultBinders(this)
  /**
   * is used to fill in all variables extracted by macro
   * usually it is just
   * this.extractEverything(this)
   */
  override def activateMacro(): Unit = {
    this.extractors.foreach(e=>e.extractEverything(this))
  }
}
