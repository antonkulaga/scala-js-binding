package org.denigma.semantic.models

import org.denigma.binding.binders.{GeneralBinder, NavigationBinding}
import org.denigma.binding.messages.ModelMessages.Suggestion
import org.denigma.binding.views.BindableView
import org.denigma.semantic.models.binders.{PropertySelector, SelectBinder}
import org.denigma.semantic.rdf.ModelInside
import org.denigma.semantic.storages.AjaxModelStorage
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf._
import rx.Var

import scala.collection.immutable.Map
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

object SelectableModelView {

  def defaultBinders(view:SelectableModelView)  =     new SelectBinder(view,view.modelInside,view.suggest)::new GeneralBinder(view)::new NavigationBinding(view)::Nil

}


/**
 * View that binds with Selectize.js selectors
 */
trait SelectableModelView extends EditModelView with AjaxModelView  {

  lazy val shapeRes = params.get("shape").map{case sh=>sh.asInstanceOf[Res]}.get

  override def storage: AjaxModelStorage =  params.get("storage").map{case sh=>sh.asInstanceOf[AjaxModelStorage]}.get

  def resource = this.modelInside.now.current.id


  def suggest(key:IRI,str:String): Future[Suggestion] = {

    storage.suggest(this.shapeRes,this.modelInside.now.current.id,key,str)

  }
}


