package controllers

import org.denigma.binding.messages.ExploreMessages
import org.denigma.binding.messages.ExploreMessages.{ExploreMessage, Explore}
import org.denigma.binding.messages.ModelMessages._
import org.denigma.binding.picklers.rp
import org.denigma.binding.play._
import org.scalajs.spickling.playjson._
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.shex.PropertyModel
import play.api.libs.json.Json
import play.api.mvc.{Controller, Result}

/**
 *
 */
object ProjectController  extends Controller with PickleController with AjaxModelEndpoint with AjaxExploreEndpoint
{

  override type ModelRequest = AuthRequest[ModelMessage]

  override type ModelResult = Result

  override type ExploreRequest = AuthRequest[ExploreMessage]

  override type ExploreResult = Result


  val shapeRes = new IRI("http://shape.org")
  val title = (WI.PLATFORM / "title").iri
  val text = (WI.PLATFORM / "text").iri
  val completed = (WI.PLATFORM / "completed").iri
  val task = (WI.PLATFORM / "task").iri


  val writePaper = PropertyModel(IRI(WI.PLATFORM /"WritePaper"), title -> StringLiteral("Write paper"),  text->StringLiteral("I have to write agind as a disease paper") , completed->BooleanLiteral(false) , RDF.TYPE-> task)
  val makeWebsite = PropertyModel(IRI(WI.PLATFORM /"MakeWebsite"), title -> StringLiteral("Make a website"),  text->StringLiteral("I have to make Longevity Ukraine website work") , completed->BooleanLiteral(true), RDF.TYPE-> task)
  val doCRM = PropertyModel(IRI(WI.PLATFORM /"MakeCRM"), title -> StringLiteral("Make ILA CRM"),  text->StringLiteral("I have to make CRM for ILA") , completed->BooleanLiteral(false), RDF.TYPE-> task)


  var items:List[PropertyModel] = writePaper::makeWebsite::doCRM::Nil


  override def onCreate(createMessage: Create)(implicit request:ModelRequest): Result = {
    items = this.items ++ createMessage.models.toList
    Ok(rp.pickle(true)).as("application/json")
  }

  override def onUpdate(updateMessage: Update)(implicit request:ModelRequest): Result =
  {
    val res = updateMessage.models.map(_.resource)
    items = items.filterNot(i=>i.resource==res)++updateMessage.models
    Ok(rp.pickle(true)).as("application/json")
  }

  override def onRead(readMessage: Read)(implicit request:ModelRequest): Result = {
    val res = items.filter(i=>readMessage.resources.contains(i.resource))
    Ok(rp.pickle(res)).as("application/json")
  }

  override def onDelete(deleteMessage: Delete)(implicit request:ModelRequest): Result = {

    items = items.filterNot(kv=>deleteMessage.res.contains(kv.resource))
    Ok(rp.pickle(true)).as("application/json")

  }

  def modelEndpoint() = UserAction(this.pickle[ModelMessage]()){implicit request=>
    this.onModelMessage(request.body)
  }

  def exploreEndpoint() = UserAction(this.pickle[ExploreMessage]()){implicit request=>
    this.onExploreMessage(request.body)
  }

  override def onSelect(createMessage: ExploreMessages.SelectQuery)(implicit request: ModelRequest): Result = {
    val data = rp.pickle(items)
    Ok(data).as("application/json")
  }

  override def onSuggest(suggestMessage: ExploreMessages.Suggest)(implicit request: ModelRequest): Result = ???

  override def onBadModelMessage(message: ModelMessage): Result= BadRequest(Json.obj("status" ->"KO","message"->"wrong message type!")).as("application/json")


  override def onExplore(exploreMessage: Explore)(implicit request: ExploreRequest): ExploreResult = ???

  override def onBadExploreMessage(message: ExploreMessage)(implicit request: ExploreRequest): ExploreResult = BadRequest(Json.obj("status" ->"KO","message"->"wrong message type!")).as("application/json")

}


