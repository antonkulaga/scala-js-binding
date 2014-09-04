package org.denigma.binding.frontend

import org.denigma.binding.views.BindableView
import org.denigma.controls.general.EditableMenuView
import rx._
import org.scalajs.dom._
import scala.collection.immutable._
import scalatags.Text.Tag

/**
 * Menu view, this view is devoted to displaying menus
 * @param elem html element
 * @param params view params (if any)
 */
class MenuView(elem:HTMLElement, params:Map[String,Any] = Map.empty) extends EditableMenuView("menu",elem,params)
{
    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit = BindableView.defaultBinders(this)
}