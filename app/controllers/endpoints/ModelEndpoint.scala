package controllers.endpoints

import java.util.Date
import controllers.endpoints.Items
import org.denigma.binding.messages.Suggestion
import org.denigma.binding.messages.ModelMessages._
import org.denigma.binding.picklers.rp
import org.denigma.binding.play.{AjaxModelEndpoint, AuthRequest, PickleController, UserAction}
import org.scalajs.spickling.playjson._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex.PropertyModel
import play.Logger
import play.api.libs.json.Json
import play.api.mvc.{Controller, Result}

import scala.concurrent.Future


trait ModelEndpoint extends AjaxModelEndpoint with PickleController with Items{
  self:Controller=>


  override type ModelRequest = AuthRequest[ModelMessage]

  override type ModelResult = Future[Result]


  override def onCreate(createMessage: Create)(implicit request: ModelRequest): ModelResult = {
    items.get(createMessage.shapeId) match {
      case Some(value)=> this.items = this.items.updated(createMessage.shapeId,value++createMessage.models)
        Future.successful(TRUE )
      //articles = articles++createMessage.models

      case None=> this.onBadModelMessage(createMessage,"NEW ITEM TYPE FOR CHANNEL IS NOT YET IMPLEMENTED") //TODO: fix
    }
  }


  def modelEndpoint() = UserAction.async(this.pickle[ModelMessage]()){implicit request=>
    this.onModelMessage(request.body)

  }



  override def onUpdate(updateMessage: Update)(implicit request: ModelRequest): ModelResult = {

    items.get(updateMessage.shapeId) match {
      case Some(value)=>
        this.items = this.items.updated(updateMessage.shapeId,value++updateMessage.models)
        Future.successful(TRUE )

      case None=> this.onBadModelMessage(updateMessage,"NEW ITEM TYPE FOR CHANNEL IS NOT YET IMPLEMENTED") //TODO: fix
    }

    //this.articles = this.articles ++ updateMessage.models
  }

  override def onBadModelMessage(message: ModelMessage, reason:String): ModelResult ={

    Future.successful(BadRequest(Json.obj("status" ->"KO","message"->reason)).as("application/json"))

  }


  override def onRead(readMessage: Read)(implicit request: ModelRequest): ModelResult = {
    items.get(readMessage.shapeId)  match {
      case Some(items)=>
        val res = items.filter(i=>readMessage.resources.contains(i.id))
        val p = rp.pickle(res)
        Future.successful(Ok(p).as("application/json"))

      case None=>
        val p = rp.pickle(List.empty)

        Future.successful(Ok(p).as("application/json"))
    }
  }

  override def onDelete(deleteMessage: Delete)(implicit request: ModelRequest): ModelResult = {
    items.get(deleteMessage.shape) match {
      case Some(value)=>
        this.items = this.items.updated(deleteMessage.shape,value.filterNot(i=>i.id==deleteMessage.res))
        Future.successful(FALSE)
      case None =>

        Future.successful(TRUE)
      //this.articles = this.articles.filterNot(i=>i.id==deleteMessage.res)

    }
  }

  def suggestModels(items:List[PropertyModel], suggestMessage: Suggest): ModelResult  = {

    val t = suggestMessage.typed
    val list = for{
      i<-items
      p<-i.properties
      v<-p._2
      if v.isInstanceOf[IRI] && v.stringValue.contains(t)
    } yield v

   // val mes = ModelMessages.Suggestion(t,List[RDFValue](IRI("http://one"),IRI("http://tries"),IRI("http://something")),suggestMessage.id,suggestMessage.channel,new Date())
    val mes = Suggestion(t,list,suggestMessage.id,suggestMessage.channel,new Date())
    val p = rp.pickle(mes)
    Logger.info(p.toString())
    Future.successful(Ok(p).as("application/json"))
  }

  override def onSuggest(suggestMessage: Suggest): ModelResult = {

    items.get(suggestMessage.shape)  match {
      case Some(list)=>suggestModels(list,suggestMessage)
      case None=> this.onBadModelMessage(suggestMessage)
    }


  }
}