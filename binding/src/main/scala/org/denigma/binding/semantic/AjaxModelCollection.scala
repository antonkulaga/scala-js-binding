package org.denigma.binding.semantic

import org.denigma.binding.extensions.sq
import org.denigma.binding.picklers.rp
import org.denigma.binding.storages.AjaxModelQueryStorage
import org.scalajs.dom
import org.scalajs.dom._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex.PropertyModel
import rx.Var

import scala.collection.immutable._
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}


abstract class AjaxModelCollection(val name:String = "AjaxModelCollection", val elem:HTMLElement,val params:Map[String,Any])
  extends ModelCollection
{
  require(params.contains("path"),"AjaxModelCollectionView should have path view-param")
  require(params.contains("query"),"AjaxModelCollectionView should have query query-param")
  require(params.contains("shape"),"AjaxModelCollectionView should have shape shape-param")

  val query = IRI(params("query").toString)
  val shape = IRI(params("shape").toString)
  val path:String = params.get("path").map(v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)).get

  val storage = new AjaxModelQueryStorage(path)(registry)

  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  override def bindView(el: HTMLElement) = {

    super.bindView(el)
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

