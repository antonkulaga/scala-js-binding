package org.denigma.binding.frontend

import rx._
import org.scalajs.dom
import org.scalajs.dom._
import scala.collection.immutable._
import org.denigma.views._
import org.denigma.extensions._
import scala.util.Success
import scala.util.Failure
import scalatags.HtmlTag
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.scalax.semweb.rdf.IRI
import org.denigma.binding.models._
import shared._
import org.denigma.models.AjaxStorage
import scala.concurrent.Future
import org.denigma.tools.{EditableMenuView, AjaxMenuView}

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

  override lazy val mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)

  override lazy val  lists: Map[String, Rx[scala.List[Map[String, Any]]]] = this.extractListRx(this)
}