package controllers.endpoints

import java.util.Date
import org.denigma.binding.messages.Suggestion
import org.denigma.endpoints.{PrickleController, AjaxModelEndpoint, AuthRequest, UserAction}
import org.denigma.semweb.shex.PropertyModel
import play.api.libs.json.Json
import play.api.mvc.{Controller, Result}
import prickle._
import prickle.Unpickler._

import scala.concurrent.Future

/**
 * This endpoint is for operatins with property models (CRUD operations) as well as sugestions of new values
 */
trait ModelEndpoint extends AjaxModelEndpoint with PrickleController with Items{
  self:Controller=>

  import org.denigma.binding.composites.BindingComposites
  import BindingComposites._
  import org.denigma.binding.messages.ModelMessages._

  override type ModelRequest = AuthRequest[ModelMessage]

  override type ModelResult = Future[Result]

  
  override def onCreate(createMessage: Create)(implicit request: ModelRequest): ModelResult = {
    items.get(createMessage.shapeId) match {
      case Some(value)=> this.items = this.items.updated(createMessage.shapeId,value++createMessage.models)
        Future.successful(pTRUE )
      //articles = articles++createMessage.models

      case None=> this.onBadModelMessage(createMessage,"NEW ITEM TYPE FOR CHANNEL IS NOT YET IMPLEMENTED") //TODO: fix
    }
  }


  def modelEndpoint() = UserAction.async(this.unpickleWith{
    str=>
      Unpickle[ModelMessage](BindingComposites.modelsMessages.unpickler).fromString(str)
  })
  {
    implicit request=>  this.onModelMessage(request.body)

  }



  override def onUpdate(updateMessage: Update)(implicit request: ModelRequest): ModelResult = {

    items.get(updateMessage.shapeId) match {
      case Some(value)=>
        this.items = this.items.updated(updateMessage.shapeId,value++updateMessage.models)
        Future.successful(pTRUE )

      case None=> this.onBadModelMessage(updateMessage,"NEW ITEM TYPE FOR CHANNEL IS NOT YET IMPLEMENTED") //TODO: fix
    }

    //this.articles = this.articles ++ updateMessage.models
  }

  override def onBadModelMessage(message: ModelMessage, reason:String): ModelResult ={

    Future.successful(BadRequest(Json.obj("status" ->"KO","message"->reason)).as("application/json"))

  }


  override def onRead(readMessage: Read)(implicit request: ModelRequest): ModelResult = {
    items.get(readMessage.shapeId)  match {
      case Some(its)=>
        val res: Seq[PropertyModel] = its.filter(i=>readMessage.resources.contains(i.id))
        val p = Pickle.intoString[Seq[PropertyModel]](res)
        Future.successful(this.pack(p))
        //Future.successful(Ok(p).as("application/json"))

      case None=>
        
        val p = Pickle.intoString(Seq.empty[PropertyModel])//rp.pickle(List.empty)

        //Future.successful(Ok(p).as("application/json"))
        Future.successful(this.pack(p))
    }
  }

  override def onDelete(deleteMessage: Delete)(implicit request: ModelRequest): ModelResult = {
    items.get(deleteMessage.shape) match {
      case Some(value)=>
        this.items = this.items.updated(deleteMessage.shape,value.filterNot(i=>i.id==deleteMessage.res))
        Future.successful(pFALSE)
      case None =>

        Future.successful(pTRUE)
      //this.articles = this.articles.filterNot(i=>i.id==deleteMessage.res)

    }
  }

  def suggestModels(items:List[PropertyModel], suggestMessage: Suggest): ModelResult  =
  {
    val t = suggestMessage.typed
    val list = for{
      i <- items
      p <- i.properties
      v <- p._2
      //if v.isInstanceOf[IRI] &&
      if v.stringValue.contains(t)
      if v.stringValue.length<256

    } yield v

   // val mes = ModelMessages.Suggestion(t,List[RDFValue](IRI("http://one"),IRI("http://tries"),IRI("http://something")),suggestMessage.id,suggestMessage.channel,new Date())
    val mes = Suggestion(t,list,suggestMessage.id,suggestMessage.channel,new Date())
    //val p = rp.pickle(mes)
    val p = Pickle.intoString[Suggestion](mes)
    Future.successful(Ok(p).as("application/json"))
  }

  override def onSuggest(suggestMessage: Suggest): ModelResult = {

    items.get(suggestMessage.shape)  match {
      case Some(list)=>
        suggestModels(list,suggestMessage)
      case None=> this.onBadModelMessage(suggestMessage)
    }


  }
}