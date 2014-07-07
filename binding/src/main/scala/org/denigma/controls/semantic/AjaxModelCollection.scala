package org.denigma.controls.semantic

import org.denigma.binding.extensions._
import org.denigma.binding.semantic.{ActiveModelView, ModelCollection, ModelInside}
import org.denigma.binding.storages.{AjaxExploreStorage, AjaxModelStorage}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex.PropertyModel
import rx.core.{Rx, Var}

import scala.collection.immutable._
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}
import scalatags.Text._

object AjaxModelCollection
{
  type ItemView =  ActiveModelView

  def apply(html:HTMLElement,item:Var[ModelInside],storage:AjaxModelStorage, shape:Res):ItemView= {
    //
    new JustAjaxModel("item"+Math.random(),html,item,storage,shape)
  }




  class JustAjaxModel(val name:String,val elem:HTMLElement, slot:Var[ModelInside],val storage:AjaxModelStorage, val shape:Res) extends AjaxModelView{


    override val modelInside = slot

    override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

    override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

    override def mouseEvents: Predef.Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

    override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

    override def resource: Res = this.modelInside.now.current.id

  }

}

abstract class AjaxModelCollection(val name:String = "AjaxModelCollection", val elem:HTMLElement,val params:Map[String,Any])
  extends ModelCollection
{
  require(params.contains("path"),"AjaxModelCollectionView should have path view-param") //is for exploration by default
  //require(params.contains("path"),"AjaxModelCollectionView should have path view-param")

  require(params.contains("query"),"AjaxModelCollectionView should have query query-param")
  require(params.contains("shape"),"AjaxModelCollectionView should have shape shape-param")

  require(params.contains("crud"),"AjaxModelCollectionView should have crud view-param")

  override type ItemView = AjaxModelCollection.ItemView


  val query = IRI(params("query").toString)
  val shape = IRI(params("shape").toString)
  val path:String = params.get("path").map(v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)).get
  val crud:String = params.get("crud").map(v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)).get

  val exploreStorage = new AjaxExploreStorage(path,query,shape)(registry)

  val crudStorage:AjaxModelStorage = new AjaxModelStorage(crud)(registry)



//  val dirty = Rx{items().filterNot(_}

  override def newItem(item:Item):ItemView =
  {
    //dom.console.log(template.outerHTML.toString)
    val el = template.cloneNode(true).asInstanceOf[HTMLElement]

    el.removeAttribute("data-template")
    val mp: Map[String, Any] = Map[String,Any]("model"->item, "storage"->crudStorage, "shape"->this.shape)

    val view: ItemView = el.attributes.get("data-item-view") match {
      case None=>
        AjaxModelCollection.apply(el,item, this.crudStorage, this.shape)
      case Some(v)=> this.inject(v.value,el,mp) match {
        case iv:ItemView=> iv
        case iv if iv.isInstanceOf[ActiveModelView]=> iv.asInstanceOf[ActiveModelView]
        case _=>
          dom.console.error(s"view ${v.value} exists but does not inherit ItemView")
          ModelCollection.apply(el,item)
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
    val models: Future[scala.List[PropertyModel]] = exploreStorage.select(query,shape)
    models.onComplete {
      case Success(data: scala.List[PropertyModel]) => items match {
        case its:Var[List[Var[ModelInside]]]=>
          its() = data.map(d=>Var(ModelInside(d)))
        case _=>dom.console.error("items is not Var")
      }
      case Failure(m) =>
        dom.console.error(s"Future data failure for view ${this.id} with exception: \n ${m.toString}")
    }
  }




}

