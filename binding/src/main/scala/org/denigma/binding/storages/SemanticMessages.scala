package org.denigma.binding.storages

import org.scalax.semweb.rdf.Res
import org.scalax.semweb.shex.{PropertyModel, Shape}
import org.scalax.semweb.sparql.SelectQuery


object SemanticMessages{
  trait SemanticMessage
  trait ReadMessage extends SemanticMessage
  trait QueryMessage extends ReadMessage
  trait WriteMessage extends SemanticMessage


  case class Describe(res:Res) extends ReadMessage

  case class Read(res:Res,shapeId:Res) extends ReadMessage
  case class ReadWithShape(res:Res,shapeId:Res) extends ReadMessage
  case class ReadShaped(res:Res,shape:Shape) extends ReadMessage

  case class GetShapes(res:Res) extends ReadMessage
  case class GetShape(res:Res) extends ReadMessage


  case class ReadAll[T](query:T) extends ReadMessage

  case class Select(query:SelectQuery,shape:Shape) extends QueryMessage



  case class Write(value:PropertyModel,rewrite:Boolean = true) extends WriteMessage
  case class WriteShaped(value:PropertyModel,rewrite:Boolean = true,shape:Shape) extends WriteMessage
  case class Remove(res:Res)  extends WriteMessage

}
