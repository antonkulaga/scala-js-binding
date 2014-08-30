package org.denigma.semantic.controls

import org.denigma.semantic.controls.EditModelView
import org.denigma.semantic.storages.AjaxModelStorage
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf._

import scala.collection.immutable.Map
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}


/**
 * View that binds with Selectize.js selectors
 */
trait SelectableModelView extends EditModelView  {

  lazy val shapeRes = params.get("shape").map{case sh=>sh.asInstanceOf[Res]}.get

  override def storage: AjaxModelStorage =  params.get("storage").map{case sh=>sh.asInstanceOf[AjaxModelStorage]}.get

  def resource = this.modelInside.now.current.id

  var selectors = Map.empty[HTMLElement,PropertySelector]


  def typeHandler(el: HTMLElement, key: IRI)(str:String) =
    //this.storage.read()
    this.selectors.get(el) match
    {
      case Some(s)=>


        storage.suggest(this.shapeRes,this.modelInside.now.current.id,key,str).onComplete{
          case Success(sgs)=>s.updateOptions(sgs.options)
          case Failure(th)=>dom.console.error(s"type handler failure for ${key.toString()} with failure ${th.toString}")

        }
      case None=>dom.console.error(s"cannot find selector for ${key.stringValue}")
    //dom.console.log("typed = "+str)
  }


  protected override def bindRdfInput(el: HTMLElement, key: IRI): Unit =
  {

    this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (e, model) =>
      val sel = this.selectors.get(e) match {
        case Some(s)=>
          s
          //dom.console.error("second binding is not required")
        case None =>
          val s = new PropertySelector(e,key,modelInside, this.typeHandler(e,key))
          this.selectors = this.selectors + (e-> s)

          s
      }
      sel.fillValues(model)
    }
  }





}
