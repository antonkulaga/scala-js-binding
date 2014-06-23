package org.denigma.binding.frontend.slides

import org.denigma.binding.semantic.ModelView

import scala.collection.immutable.Map
import rx._
import scalatags._
import rx.core.Var
import org.scalajs.dom.{TextEvent, HTMLElement, MouseEvent}
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.rdf.{RDFValue, StringLiteral, IRI}
import org.scalajs.dom
import org.denigma.binding.extensions._
import org.denigma.binding.controls.{EditModelView, AjaxModelView, ActiveModelView}
import org.denigma.binding.storages.AjaxStorage
import scalatags.Text.Tag
import scala.scalajs.js
import js.Dynamic.{ global => g, newInstance => jsnew }

class PageEditView(elem:HTMLElement,val params:Map[String,Any]) extends AjaxModelView("PageModel",elem,params) with EditModelView
{

  this.saveClick.takeIf(dirty).handler{
    //dom.console.log("it should be saved right now")
    this.saveModel()
  }


  this.toggleClick.handler{
    this.editMode() = !this.editMode.now
  }


  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  lazy val textEvents: Map[String, rx.Var[TextEvent]] = this.extractTextEvents(this)

  lazy val mouseEvents: Map[String, rx.Var[dom.MouseEvent]] = this.extractMouseEvents(this)

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)
}
