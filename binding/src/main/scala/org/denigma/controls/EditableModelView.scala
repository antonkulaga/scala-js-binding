package org.denigma.controls

import scala.collection.immutable.Map
import rx._
import rx.core.Var
import org.scalajs.dom.HTMLElement
import org.scalajs.dom.MouseEvent
import org.denigma.views.models.ModelView
import org.denigma.views.core.OrdinaryView
import org.denigma.storages.AjaxModelStorage
import org.scalax.semweb.rdf.IRI
import org.denigma.extensions.sq
import scala.util.{Failure, Success}
import org.scalajs.dom
import org.scalax.semweb.shex.PropertyModel
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

trait EditableModelView extends ModelView
{
  self:OrdinaryView=>



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

  val saveClick: Var[MouseEvent] = Var(this.createMouseEvent())

}

abstract class AjaxModelView(name:String = "AjaxModel", element:HTMLElement,params:Map[String,Any]) extends OrdinaryView(name:String,element) with EditableModelView{

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