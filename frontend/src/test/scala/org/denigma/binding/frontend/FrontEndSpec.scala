package org.denigma.binding.frontend

import org.denigma.binding.views.BindableView
import org.denigma.binding.views.utils.ViewInjector
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement

import scala.collection.immutable.Map
import scala.scalajs.js.annotation.JSExport
import scala.util.Try

@JSExport("FrontEndSpec")
object FrontEndSpec extends BindableView with scalajs.js.JSApp{

  override def name = "main"

  lazy val elem:HTMLElement = dom.document.body

  override val params:Map[String,Any] = Map.empty


  /**
   * Register views
   */
  ViewInjector
    .register("test-general", (el, params) =>Try(new TestGeneral(el,params)))
    .register("test-collection", (el, params) =>Try(new TestCollection(el,params)))


  @JSExport
  def main(): Unit = {
    this.bindView(this.viewElement)
  }

  override def activateMacro(): Unit = {
    extractors.foreach(_.extractEverything(this))
  }

  def attachBinders() = {this.binders = BindableView.defaultBinders(this)}

}
