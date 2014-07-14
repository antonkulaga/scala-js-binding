package controllers.literature

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
trait ExploreArticles extends PickleController with AjaxExploreEndpoint with ArticleItems with TaskItems
{
  self:PJaxPlatformWith=>


  override type ExploreRequest = AuthRequest[ExploreMessage]

  override type ExploreResult = Future[Result]

  def exploreEndpoint() = UserAction.async(this.pickle[ExploreMessage]()){implicit request=>
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
    val res = exploreMessage.channel match {
      case ch if ch.contains("task")=>
        val list = this.exploreItems(tasks,exploreMessage)
        ExploreMessages.Exploration(this.taskShape,list,exploreMessage)
      case ch=>
        val list= this.exploreItems(articles,exploreMessage)
        ExploreMessages.Exploration(this.articleShape,list,exploreMessage)

    }
    val p = rp.pickle(res)
    Future.successful(Ok(p).as("application/json"))

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

    suggestMessage.channel match {
      case ch if ch.contains("task")=> this.suggest(suggestMessage, tasks)(request)
      case _ =>this.suggest(suggestMessage, articles)(request)
    }
  }

  override def onSelect(suggestMessage: ExploreMessages.SelectQuery)(implicit request: ExploreRequest): ExploreResult = {

    val p = suggestMessage.channel match {
      case ch if ch.contains("task")=>rp.pickle(tasks)
      case _=> rp.pickle(articles)
    }
    Future.successful(Ok(p).as("application/json"))


  }

  override def onBadExploreMessage(message: ExploreMessage)(implicit request: ExploreRequest): ExploreResult = Future.successful(BadRequest(Json.obj("status" ->"KO","message"->"wrong message type!")).as("application/json"))


}
