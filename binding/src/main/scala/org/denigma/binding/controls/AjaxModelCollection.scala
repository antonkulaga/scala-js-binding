package org.denigma.binding.controls

import org.denigma.binding.picklers.rp
import org.denigma.binding.extensions._
import org.denigma.binding.semantic.{ModelCollection, ModelInside, ModelView}
import org.denigma.binding.storages.AjaxModelQueryStorage
import org.denigma.binding.views.{CollectionView, OrdinaryView, BindingView}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex.PropertyModel
import rx.{Rx, Var}

import scala.collection.immutable._
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalatags.Text.Tag
import scalajs.concurrent.JSExecutionContext.Implicits.queue

object AjaxModelCollection
{
 type ItemView =  BindingView with ModelView

  def apply(html:HTMLElement,item:Var[ModelInside]):ItemView= {
    //
    new JustModel("item"+Math.random(),item,html)
  }




  class JustModel(val name:String,slot:Var[ModelInside],val elem:HTMLElement) extends OrdinaryView with ModelView{


    override val modelInside = slot

    override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

    override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

    override def mouseEvents: Predef.Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

    override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)
  }

}

abstract class AjaxModelCollection(val name:String = "AjaxModelCollection", val elem:HTMLElement,val params:Map[String,Any])
  extends ModelCollection
{
  val storage = new AjaxModelQueryStorage(path)(registry)

  require(params.contains("path"),"AjaxModelCollectionView should have path view-param")
  require(params.contains("query"),"AjaxModelCollectionView should have query query-param")
  require(params.contains("shape"),"AjaxModelCollectionView should have shape shape-param")

  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  override def bindView(el: HTMLElement) = {

    rp.registerPicklers()
    super.bindView(el)
    this.subscribeUpdates()
    val models: Future[scala.List[PropertyModel]] = storage.select(query,shape)
    models.onComplete {
      case Success(data) => items match {
        case its:Var[List[Var[ModelInside]]]=>
          its() = data.map(d=>Var(ModelInside(d)))
        case _=>dom.console.error("items is not Var")
      }
      case Failure(m) =>
        dom.console.error(s"Future data failure for view ${this.id} with exception: \n ${m.toString}")
    }
  }




}

