package org.denigma.semantic.models

import org.denigma.binding.extensions._
import org.denigma.binding.messages.{ExploreMessages, Filters}
import org.denigma.semantic.rdf.ModelInside
import org.denigma.semantic.storages.{AjaxExploreStorage, AjaxModelStorage}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex._
import rx.core.{Rx, Var}

import scala.collection.immutable._
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}
object AjaxModelCollection
{
  type ItemView =  ModelView

  def apply(html:HTMLElement,params:Map[String,Any]):ItemView= {
    //
    new JustRemoteModel("item"+Math.random(),html,params)
  }

  class JustRemoteModel(override val name:String,val elem:HTMLElement, val params:Map[String,Any]) extends RemoteModelView{


    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

    override protected def attachBinders(): Unit = binders = RemoteModelView.defaultBinders(this)
  }

}

abstract class AjaxModelCollection(override val name:String,val elem:HTMLElement,val params:Map[String,Any])
  extends ModelCollection with WithShapeView
{


  override type ItemView = AjaxModelCollection.ItemView

  val query = this.resolveKey("query"){case q=>IRI(q.toString)} //IRI(params("query").toString)

  val path:String = this.resolveKey("path"){
    case v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)
  }
  val crud:String = this.resolveKey("crud"){
      case v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)
    }


  val exploreStorage = new AjaxExploreStorage(path)(registry)


  val crudStorage= new AjaxModelStorage(crud)(registry)


  //filters:List[Filters.Filter] = List.empty[Filters.Filter] , searchTerms:List[String] = List.empty[String], sortOrder:List[Sort] = List.empty[Sort]

  val propertyFilters = Var(Map.empty[IRI,Filters.Filter])

  val explorer:Rx[ExploreMessages.Explore] = Var(ExploreMessages.Explore(
    this.query, this.shapeRes, id= this.exploreStorage.genId(),channel = exploreStorage.channel
  )  )

  /**
   * Loads data fro the server
   */
  def loadData(explore:ExploreMessages.Explore) = {
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


  //  val dirty = Rx{items().filterNot(_}

  override def newItem(item:Item):ItemView = {
    item.handler(onItemChange(item))
    this.constructItem(item,Map("model"->item, "storage"->crudStorage, "shape"->this.shape)){ (el,mp)=>
      item.handler(onItemChange(item))
      AjaxModelCollection.apply(el,mp)
    }
  }


  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  override def bindView(el: HTMLElement) = {

    super.bindView(el)
    this.loadData(this.explorer.now)
  }




}

