package controllers.endpoints


import java.util.Date

import org.denigma.binding.messages.ShapeMessages.{ShapeMessage, GetShapes}
import org.denigma.binding.messages.{ShapeMessages, Suggestion}
import org.denigma.binding.picklers.rp
import org.denigma.binding.play._
import org.scalajs.spickling.playjson._
import org.scalax.semweb.rdf.IRI
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future

trait ShapeEndpoint extends  PickleController with Items with AjaxShapeEndpoint{
  self:Controller=>

  override type ShapeRequest = AuthRequest[ShapeMessage]

  override def getShapes(suggestMessage: GetShapes): ShapeResult = {

    val p = rp.pickle(this.shapes.values.toList)
    Future.successful(Ok(p).as("application/json"))
  }

  override def onBadShapeMessage(message: ShapeMessage, reason: String): ShapeResult = {
    Future.successful(BadRequest(Json.obj("status" ->"KO","message"->reason)).as("application/json"))
  }

  override def onSuggestProperty(suggestMessage: ShapeMessages.SuggestProperty): ShapeResult = {
    val t = suggestMessage.typed.replace(" ","_")
    val list: List[IRI] = this.properties.filter(p=>p.stringValue.contains(t))
    val mes: Suggestion = Suggestion(t,list,suggestMessage.id,suggestMessage.channel,new Date())
    val p = rp.pickle(mes)
    Future.successful(Ok(p).as("application/json"))

  }

  override type ShapeResult =  Future[Result]

  def shapeEndpoint() = UserAction.async(this.pickle[ ShapeMessage]()){implicit request=>
    this.onShapeMessage(request.body)

  }
}
