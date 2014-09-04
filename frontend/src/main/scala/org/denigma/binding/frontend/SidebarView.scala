package org.denigma.binding.frontend

import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.scalajs.dom.extensions.Ajax
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scalatags.Text.Tag

/**
 * View for the sitebar
 */
class SidebarView (val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView{

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}



  val logo = Var("")

  Ajax.get(sq.withHost("/logo/sidebar")).foreach{res=>
    logo()=sq.withHost(res.responseText)
    js.eval("$('.ui.accordion').accordion();")

  }

  override protected def attachBinders(): Unit =  binders =  BindableView.defaultBinders(this)
}
