package org.denigma.controls

import org.denigma.binding.picklers.rp
import org.denigma.extensions._
import org.denigma.storages.AjaxModelQueryStorage
import org.denigma.views.core.{BindingView, OrdinaryView}
import org.denigma.views.lists.CollectionView
import org.denigma.views.models.{ModelInside, RDFView, ModelView}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex.PropertyModel
import rx.{Rx, Var}

import scala.collection.immutable._
import scala.util.{Failure, Success}
import scalatags.Text.Tag
import scalajs.concurrent.JSExecutionContext.Implicits.queue

object AjaxModelCollection
{
 type ItemView =  BindingView with ModelView

  def apply(html:HTMLElement,model:PropertyModel):ItemView= {
    //
    new JustModel("item"+Math.random(),model,html)
  }


  class JustModel(name:String,model:PropertyModel,html:HTMLElement) extends OrdinaryView(name,html) with ModelView{


    override val modelInside = Var(ModelInside( model))

    override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

    override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

    override def mouseEvents: Predef.Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

    override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)
  }

}

abstract class AjaxModelCollection(name:String = "AjaxModelCollection", element:HTMLElement,params:Map[String,Any])
  extends OrdinaryView(name:String,element) with CollectionView
{
  require(params.contains("path"),"AjaxModelCollectionView should have query view-param")
  require(params.contains("query"),"AjaxModelCollectionView should have path query-param")
  require(params.contains("shape"),"AjaxModelCollectionView should have shape shape-param")


  val path:String = params.get("path").map(v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)).get
  val query = IRI(params("query").toString)
  val shape = IRI(params("shape").toString)

  val registry = rp

  val storage = new AjaxModelQueryStorage(path)(registry)

  override type Item = PropertyModel
  override type ItemView =  AjaxModelCollection.ItemView

  override val items = Var(List.empty[PropertyModel])


  override def newItem(item:Item):ItemView = {
    val el = template.cloneNode(true).asInstanceOf[HTMLElement]
    val mp: Map[String, Any] = Map[String,Any]("model"->item)

    val view = el.attributes.get("item-view") match {
      case None=>
        AjaxModelCollection.apply(el,item)
      case Some(v)=> this.inject(v.value,el,mp) match {
        case item:ItemView=> item
        case _=>
          dom.console.error(s"view ${v.value} exists but does not inherit MapView")
          AjaxModelCollection.apply(el,item)
      }
    }
    view
  }

  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  override def bindView(el: HTMLElement) = {

    rp.registerPicklers()
    val models = storage.select(query,shape)
    models.onComplete {
      case Success(data) => items match {
        case its:Var[List[PropertyModel]]=>
          its() = data
          this.updateItems()
        case _=>dom.console.error("items is not Var")
      }
      case Failure(m) =>
        dom.console.error(s"Future data failure for view ${this.id} with exception: \n ${m.toString}")
        super.bindView(el)
    }
  }

  protected def updateItems() = {

    val id = "items_of_"+this.viewElement.id
    val span: HTMLElement = sq.byId(id) match {
      case Some(el)=>el
      case None=>
        val sp = document.createElement("span")
        sp.id = id
        if(template==viewElement) viewElement.appendChild(sp) else viewElement.replaceChild(sp,template)
        sp
    }
    //items.now.foreach(i=>dom.console.log(i.toString))
    val viewElements: List[ItemView] = this.items.now.map(this.newItem)

    viewElements.foreach{i=>
      this.addView(i)

      //viewElement.appendChild(i.viewElement)
      viewElement.insertBefore(i.viewElement,span)
      //viewElement.insertAdjacentviewElement()
      i.bindView(i.viewElement)
    }

    viewElement.children.collect{case el:HTMLElement=>el}.foreach(bind)

  }


}
