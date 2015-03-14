package org.denigma.binding.messages

import java.util.Date

import org.scalax.semweb.messages.{Channeled, StorageMessage}
import org.scalax.semweb.rdf._
import org.scalax.semweb.shex.{NameClass, PropertyModel, Shape}

import scala.util.matching.Regex


object ExploreMessages {

  type ExploreMessage = StorageMessage

  type ResourceQuery = SelectQuery

  case class SelectQuery(shapeId:Res,query:Res, id:String , context:List[Res] = List() , channel:String = Channeled.default, time:Date = new Date()) extends ExploreMessage

  case class Explore(query:Res,  shape:Res, filters:List[Filters.Filter] = List.empty[Filters.Filter] , searchTerms:List[String] = List.empty[String], sortOrder:List[Sort] = List.empty[Sort],id:String  , channel:String = Channeled.default, time:Date = new Date()) extends ExploreMessage

  case class ExploreSuggest(typed:String,nameClass:NameClass,explore:Explore,  id:String  , time:Date = new Date()) extends ExploreMessage {
    def channel = explore.channel
  }

  case class ExploreSuggestion(term:String, options:List[RDFValue],  id:String  , channel:String = Channeled.default, time:Date = new Date()) extends ExploreMessage
  {
    def fromSuggest(suggest:ExploreSuggest,list:List[RDFValue]) = ExploreSuggestion(suggest.typed,list,suggest.id,suggest.channel,new Date())
  }

  case class Exploration(shape:Shape,models:List[PropertyModel], explore:Explore) extends ExploreMessage{
    def time = explore.time
    def id = explore.id //TEMP
    override def channel: String = explore.channel
  }

  case class StringContainsFilter(field:IRI,value:String)

}

case class Sort(field:IRI,desc:Boolean = true)
{
  def sort(sorts:List[Sort] = Nil)(a:PropertyModel,b:PropertyModel):Int =  (a.properties.get(field),b.properties.get(field)) match {
    case (Some(fa), Some(fb))=>(fa,fb) match {
      case (fa:IRI,fb:IRI)=> fa.stringValue.compareTo(fb.stringValue)
      case (fa:IRI,fb:BlankNode)=>1
      case (fa:Res,fb:Lit)=>1
      case (fa:BlankNode,fb:IRI)=> -1
      case (fa:Lit,fb:Res)=> -1
      case _=> if(sorts.isEmpty) 0 else sorts.head.sort(sorts.tail)(a,b)
    }
    case (None,Some(fb)) => 2
    case (Some(fa), None)=>1
    case (None,None)=>  if(sorts.isEmpty) 0 else sorts.head.sort(sorts.tail)(a,b)
  }

}

object Filters{

  case class ContainsFilter(field:IRI,str:String) extends Filter {
    override def matches(p: PropertyModel): Boolean = p.properties.get(field) match {
      case None=>false
      case Some(f)=>f.exists(v=>v.stringValue.contains(str))
    }
  }


  case class StringFilter(field:IRI,reg:String) extends Filter {
    lazy val regex: Regex = reg.r

    override def matches(p: PropertyModel): Boolean = p.properties.get(field) match {
      case None=>false
      case Some(f)=> f match {
        case this.regex=>true
        case _=>false
      }
    }
  }

  case class NumFilter(field:IRI,min:Double,max:Double)extends Filter
  {
    override def matches(p: PropertyModel): Boolean = p.properties.get(field) match {
      case None=>false
      case Some(f)=>f.exists{
        case l:DoubleLiteral=>l.value>min && l.value<max
      }
    }
  }


  case class ValueFilter(field:IRI,values:Set[RDFValue]) extends Filter
  {
    override def matches(p: PropertyModel): Boolean = p.properties.get(field) match {
      case None=>false
      case Some(f)=>f.exists(v=>values.contains(v))
    }

    def add(v:RDFValue) = this.copy(field,values+v)
    def remove(v:RDFValue) = this.copy(field,values -v)
    def isEmpty = values.isEmpty
  }

  trait Filter
  {
    def matches(p:PropertyModel):Boolean
  }

}



trait ExploreProtocol {

  type ExploreMessage

  type SelectMessage <:ExploreMessage

  type ResourceQuery <:ExploreMessage

  type SearchMessage <:ExploreMessage

  type SuggestMessage

}

