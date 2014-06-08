package controllers

import play.api.mvc.{Request, Result, Controller}
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary._

import org.scalax.semweb.rdf.vocabulary.WI
import org.denigma.binding.models.ModelMessages._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.StringLiteral
import org.denigma.binding.models.ModelMessages.Create
import org.denigma.binding.models.ModelMessages.Read
import org.denigma.binding.models.ModelMessages.Delete
import org.denigma.binding.models.ModelMessages.Update
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalajs.spickling.playjson._
import org.denigma.binding.picklers.rp
import org.denigma.binding.models.{StorageProtocol, ModelMessages}


object PageController extends Controller with PickleController with AjaxModelEndpoint
{

  override type RequestType = AuthRequest[ReadMessage]

  val res = new IRI("http://page.org")
  val shapeRes = new IRI("http://shape.org")

  var items: Map[Res, PropertyModel] = Map(res->  PropertyModel(res,
    properties = Map(
      (WI.PLATFORM / "title").iri -> Set(StringLiteral("HELLO WORLD")),
      (WI.PLATFORM / "text").iri->Set(StringLiteral("TEXT")))
  ) )

  override def onCreate(createMessage: Create)(implicit request:RequestType): Result = {
    val models:Map[Res,PropertyModel] =  createMessage.models.map(m=> m.resource -> m).toMap
    if(createMessage.rewriteIfExists) {
      items = this.items ++ models
    }
    else
    {
      items  = items ++ models.filterNot{case (key,value)=>items.contains(key)}
    }
    Ok(rp.pickle(true)).as("application/json")
  }

  override def onUpdate(updateMessage: Update)(implicit request:RequestType): Result = {
    val models:Map[Res,PropertyModel] =  updateMessage.models.map(m=> m.resource -> m).toMap
    if(updateMessage.createIfNotExists) {
      items = this.items ++ models
    }
    else
    {
      items  = items ++ models.filter{case (key,value)=>items.contains(key)}
    }
    Ok(rp.pickle(true)).as("application/json")
  }

  override def onRead(readMessage: Read)(implicit request:RequestType): Result = {
    val res = items.foldLeft(List.empty[PropertyModel]){ case (acc,(key,value))=> if(readMessage.resources.contains(key)) value::acc else acc  }
    Ok(rp.pickle(res)).as("application/json")
  }

  override def onDelete(deleteMessage: Delete)(implicit request:RequestType): Result = {
    items = items.filterNot(kv=>deleteMessage.res.contains(kv._1))
    Ok(rp.pickle(true)).as("application/json")

  }

  def endpoint() = UserAction(this.pickle[ReadMessage]()){implicit request=>
   this.onMessage(request.body)

  }
}

trait AjaxModelEndpoint {
  self:Controller=>

  type RequestType <:Request[ReadMessage]

  def onCreate(createMessage:ModelMessages.Create)(implicit request:RequestType):Result
  def onRead(readMessage:ModelMessages.Read)(implicit request:RequestType):Result
  def onUpdate(updateMessage:ModelMessages.Update)(implicit request:RequestType):Result
  def onDelete(deleteMessage:ModelMessages.Delete)(implicit request:RequestType):Result

  def onMessage(message:ModelMessages.ModelMessage)(implicit request:RequestType) = message match {
    case m:ModelMessages.Create=>this.onCreate(m)
    case m:ModelMessages.Read=>this.onRead(m)
    case m:ModelMessages.Update=>this.onUpdate(m)
    case m:ModelMessages.Delete=>this.onDelete(m)

    case _=> this.BadRequest("wrong model message format")
  }


}
