package controllers

import org.denigma.binding.composites.BindingComposites
import org.denigma.binding.messages.ModelMessages
import org.denigma.binding.messages.ModelMessages._
import org.denigma.endpoints.{AjaxModelEndpoint, AuthRequest, UserAction}
import org.scalax.semweb.rdf.{IRI, StringLiteral, _}
import org.scalax.semweb.rdf.vocabulary.{WI, _}
import org.scalax.semweb.shex.PropertyModel
import play.api.libs.json.Json
import play.api.mvc.{Controller, Result}
import prickle.{Pickle, Unpickle}


object PageController extends Controller with PrickleController with AjaxModelEndpoint
{
  import org.denigma.binding.composites.BindingComposites
  import BindingComposites._
  //override type ModelRequest = AuthRequest[ReadMessage]
  override type ModelRequest = AuthRequest[  ModelMessage ]


  override type ModelResult = Result


  val shapeRes = new IRI("http://shape.org")
  val header = (WI.PLATFORM / "header").iri
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
      header -> Set(StringLiteral("This is test rdf model that is kept in memory. You can switch on contenteditable mode and edit text directly")),
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

  def onSuggest(suggestMessage:ModelMessages.Suggest):ModelResult = ???


  var items: Map[Res, PropertyModel] = Map(hello->helloModel, rybka->rybkaModel   )

  override def onCreate(createMessage: Create)(implicit request:ModelRequest): Result = {
    val models:Map[Res,PropertyModel] =  createMessage.models.map(m=> m.resource -> m).toMap
    if(createMessage.rewriteIfExists) {
      items = this.items ++ models
    }
    else
    {
      items  = items ++ models.filterNot{case (key,value)=>items.contains(key)}
    }
    pTRUE
  }

  override def onUpdate(updateMessage: Update)(implicit request:ModelRequest): Result = {
    val models:Map[Res,PropertyModel] =  updateMessage.models.map(m=> m.resource -> m).toMap
    play.Logger.info("UPDATE = \n"+models.map(m=>m._1.stringValue+" => "+m._2.properties.toString).mkString("\n"))
    if(updateMessage.createIfNotExists) {
      items = this.items ++ models
    }
    else
    {
      items  = items ++ models.filter{case (key,value)=>items.contains(key)}
    }
    pTRUE
  }

  override def onRead(readMessage: Read)(implicit request:ModelRequest): Result = {
    val res: Seq[PropertyModel] = items.foldLeft(List.empty[PropertyModel]){ case (acc,(key,value))=> if(readMessage.resources.contains(key)) value::acc else acc  }
    this.pack(Pickle.intoString(res))
  }

  override def onDelete(deleteMessage: Delete)(implicit request:ModelRequest): Result = {
    items = items.filterNot(kv=>deleteMessage.res.contains(kv._1))
    pTRUE

  }


  override def onBadModelMessage(message: ModelMessage, reason:String): ModelResult = BadRequest(Json.obj("status" ->"KO","message"->reason)).as("application/json")

 def modelEndpoint() = UserAction(this.unpickleWith{
   str=>
     //play.api.Logger.error(s"PARSING: $str")
     val mes = Unpickle[ModelMessage](BindingComposites.modelsMessages.unpickler).fromString(str)
     mes
 }){implicit request=>
    this.onModelMessage(request.body)

  }
}


