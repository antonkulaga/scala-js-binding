package org.denigma.binding.frontend.controls

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.{BindingEvent, JustPromise, PromiseEvent}
import org.denigma.semantic.models.WithShapeView
import org.denigma.semantic.rdf.ShapeInside
import org.denigma.semantic.shapes.{PropertyView, ArcView, ShapeView}
import org.denigma.semantic.storages.ShapeStorage
import org.scalajs.dom.{MouseEvent, HTMLElement}
import org.scalax.semweb.rdf.vocabulary.{RDF, XSD}
import org.scalax.semweb.rdf.{IRI, RDFValue, vocabulary}
import org.scalax.semweb.shex._
import rx._
import rx.extensions._
import org.scalajs.dom
import rx.ops._
import scala.concurrent.Promise
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.denigma.binding.extensions._

import scala.util.{Failure, Success}

object EditableShape
{

  lazy val emptyArcRule: ArcRule =  ArcRule(propertyName = RDF.VALUE)

  def apply(elem:HTMLElement,mp:Map[String,Any]) = {
    new ShapeProperty(elem,mp)
  }


}

class EditableShape (val elem:HTMLElement,val params:Map[String,Any]) extends  ShapeView with WithShapeView
{


  override def newItem(item:Item):ItemView = this.constructItem(item,Map("item"->item)) { (e,m)=>
    ArcView.apply(e,m)
  }

  val onShapeChange = Obs(shapeInside,skipInitial = false){
    val cur = shapeInside.now.current
    updateShape(cur)
  }




  val addClick: Var[MouseEvent] = Var(EventBinding.createMouseEvent())

  lazy val onAddClick = rx.extensions.AnyRx(addClick).handler{
    val item: Var[ArcRule] = Var(EditableShape.emptyArcRule)
    this.rules() = rules.now + item
  }



  val applyShape = Var(EventBinding.createMouseEvent())


  override protected def attachBinders(): Unit = this.withBinders(ShapeView.defaultBinders(this))

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}



}

