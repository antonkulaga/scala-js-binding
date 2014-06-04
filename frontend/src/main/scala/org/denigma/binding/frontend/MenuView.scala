package org.denigma.binding.frontend

import rx._
import org.scalajs.dom._
import scala.collection.immutable._
import scalatags.HtmlTag
import org.denigma.controls.EditableMenuView

/**
 * Menu view, this view is devoted to displaying menus
 * @param el html element
 * @param params view params (if any)
 */
class MenuView(el:HTMLElement, params:Map[String,Any] = Map.empty) extends EditableMenuView("menu",el,params)
{

  override lazy val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override lazy val mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override lazy val  lists: Map[String, Rx[scala.List[Map[String, Any]]]] = this.extractListRx(this)
}