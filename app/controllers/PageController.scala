package controllers

import org.denigma.binding.messages.ModelMessages.ReadMessage
import play.api.mvc.{Request, Result, Controller}
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary._
import org.denigma.binding.messages.ModelMessages._

import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.StringLiteral
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalajs.spickling.playjson._
import org.denigma.binding.picklers.rp
import org.denigma.binding.play.{AjaxModelEndpoint, PickleController, AuthRequest, UserAction}
import play.api.http
import org.scalax.semweb.shex._


object PageController extends Controller with PickleController with AjaxModelEndpoint
{

  override type RequestType = AuthRequest[ReadMessage]

  val shapeRes = new IRI("http://shape.org")
  val title = (WI.PLATFORM / "title").iri
  val text = (WI.PLATFORM / "text").iri


  val hello = IRI("http://page.org")


  val helloModel = PropertyModel(hello,
    properties = Map(
      title -> Set(StringLiteral("HELLO WORLD")),
      text->Set(StringLiteral("TEXT")))
  )


  val rybka = IRI("http://rybka.org.ua/project")

  val rybkaModel = PropertyModel(rybka,
    properties = Map(
      title -> Set(StringLiteral("About Rybka Project")),
      text ->Set(StringLiteral(
        """
          |                <p class="ui text">
          |
          |                Memory consolidation is long-term memory formation from short-term memories.
          |                Despite of various studies the problem of memory storage in the brain is not solved yet.
          |                Molecular mechanisms of memory loading and reading (retrieval) are not fully discovered as well.
          |                </p>
          |                <p class="text">
          |
          |                The Rybka Project is dedicated to bring some more clarity to this area.
          |                Basic idea our studies is to research proteins responsible for memory consolidation, reconsolidation, and learning,  as well as their expressions with Zebrafish model.
          |                </p>
          |                <p class="text">
          |                    We are going  manage to make those proteins glow with help of green flourescent protein (GFP) transfection.
          |                    Since we use transparent strain of the zebrafish we will be able to watch them glowing while and after fish learns something while solving some cognitive tasks.
          |
          |                </p>
          |            </div>
        """.stripMargin))
    )
  )




  var items: Map[Res, PropertyModel] = Map(hello->helloModel, rybka->rybkaModel   )

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


