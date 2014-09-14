package org.denigma.semantic.models

import org.denigma.binding.binders.{GeneralBinder, NavigationBinding}
import org.denigma.semantic.binders.SelectBinder
import org.denigma.semantic.storages.AjaxModelStorage
import org.scalax.semweb.rdf._
import org.scalax.semweb.shex.{ArcRule, Shape, ValueSet}

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object SelectableModelView {

  def defaultBinders(view:SelectableModelView)  =     new SelectBinder(view,view.model,view.suggest)::new GeneralBinder(view)::new NavigationBinding(view)::Nil

}


/**
 * View that binds with Selectize.js selectors
 */
trait SelectableModelView extends AjaxModelView  {

  var shape:Option[Shape] = None

  lazy val shapeRes = params.get("shape").map{
    case sh:Res=>sh

    case shex:Shape=>
      shape = Some(shex)
      shex.id.asResource

  }.get

  override def storage: AjaxModelStorage =  params.get("storage").map{case sh=>sh.asInstanceOf[AjaxModelStorage]}.get

  def resource = this.model.now.current.id


  def suggest(key:IRI,str:String): Future[List[RDFValue]] = {

    def send: () => Future[List[RDFValue]] = ()=>storage.suggest(this.shapeRes,this.model.now.current.id,key,str).map(r=>r.options)

    shape match {
      case Some(sh)=>
        //TODO: add changes to shape to make validation easier
        sh.arcRules().find(r=>r.name.matches(key)) match {
          case Some(arc:ArcRule)=>
            //TODO: add validation
            arc.value match {
              case ValueSet(values)=>
                Future.successful(values.toList)

              case _ =>send()
            }
          case None=>
            //TODO: if there is not shape everything is ok
            send()
        }
      case None=> send()
    }





  }
}


