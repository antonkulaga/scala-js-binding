package controllers.endpoints


import java.util.Date
import org.denigma.binding.composites.BindingComposites._
import org.denigma.binding.messages.Suggestion
import org.denigma.endpoints.{PrickleController, AjaxShapeEndpoint, AuthRequest, UserAction}
import org.denigma.semweb.messages.ShapeMessages
import org.denigma.semweb.messages.ShapeMessages.{GetShapes, ShapeMessage}
import org.denigma.semweb.rdf.IRI
import org.denigma.semweb.rdf.vocabulary._
import org.denigma.semweb.shex.{IRILabel, ShEx}
import play.api.libs.json.Json
import play.api.mvc._
import prickle.{Pickle, Unpickle}

import scala.concurrent.Future

trait ShapeEndpoint extends   Items with AjaxShapeEndpoint with PrickleController{
  self:Controller=>

  override type ShapeRequest = AuthRequest[ShapeMessage]

  override type ShapeResult =  Future[Result]

  lazy val allShapes = IRILabel(WI.pl("AllShapes"))

  lazy val defaultPrefixes = Seq(RDF.prefix->RDF.namespace,"rdfs"->RDFS.namespace,"owl"->OWL.namespace,"dc"->DCElements.namespace,"dct"->DCTerms.namespace)


  override def getShapes(suggestMessage: GetShapes): ShapeResult = {

    import org.denigma.semweb.composites.SemanticComposites._
    val shs = this.shapes.values.toList
    val shapes: ShEx = ShEx(allShapes,shs,shs.headOption.map(_.id),Some(allShapes.iri.label),defaultPrefixes)
    val p = Pickle.intoString[ShEx](shapes)
    //val p = rp.pickle(this.shapes.values.toList)
    Future.successful(this.pack(p))
  }

  def getShex(shex:ShapeMessages.GetShEx) = {
    import org.denigma.semweb.composites.SemanticComposites._
    val shs = this.shapes.values.toList
    val shapes: ShEx = ShEx(allShapes,shs,shs.headOption.map(_.id),Some(allShapes.iri.label),defaultPrefixes)
    val p = Pickle.intoString[ShEx](shapes)
    //val p = rp.pickle(this.shapes.values.toList)
    Future.successful(this.pack(p))
  }

  override def onBadShapeMessage(message: ShapeMessage, reason: String): ShapeResult = {
    Future.successful(BadRequest(Json.obj("status" ->"KO","message"->reason)).as("application/json"))
  }

  def badMessage( reason: String): ShapeResult = {
    Future.successful(BadRequest(Json.obj("status" ->"KO","message"->reason)).as("application/json"))
  }

  override def onSuggestProperty(suggestMessage: ShapeMessages.SuggestProperty): ShapeResult = {
    val t = suggestMessage.typed.replace(" ","_")
    val list: List[IRI] = this.properties.filter(p=>p.stringValue.contains(t))
    val mes: Suggestion = Suggestion(t,list,suggestMessage.id,suggestMessage.channel,new Date())
    val p = Pickle.intoString[Suggestion](mes)
    Future.successful(Ok(p).as("text/plain"))

  }


  def shapeEndpoint() = UserAction.async(this.unpickleWith{str=>  Unpickle[ShapeMessage].fromString(str)  }){ implicit request=>
    request.body match {
      case sp:ShapeMessages.SuggestProperty=> this.onSuggestProperty(sp)
      case gs:ShapeMessages.GetShapes=> this.getShapes(gs)
      case gs:ShapeMessages.GetShEx=> this.getShex(gs)
      case other => badMessage("I HAVE NOT IMPLEMENTED SUPPORT FOR OTHER MESSAGES YET!")
    }
  }

}
