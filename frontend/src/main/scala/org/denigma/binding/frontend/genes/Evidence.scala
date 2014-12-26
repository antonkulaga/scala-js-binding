package org.denigma.binding.frontend.genes

import org.denigma.binding.binders.{NavigationBinding, GeneralBinder, BasicBinding}
import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.extensions._
import org.denigma.binding.messages.ExploreMessages
import org.denigma.binding.views.{ReactiveView, OrganizedView, BindableView}
import org.denigma.semantic.binders.shex.{ArcBinder, OccursBinder, NamesBinder}
import org.denigma.semantic.grids.{ExplorableCollection, ExplorableView}
import org.denigma.semantic.models.{WithShapeView, RemoteModelView}
import org.denigma.semantic.models.collections.AjaxModelCollection
import org.denigma.semantic.rdf.{ShapeInside, ModelInside}
import org.denigma.semantic.shapes.{PropertyView, ArcView, ShapeView, ShapedModelView}
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex.{Shape, PropertyModel}
import rx._
import rx.core.Var

import scala.collection.immutable.{Map, List}
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

class Proofs( elem:HTMLElement, params:Map[String,Any])  extends ExplorableCollection("Proof",elem,params)
{


  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  val isDirty = Rx{  this.dirty().size>0  }

  override protected def attachBinders(): Unit = binders = BindableView.defaultBinders(this)

  override def loadData(explore:ExploreMessages.Explore) = {

   val models:Future[ExploreMessages.Exploration] = exploreStorage.explore(explore)

    models.onComplete {
      case Success(data) =>

        this.shape() = this.shape.now.copy(current = data.shape)
        val mod: scala.List[PropertyModel] = data.models
        items match {
          case its:Var[List[Var[ModelInside]]]=>
            its() = mod.map(d=>Var(ModelInside(d)))

          case _=>dom.console.error("items is not Var")
        }
      case Failure(m) =>
        dom.console.error(s"Future data failure for view ${this.id} with exception: \n ${m.toString}")
    }
  }

}


class Evidence( elem:HTMLElement, params:Map[String,Any]) extends ShapedModelView(elem,params){

  override def name:String = "evidence"

  override def activateMacro(): Unit = extractors.foreach(_.extractEverything(this))

  val removeClick = Var(EventBinding.createMouseEvent())

  removeClick.handler{
    this.die()
  }

  override protected def attachBinders(): Unit = binders = RemoteModelView.selectableBinders(this)

  val saveClick = Var(EventBinding.createMouseEvent())

  saveClick.takeIf(dirty).handler{
    this.saveModel()
  }


}

class EvidenceProperty(val elem:HTMLElement, val params:Map[String,Any]) extends PropertyView {

  override protected def attachBinders(): Unit = binders = PropertyView.selectableBinders(this)

  override def activateMacro(): Unit = extractors.foreach(_.extractEverything(this))

}