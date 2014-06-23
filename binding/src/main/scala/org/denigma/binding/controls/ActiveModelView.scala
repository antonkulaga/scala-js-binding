package org.denigma.binding.controls

import org.denigma.binding.semantic.ModelView
import org.denigma.binding.views.OrdinaryView

import scala.collection.immutable.Map
import rx._
import rx.core.Var
import org.scalajs.dom.HTMLElement
import org.scalajs.dom.MouseEvent
import org.denigma.binding.storages.AjaxModelStorage
import org.scalax.semweb.rdf.IRI
import org.denigma.binding.extensions.sq
import scala.util.{Failure, Success}
import org.scalajs.dom
import org.scalax.semweb.shex.PropertyModel
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

trait ActiveModelView extends OrdinaryView with ModelView
{



  override protected def otherPartial:PartialFunction[String,Unit] = {case _=>}

  override def bindDataAttributes(el:HTMLElement,ats:Map[String, String]) = {
    this.bindProperties(el,ats)
    this.bindEvents(el,ats)
  }


  //TODO: rewrite
  override def bindProperties(el:HTMLElement,ats:Map[String, String]): Unit = for {
    (key, value) <- ats
  }{
    this.visibilityPartial(el,value)
      .orElse(this.classPartial(el,value))
      .orElse(this.propertyPartial(el,key.toString,value))
      .orElse(this.loadIntoPartial(el,value))
      .orElse(this.otherPartial)(key.toString)//key.toString is the most important!
  }

  val dirty = Rx{!this.modelInside().isUnchanged}

}

abstract class AjaxModelView(val name:String = "AjaxModel", val elem:HTMLElement,params:Map[String,Any]) extends ActiveModelView{

  self=>

  val path:String = params.get("path").map(v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)).get
  val resource = IRI(params("resource").toString)
  val shape = IRI(params("shape").toString)

  val storage = new AjaxModelStorage(path)

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

  def saveModel() = {
    if(this.modelInside.now.isUnchanged)
    {
      dom.console.log("trying to save unchanged model")
    }
    else {
      storage.update(this.shape,overWrite = true)(modelInside.now.current).onComplete{
        case Failure(th)=>
          dom.console.error(s"failure in saving of movel on $path: \n ${th.getMessage} ")
        case Success(bool)=>
        {
          if(bool) this.modelInside() = this.modelInside.now.refresh else dom.console.log(s"the model was not saved")
        }

      }
    }
  }


  /**
   * Handler on model load
   * @param items
   */
  protected def onLoadModel(items:List[PropertyModel]) = {
    if(items.size>1) dom.console.error(s"more than one model received from $path for onemodel binding")
    val model = items.head
    this.modelInside() = this.modelInside.now.copy(model,model)
  }

}