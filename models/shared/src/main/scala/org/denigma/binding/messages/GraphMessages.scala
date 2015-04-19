package org.denigma.binding.messages

import java.util.Date

import org.denigma.semweb.messages.{Channeled, StorageMessage}
import org.denigma.semweb.rdf.{IRI, Res}
import org.denigma.semweb.sparql.Pat

object GraphMessages {

  trait GraphMessage extends StorageMessage

  case class NodeExplore(resource:Res,props:List[IRI],patterns:List[Pat] = List.empty, depth:Int = 1,id:String,channel:String = Channeled.default,time:Date = new Date()) extends GraphMessage


}
