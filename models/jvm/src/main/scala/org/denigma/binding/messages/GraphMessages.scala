package org.denigma.binding.messages

import java.util.Date

import org.scalax.semweb.messages.{Channeled, StorageMessage}
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.sparql.Pat

object GraphMessages {

  trait GraphMessage extends StorageMessage

  case class NodeExplore(resource:Res,props:List[IRI],patterns:List[Pat] = List.empty, depth:Int = 1,id:String,channel:String = Channeled.default,time:Date = new Date()) extends GraphMessage


}
