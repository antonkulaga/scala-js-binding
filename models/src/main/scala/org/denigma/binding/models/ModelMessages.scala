package org.denigma.binding.models

import org.scalax.semweb.rdf.Res
import org.scalax.semweb.shex.{PropertyModel, Shape}

/**
 * Each source has its id, it is "channel"
 */
trait Channeled{
  def channel:String
}
trait StorageMessage extends Channeled{
    def id:String
    def time:Double
}

object ModelMessages extends ExtendedStorageProtocol
{
  self=>


  trait ModelMessage extends StorageMessage

  case class SelectQuery(channel:String,shapeId:Res,query:Res, id:String ,time:Double ) extends ModelMessage

  case class Create(channel:String,shapeId:Res,models:Set[PropertyModel],rewriteIfExists:Boolean = true, id:String ,time:Double ) extends ModelMessage

  case class Read(channel:String,shapeId:Res,resources:Set[Res], id:String ,time:Double ) extends ModelMessage

  case class Update(channel:String,shapeId:Res,models:Set[PropertyModel],createIfNotExists:Boolean = true, id:String ,time:Double ) extends ModelMessage

  case class Delete(channel:String,shape:Res,res:Set[Res], id:String ,time:Double )  extends ModelMessage

  type CommonMessage = StorageMessage
  type CreateMessage = Create
  type ReadMessage = Read
  type UpdateMessage = Update
  type DeleteMessage = Delete
  type ResourceQuery = SelectQuery

}

object ShapeMessages {

  trait ShapeMessage extends Channeled

  case class GetShapes(channel:String,shapeIds:Res*) extends ShapeMessage

  case class AllResourcesForShape[T](channel:String,shapeId:Res) extends ShapeMessage

  case class AllShapesForResource[T](channel:String,res:Res) extends ShapeMessage

  case class UpdateShape(channel:String,shap:Shape,rewrite:Boolean)


}

object DescribeMessages{

  case class Describe(channel:String,res:Res*) extends Channeled


}
