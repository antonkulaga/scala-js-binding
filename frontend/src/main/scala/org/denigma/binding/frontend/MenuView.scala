package org.denigma.binding.frontend

import rx._
import org.scalajs.dom._
import scala.collection.immutable._
import org.denigma.binding.controls.EditableMenuView
import scalatags.Text.Tag

/**
 * Menu view, this view is devoted to displaying menus
 * @param elem html element
 * @param params view params (if any)
 */
class MenuView(elem:HTMLElement, params:Map[String,Any] = Map.empty) extends EditableMenuView("menu",elem,params)
{


  override lazy val tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override lazy val mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)
}