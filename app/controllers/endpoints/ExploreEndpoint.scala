package controllers.endpoints

import java.util.Date

import controllers.PJaxPlatformWith
import org.denigma.binding.messages.ExploreMessages
import org.denigma.binding.messages.ExploreMessages.ExploreMessage
import org.denigma.binding.picklers.rp
import org.denigma.binding.play.{AjaxExploreEndpoint, AuthRequest, PickleController, UserAction}
import org.scalajs.spickling.playjson._
import org.scalax.semweb.shex.PropertyModel
import play.api.libs.json.Json
import play.api.mvc.Result

import scala.concurrent.Future

/**
 * Explore articles trait
 */
trait ExploreEndpoint extends PickleController with AjaxExploreEndpoint with Items
{
  self:PJaxPlatformWith=>


  override type ExploreRequest = AuthRequest[ExploreMessage]

  override type ExploreResult = Future[Result]

  def exploreEndpoint() = UserAction.async(this.unpickle[ExploreMessage]()){implicit request=>
    this.onExploreMessage(request.body)

  }

  protected def exploreItems(items:List[PropertyModel],exploreMessage:ExploreMessages.Explore): List[PropertyModel] = {
    val list: List[PropertyModel] = items.filter{case a=>
      val res = exploreMessage.filters.forall(_.matches(a))
      res
    }

    exploreMessage.sortOrder match {
      case Nil=>list
      case s::xs=>
        play.Logger.debug("sort takes place")

        list.sortWith{case (a,b)=>s.sort(xs)(a,b) > -1}
    }

  }

  override def onExplore(exploreMessage: ExploreMessages.Explore)(implicit request: ExploreRequest): ExploreResult = {

    this.items.get( exploreMessage.shape)  match {
      case Some(list)=> this.shapes.get(exploreMessage.shape) match {
        case Some(shape)=>
          val res = rp.pickle( ExploreMessages.Exploration(shape,list,exploreMessage) )
          Future.successful(Ok(res).as("application/json"))

        case None=> this.onBadExploreMessage(exploreMessage,s"cannot find items for ${exploreMessage.channel}")
      }
      case None=>this.onBadExploreMessage(exploreMessage,s"cannot find items for ${exploreMessage.channel}")
    }
  }


  protected def suggest(suggestMessage: ExploreMessages.ExploreSuggest, items:List[PropertyModel])(implicit request: ExploreRequest): ExploreResult = {
    //play.Logger.debug("original = "+suggestMessage.toString)
    val t = suggestMessage.typed
    val prop = suggestMessage.prop
    val list = exploreItems(items,suggestMessage.explore)
    //play.Logger.debug("basic list = "+suggestMessage.toString)

    val result = list
      .collect { case item if item.properties.contains(prop) =>
      item.properties(prop).collect {
        case p if p.stringValue.contains(t) => p
      }
    }.flatten

      val mes = ExploreMessages.ExploreSuggestion(t, result, suggestMessage.id, suggestMessage.channel, new Date())
      val p = rp.pickle(mes)
      Future.successful(Ok(p).as("application/json"))

  }



  override def onExploreSuggest(suggestMessage: ExploreMessages.ExploreSuggest)(implicit request: ExploreRequest): ExploreResult = {

    this.items.get( suggestMessage.explore.shape)  match
    {
      case Some(list)=>this.suggest(suggestMessage, list)(request)
      case None=> this.onBadExploreMessage(suggestMessage)
    }
  }

  override def onBadExploreMessage(message: ExploreMessages.ExploreMessage, reason:String)(implicit request: ExploreRequest): ExploreResult ={

    Future.successful(BadRequest(Json.obj("status" ->"KO","message"->reason)).as("application/json"))

  }

  override def onSelect(suggestMessage: ExploreMessages.SelectQuery)(implicit request: ExploreRequest): ExploreResult = {
    this.items.get( suggestMessage.shapeId)  match {

      case Some(list)=>
        Future.successful(Ok(rp.pickle(list)).as("application/json"))

      case None=> this.onBadExploreMessage(suggestMessage,"cannot find shape for the message")
    }




  }

  override def onBadExploreMessage(message: ExploreMessage)(implicit request: ExploreRequest): ExploreResult = Future.successful(BadRequest(Json.obj("status" ->"KO","message"->"wrong message type!")).as("application/json"))


}
