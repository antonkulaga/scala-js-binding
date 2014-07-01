package org.denigma.binding.messages

import org.scalax.semweb.messages.{Channeled, StorageMessage}
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex.PropertyModel
import java.util.Date

/**
 * Contains model messages
 */
object ModelMessages extends ExtendedStorageProtocol
{
  self=>


  trait ModelMessage extends StorageMessage

  case class SelectQuery(shapeId:Res,query:Res, id:String , context:List[Res] = List() , channel:String = Channeled.default, time:Date = new Date()) extends ModelMessage

  case class Create(shapeId:Res,models:Set[PropertyModel],id:String, rewriteIfExists:Boolean = true,context:Res = IRI(WI.RESOURCE)  , channel:String = Channeled.default, time:Date = new Date()) extends ModelMessage

  //TODO check if empty contexts work right
  case class Read(shapeId:Res,resources:Set[Res], id:String , contexts:List[Res] = List.empty, channel:String = Channeled.default,time:Date = new Date()) extends ModelMessage

  case class Update(shapeId:Res,models:Set[PropertyModel], id:String, createIfNotExists:Boolean = true, channel:String = Channeled.default, time:Date = new Date() ) extends ModelMessage

  case class Delete(shape:Res,res:Set[Res], id:String  , channel:String = Channeled.default, time:Date = new Date())  extends ModelMessage

  type CommonMessage = StorageMessage
  type CreateMessage = Create
  type ReadMessage = Read
  type UpdateMessage = Update
  type DeleteMessage = Delete
  type ResourceQuery = SelectQuery

}
