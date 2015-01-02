package org.denigma.binding.messages

import java.util.Date

import org.denigma.binding.messages.ExploreMessages.ExploreMessage
import org.scalax.semweb.messages.{Channeled, StorageMessage}
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf._
import org.scalax.semweb.shex.{ArcRule, Shape, PropertyModel}

import scala.util.matching.Regex

/**
 * Contains model messages
 */
object ModelMessages  extends ModelProtocol {

  type ModelMessage = StorageMessage

  case class Create(shapeId:Res,models:Set[PropertyModel],id:String, rewriteIfExists:Boolean = true,context:Res = IRI(WI.RESOURCE)  , channel:String = Channeled.default, time:Date = new Date()) extends  ModelMessage

  //TODO check if empty contexts work right
  case class Read(shapeId:Res,resources:Set[Res], id:String , contexts:List[Res] = List.empty, channel:String = Channeled.default,time:Date = new Date()) extends  ModelMessage

  case class Update(shapeId:Res,models:Set[PropertyModel], id:String, createIfNotExists:Boolean = true, channel:String = Channeled.default, time:Date = new Date() ) extends  ModelMessage

  case class Delete(shape:Res,res:Set[Res], id:String  , channel:String = Channeled.default, time:Date = new Date())  extends  ModelMessage

  case class Suggest(shape:Res,modelRes:Res,ruleOrProp:Res,typed:String,id:String  , channel:String = Channeled.default, time:Date = new Date()) extends ModelMessage


  type CreateMessage = Create
  type ReadMessage = Read
  type UpdateMessage = Update
  type DeleteMessage = Delete


}

/**
 * Just come constrains to keep CRUD protocols in order
 */
trait ModelProtocol {

  type ModelMessage

  type CreateMessage<:ModelMessage

  type ReadMessage<:ModelMessage

  type UpdateMessage<:ModelMessage

  type DeleteMessage<:ModelMessage

  type SuggestMessage<:ModelMessage

  type SuggestResult<:ModelMessage

}


case class Suggestion(term:String, options:List[RDFValue],  id:String  , channel:String = Channeled.default, time:Date = new Date()) extends StorageMessage
