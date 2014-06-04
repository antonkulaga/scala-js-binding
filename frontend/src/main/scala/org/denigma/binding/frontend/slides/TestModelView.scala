package org.denigma.binding.frontend.slides

import org.denigma.views.ModelView
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
import org.denigma.models.AjaxStorage
import org.denigma.controls.EditableModelView

object TestModelView{
  val testProp = PropertyModel(
    properties = Map(
      IRI("http://hello.world.com")->Set(StringLiteral("HELLO WORLD")),
      IRI("http://text.com")->Set(StringLiteral("TEXT")))
  )
}

/**
 * Test model view
 * @param element
 */
class TestModelView(element:HTMLElement,   props:PropertyModel = TestModelView.testProp    ) extends EditableModelView("TestModel",element,props)
{

  this.saveClick.takeIf(dirty).handler{
    dom.console.log("it should be saved right now")
  }


  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  lazy val textEvents: Map[String, rx.Var[TextEvent]] = this.extractTextEvents(this)

  lazy val mouseEvents: Map[String, rx.Var[dom.MouseEvent]] = this.extractMouseEvents(this)
}
