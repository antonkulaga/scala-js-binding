package org.denigma.binding.messages

import java.util.Date

import org.scalax.semweb.messages.{Channeled, StorageMessage}
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf.{IRI, RDFValue, Res}
import org.scalax.semweb.shex.{Shape, PropertyModel}

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


  type CreateMessage = Create
  type ReadMessage = Read
  type UpdateMessage = Update
  type DeleteMessage = Delete


}


object ExploreMessages extends SuggestProtocol

trait SuggestProtocol extends ExplorationProtocol
{
  type SuggestMessage = Suggest

  type SuggestResult = Suggestion

  case class Suggest(shape:Res,modelRes:Res,prop:IRI,typed:String,id:String  , channel:String = Channeled.default, time:Date = new Date()) extends ExploreMessage

  case class Suggestion(term:String, options:List[RDFValue],  id:String  , channel:String = Channeled.default, time:Date = new Date()) extends ExploreMessage

  case class ExploreSuggest(shape:Res,modelRes:Res,prop:IRI,typed:String,explore:Explore,  id:String  , time:Date = new Date()) {
    def channel = explore.channel
  }

}


trait ExplorationProtocol{

  type ExploreMessage = StorageMessage


  case class SelectQuery(shapeId:Res,query:Res, id:String , context:List[Res] = List() , channel:String = Channeled.default, time:Date = new Date()) extends ExploreMessage


  case class StringContainsFilter(field:IRI,value:String)


  case class Explore(query:Res,  shape:Res, filters:List[Filters.Filter] = List.empty[Filters.Filter] , searchTerms:List[String] = List.empty[String], sortOrder:List[Sort] = List.empty[Sort],id:String  , channel:String = Channeled.default, time:Date = new Date())

  case class Exploration(shape:Shape,models:List[PropertyModel], explore:Explore)




  type ResourceQuery = SelectQuery


}

case class Sort(field:IRI,desc:Boolean = true)

object Filters{

  case class StringFilter(field:IRI,reg:String) extends Filter

  case class NumFilter(field:IRI,min:Double,max:Double)extends Filter

  case class ValueFilter(field:IRI,values:List[RDFValue]) extends Filter

  trait Filter

}



trait ExploreProtocol {

  type ExploreMessage
  
  type SelectMessage <:ExploreMessage

  type ResourceQuery <:ExploreMessage

  type SearchMessage <:ExploreMessage


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

}