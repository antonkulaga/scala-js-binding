package org.denigma.semantic.models

import org.denigma.binding.binders.{GeneralBinder, NavigationBinding}
import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.{SelectBinder, ModelBinder}
import org.denigma.semantic.rdf.{ShapeInside, ModelInside}
import org.denigma.semantic.storages.{ModelStorage, AjaxModelStorage}
import org.scalajs.dom
import org.scalax.semweb.rdf.{RDFValue, IRI, Res}
import org.scalax.semweb.shex._
import rx.core.Var

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}



object RemoteModelView
{
  def defaultBinders(view:ModelView)  =new ModelBinder(view,view.model)::new GeneralBinder(view)::new NavigationBinding(view)::Nil

  def selectableBinders(view:RemoteModelView)  =new SelectBinder(view,view.model,view.suggest)::new GeneralBinder(view)::new NavigationBinding(view)::Nil

}

trait  RemoteModelView extends ModelView  with BindableView with WithShapeView
{

  override val model = this.modelOption.orElse{
    params.get("resource").map{ case res:Res=> Var(ModelInside(PropertyModel(res)))   }
  }.getOrElse(Var(ModelInside(PropertyModel.empty)))



  def storage:ModelStorage = this.resolveKey("storage"){case m:ModelStorage=>m}



  def saveModel() = {
    if(this.model.now.isUnchanged)
    {
      dom.console.log("trying to save unchanged model")
    }
    else {
      storage.update(this.shapeRes,overWrite = true)(model.now.current).onComplete{
        case Failure(th)=>
          dom.console.error(s"failure in saving of movel with channel $storage.channel: \n ${th.getMessage} ")
        case Success(bool)=>
        {
          if(bool) this.model() = this.model.now.refresh else dom.console.log(s"the model was not saved")
        }

      }
    }
  }


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


  /**
   * Handler on model load
   * @param items
   */
  protected def onLoadModel(items:List[PropertyModel]) = {
    if(items.size>1) dom.console.error(s"more than one model received from ${storage.channel} for onemodel binding")
    val m = items.head
    this.model() = this.model.now.copy(m,m)
  }

}

