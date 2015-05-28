package org.denigma.binding.messages

import java.util.Date

import org.denigma.semweb.messages.{Channeled, StorageMessage}
import org.denigma.semweb.rdf._
import org.denigma.semweb.rdf.vocabulary.WI
import org.denigma.semweb.shex.PropertyModel

/**
 * Contains model messages
 */
object ModelMessages  extends ModelProtocol {

  override type ModelMessage = StorageMessage

  case class Create(shapeId:Res,models:Set[PropertyModel],id:String, rewriteIfExists:Boolean = true,context:Res = IRI(WI.RESOURCE)  , channel:String = Channeled.default, time:Date = new Date()) extends  ModelMessage

  //TODO check if empty contexts work right
  case class Read(shapeId:Res,resources:Set[Res], id:String , contexts:Seq[Res] = List.empty, channel:String = Channeled.default,time:Date = new Date()) extends  ModelMessage

  case class Update(shapeId:Res,models:Set[PropertyModel], id:String, createIfNotExists:Boolean = true, channel:String = Channeled.default, time:Date = new Date() ) extends  ModelMessage

  case class Delete(shape:Res,res:Set[Res], id:String  , channel:String = Channeled.default, time:Date = new Date())  extends  ModelMessage

  case class SuggestObject(shape:Res,subject:Res,ruleOrProp:IRI,typed:String,id:String  , channel:String = Channeled.default, time:Date = new Date()) extends ModelMessage

  case class SuggestFact(subject:Res,rule:Res,typed:String,id:String  , channel:String = Channeled.default, time:Date = new Date()) extends ModelMessage


  type CreateMessage = Create
  type ReadMessage = Read
  type UpdateMessage = Update
  type DeleteMessage = Delete


}

/**
 * Just some constrains to keep CRUD protocols in order
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


case class Suggestion(term:String, options:Seq[RDFValue],  id:String  , channel:String = Channeled.default, time:Date = new Date()) extends StorageMessage
