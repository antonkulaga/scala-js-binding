package org.denigma.controls.semantic

import org.denigma.binding.extensions.sq
import org.denigma.binding.storages.AjaxModelStorage
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.IRI

import scala.collection.immutable.Map
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

trait AjaxLoadView extends AjaxModelView with ModelParamsLoader {


  //  def params:Map[String,Any]

  //  def path:String


  override def bindView(el:HTMLElement)
  {
    storage.read(shape)(resource).onComplete{
      case Success(model) if model.size==0=>
        dom.console.log(s"empty model received from $path")
        super.bindView(el)


      case Success(model) =>
        this.onLoadModel(model)
        super.bindView(el)

      case Failure(th)=>
        dom.console.error(s"failure in read of model for $path: \n ${th.getMessage} ")
        super.bindView(el)
    }

  }

}

trait ModelParamsLoader{

  def params:Map[String,Any]

  val path:String = params.get("path").map(v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)).get
  val resource = IRI(params("resource").toString)
  val shape = IRI(params("shape").toString)

  val storage:AjaxModelStorage = new AjaxModelStorage(path)
}
