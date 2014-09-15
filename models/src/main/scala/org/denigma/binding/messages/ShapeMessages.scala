package org.denigma.binding.messages

import java.util.Date

import org.scalax.semweb.messages.{Channeled, StorageMessage}
import org.scalax.semweb.rdf.Res

object ShapeMessages {

  trait ShapeMessage extends StorageMessage

  case class GetShapes(query:Option[Res] = None,id:String,channel:String = Channeled.default,time:Date = new Date()) extends ShapeMessage

  case class SuggestProperty(typed:String, id:String  , channel:String = Channeled.default, time:Date = new Date()) extends ShapeMessage


}
