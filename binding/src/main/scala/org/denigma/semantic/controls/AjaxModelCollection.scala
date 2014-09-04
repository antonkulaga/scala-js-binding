package org.denigma.semantic.controls

import org.denigma.binding.extensions._
import org.denigma.binding.messages.{ExploreMessages, Filters}
import org.denigma.semantic.binding.{ModelView, ModelCollection, ModelInside}
import org.denigma.semantic.controls.AjaxModelView
import org.denigma.semantic.storages.{AjaxExploreStorage, AjaxModelStorage}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex._
import rx.core.{Rx, Var}

import scala.collection.immutable._
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}
import scalatags.Text._
import org.scalax.semweb.rdf.vocabulary
object AjaxModelCollection
{
  type ItemView =  ModelView

  def apply(html:HTMLElement,item:Var[ModelInside],storage:AjaxModelStorage, shape:Res):ItemView= {
    //
    new JustAjaxModel("item"+Math.random(),html,item,storage,shape)
  }




  class JustAjaxModel(override val name:String,val elem:HTMLElement, slot:Var[ModelInside],val storage:AjaxModelStorage, val shapeRes:Res) extends AjaxModelView{


    override val modelInside = slot

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}



    override def resource: Res = this.modelInside.now.current.id

    override def params: Map[String, Any] = Map.empty
  }

}

abstract class AjaxModelCollection(override val name:String,val elem:HTMLElement,val params:Map[String,Any])
  extends ModelCollection
{
  require(params.contains("path"),"AjaxModelCollectionView should have path view-param") //is for exploration by default
  //require(params.contains("path"),"AjaxModelCollectionView should have path view-param")

  require(params.contains("query"),"AjaxModelCollectionView should have query query-param")
  require(params.contains("shape"),"AjaxModelCollectionView should have shape shape-param")

  require(params.contains("crud"),"AjaxModelCollectionView should have crud view-param")

  override type ItemView = AjaxModelCollection.ItemView

  val path:String = params.get("path").map(v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)).get
  val crud:String = params.get("crud").map(v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)).get


  val query = IRI(params("query").toString)



  val shapeRes = IRI(params("shape").toString)

  lazy val emptyShape = new Shape(IRILabel(shapeRes), AndRule(Set.empty[Rule], IRILabel(WI.pl("empty")) ))

  val shape:Var[Shape] = Var(emptyShape)



  val exploreStorage = new AjaxExploreStorage(path)(registry)
  val crudStorage= new AjaxModelStorage(crud)(registry)


  //filters:List[Filters.Filter] = List.empty[Filters.Filter] , searchTerms:List[String] = List.empty[String], sortOrder:List[Sort] = List.empty[Sort]

  val propertyFilters = Var(Map.empty[IRI,Filters.Filter])

  val explorer:Rx[ExploreMessages.Explore] = Var(ExploreMessages.Explore(
    this.query, this.shapeRes, id= this.exploreStorage.genId(),channel = exploreStorage.channel
  )
  )

  /**
   * Loads data fro the server
   */
  def loadData(explore:ExploreMessages.Explore) = {
    val models:Future[ExploreMessages.Exploration] = exploreStorage.explore(explore)

    models.onComplete {
      case Success(data) =>
        this.shape() = data.shape
        val mod = data.models
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

  override def newItem(item:Item):ItemView =
  {
    //dom.console.log(template.outerHTML.toString)
    val el = template.cloneNode(true).asInstanceOf[HTMLElement]

    el.removeAttribute("data-template")
    val mp: Map[String, Any] = Map[String,Any]("model"->item, "storage"->crudStorage, "shape"->this.shapeRes)

    val view: ItemView = el.attributes.get("data-item-view") match {
      case None=>
        AjaxModelCollection.apply(el,item, this.crudStorage, this.shapeRes)
      case Some(v)=> this.inject(v.value,el,mp) match {
        case iv:ItemView=> iv
        case iv if iv.isInstanceOf[PropertyModelView]=> iv.asInstanceOf[PropertyModelView]
        case _=>
          dom.console.error(s"view ${v.value} exists but does not inherit ItemView")
          ModelCollection.apply(el,item)
          //AjaxModelCollection.apply(el,item, this.crudStorage, this.shapeRes) //TODO: check if works at all
      }
    }
    item.handler(onItemChange(item))
    view
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

