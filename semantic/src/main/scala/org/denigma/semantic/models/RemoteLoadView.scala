package org.denigma.semantic.models

import org.denigma.binding.extensions.sq
import org.denigma.semantic.storages.AjaxModelStorage
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.denigma.semweb.rdf.IRI

import scala.collection.immutable.Map
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

trait RemoteLoadView extends RemoteModelView {


  //  def params:Map[String,Any]

  //  def path:String

  def params:Map[String,Any]

  val path:String = this.resolveKey("path"){
    case v:String if v.contains(":") =>v
    case v =>sq.withHost(v.toString)
  }

  val resource = this.resolveKey("resource")
  {
    case res:IRI=>res
    case res:String=>IRI(res)
    case other=>
      dom.console.error(s"unknown resource type in $id with tostring ${other.toString}")
      IRI(other.toString)
  }

  override val storage:AjaxModelStorage = new AjaxModelStorage(path)

  override def bindView(el:HTMLElement)
  {
    storage.read(shapeRes.now)(resource).onComplete{
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
