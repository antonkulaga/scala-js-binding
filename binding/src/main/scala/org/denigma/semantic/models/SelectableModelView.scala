package org.denigma.semantic.models

import org.denigma.binding.binders.{GeneralBinder, NavigationBinding}
import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.SelectBinder
import org.denigma.semantic.storages.{ModelStorage, AjaxModelStorage}
import org.scalax.semweb.rdf._
import org.scalax.semweb.shex.{AndRule, ArcRule, Shape, ValueSet}

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object SelectableModelView {

  def defaultBinders(view:SelectableModelView)  =     new SelectBinder(view,view.model,view.suggest)::new GeneralBinder(view)::new NavigationBinding(view)::Nil

}



/**
 * View that binds with Selectize.js selectors
 */
trait SelectableModelView extends RemoteModelView 
{

  def suggest(key: IRI, str: String): Future[List[RDFValue]] =  this.shape.now.current match {
      case Shape.empty=> storage.suggest(this.shapeRes, this.model.now.current.id, key, str).map(r => r.options)

      case Shape(id,andrule) if andrule == AndRule.empty=>  storage.suggest(this.shapeRes, this.model.now.current.id, key, str).map(r => r.options)

      case sh=> this.suggest(sh)(key, str)

    }




  def suggest(shape:Shape)(key:IRI,str:String): Future[List[RDFValue]] =  shape
    .arcRules().find(r=>r.name.matches(key)) match {
      case Some(arc:ArcRule)=>
        //TODO: add validation
        arc.value match {
          case ValueSet(values)=>
            Future.successful(values.toList)

          case _ => storage.suggest(shape.id.asResource, this.model.now.current.id, key, str).map(r => r.options)

        }
    }

}


