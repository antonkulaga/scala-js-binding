package org.denigma.binding.frontend.slides

import scala.collection.immutable.Map
import rx._
import scalatags._
import rx.core.Var
import org.scalajs.dom.{TextEvent, HTMLElement, MouseEvent}
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.rdf.{RDFValue, StringLiteral, IRI}
import org.denigma.binding.{GeneralBinding, EventBinding}
import org.scalajs.dom
import org.denigma.extensions._
import org.denigma.controls.{AjaxModelView, EditableModelView}
import org.denigma.storages.AjaxStorage
import org.denigma.views.models.ModelView
import scalatags.Text.Tag


/**
 * Test model view
 * @param element
 */
class TestModelView(element:HTMLElement,props:Map[String,Any]) extends AjaxModelView("TestModel",element,props)
{
  val saveClick: Var[MouseEvent] = Var(this.createMouseEvent())

  this.saveClick.takeIf(dirty).handler{
    //dom.console.log("it should be saved right now")
    this.saveModel()
  }


  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  lazy val textEvents: Map[String, rx.Var[TextEvent]] = this.extractTextEvents(this)

  lazy val mouseEvents: Map[String, rx.Var[dom.MouseEvent]] = this.extractMouseEvents(this)

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)
}
