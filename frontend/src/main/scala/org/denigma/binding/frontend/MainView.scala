/*
package org.denigma.binding.frontend

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.frontend.FrontEnd._
import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.jquery._
import rx._

import scala.collection.immutable.Map


class MainView extends BindableView{

  override def name = "main"

  lazy val elem:HTMLElement = dom.document.body

  override val params:Map[String,Any] = Map.empty


  override def activateMacro(): Unit = {
    extractors.foreach(_.extractEverything(this))
  }

  def attachBinders() = {this.binders = BindableView.defaultBinders(this)}

  val toggle: Var[MouseEvent] = Var(EventBinding.createMouseEvent())

  toggle.handler{
    jQuery(".left.sidebar").dyn.sidebar(sidebarParams).sidebar("toggle")
  }
}
*/
