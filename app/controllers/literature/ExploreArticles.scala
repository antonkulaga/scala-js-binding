package controllers.literature

import controllers.PJaxPlatformWith
import org.denigma.binding.messages.ExploreMessages._
import org.denigma.binding.messages.ModelMessages._
import org.denigma.binding.picklers.rp
import org.denigma.binding.play.{AjaxExploreEndpoint, AuthRequest, PickleController, UserAction}
import org.scalajs.spickling.playjson._
import org.scalax.semweb.rdf.RDFValue
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

  override def onExplore(exploreMessage: Explore)(implicit request: ExploreRequest): ExploreResult = ???

  protected def suggest(suggestMessage: Suggest, items:List[PropertyModel])(implicit request: ExploreRequest): ExploreResult = {
    items.find(v=>v.id==suggestMessage.modelRes) match {
      case Some(item)=>
        val res = items.foldLeft(Set.empty[RDFValue]){ case(acc,m)=>
          m.properties.get(suggestMessage.prop).map(ps=>ps.filter(p=>p.stringValue.contains(suggestMessage.typed))) match {
            case Some(vals)=>acc++vals
            case None=>acc
          }
        }
        val p = rp.pickle(res)
        Future.successful(Ok(p).as("application/json"))
      case None=>Future.successful(this.BadRequest("no model resource"))
    }

  }

  override def onSuggest(suggestMessage: Suggest)(implicit request: ExploreRequest): ExploreResult = {

    suggestMessage.channel match {
      case ch if ch.contains("tasks")=> this.suggest(suggestMessage, tasks)(request)
      case _ =>this.suggest(suggestMessage, articles)(request)
    }
  }

  override def onSelect(suggestMessage: SelectQuery)(implicit request: ExploreRequest): ExploreResult = {

    val p = suggestMessage.channel match {
      case ch if ch.contains("task")=>rp.pickle(tasks)
      case _=> rp.pickle(articles)
    }
    Future.successful(Ok(p).as("application/json"))


  }

  override def onBadExploreMessage(message: ModelMessage)(implicit request: ExploreRequest): ExploreResult = Future.successful(BadRequest(Json.obj("status" ->"KO","message"->"wrong message type!")).as("application/json"))


}
