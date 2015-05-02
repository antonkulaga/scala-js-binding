package org.denigma.semantic.controls.datagrids


import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.extensions._
import org.denigma.binding.messages.ExploreMessages
import org.denigma.binding.views.BindableView
import org.denigma.semantic.grids.ExplorableCollection
import org.denigma.semantic.models.RemoteModelView
import org.denigma.semantic.rdf.ModelInside
import org.denigma.semantic.shapes.{PropertyView, ShapedModelView}
import org.denigma.semweb.shex.PropertyModel
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import rx._
import rx.core.Var

import scala.collection.immutable.{List, Map}
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

abstract class DataGrid( elem:HTMLElement, params:Map[String,Any])  extends ExplorableCollection("DataGrid",elem,params)
{

  val saveAll= Var(EventBinding.createMouseEvent())

  saveAll.takeIf(isDirty).handler{

    //dom.console.log("SAVE DOES WORK")

    this.crudStorage.update(this.shapeRes.now,overWrite = true)(this.dirty.now.map(m=>m.now.current):_*).onComplete{
      case Failure(th)=>
        dom.console.error(s"failure in saving of movel with channel ${crudStorage.channel}: \n ${th.getMessage} ")
      case Success(bool)=>
        dom.console.log(s"response received $bool")
        if(bool) for{ms <-this.dirty.now}  ms() = ms.now.refresh else dom.console.log(s"All changed models cannot be savedthe model was not saved")


    }}


  lazy val isDirty = Rx{  this.dirty().size>0  }

  override def loadData(explore:ExploreMessages.Explore) = {

    val models:Future[ExploreMessages.Exploration] = exploreStorage.explore(explore)

    models.onComplete {
      case Success(data) =>

        this.shapeInside() = this.shapeInside.now.copy(current = data.shape)
        val mod: scala.List[PropertyModel] = data.models
        items match {
          case its:Var[List[Var[ModelInside]]]=>
            //dom.console.error("ITEMS = \n"+its.now.mkString("\n"))
            its() = mod.map(d=>Var(ModelInside(d)))

          case _=>dom.console.error("items is not Var")
        }
      case Failure(m) =>
        dom.console.error(s"Future data failure for view ${this.id} with exception: \n ${m.toString}")
    }
  }

  lazy val add = Var(EventBinding.createMouseEvent())

  add.handler{
    dom.console.log("ADD CLICK WORKS!")
    //val element = Var(ModelInside())
    //this.addItem(Var())
  }

}


class GridRow( elem:HTMLElement, params:Map[String,Any]) extends ShapedModelView(elem,params){

  override def name:String = "grid_row"

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

class GridCell(val elem:HTMLElement, val params:Map[String,Any]) extends PropertyView {

  override protected def attachBinders(): Unit = binders = PropertyView.selectableBinders(this)

  override def activateMacro(): Unit = extractors.foreach(_.extractEverything(this))

}