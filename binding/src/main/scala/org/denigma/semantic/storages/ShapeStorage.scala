package org.denigma.semantic.storages

import java.util.Date

import org.denigma.binding.extensions.sq
import org.denigma.binding.messages.{Suggestion, ShapeMessages}
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.{RDFValue, Res}
import org.scalax.semweb.shex.Shape

import scala.concurrent.Future

/**
 * Shape storage for shape editing
 * @param path
 * @param registry
 */
class ShapeStorage(path:String)(implicit registry:PicklerRegistry)  extends Storage {
  override def channel: String = this.path

  def getShapes(query:Option[Res] = None,id:String = this.genId(),channel:String = this.channel, time:Date = new Date()) = {
    val data = ShapeMessages.GetShapes(query,id,channel,time)
    sq.post(path,data):Future[List[Shape]]
  }

  def create(shape:Shape) = {

  }

  def suggestProperty(typed:String):Future[Suggestion] = {
    val data = ShapeMessages.SuggestProperty(typed,id = this.genId(),channel = this.channel, time = new Date())
    sq.post(path,data):Future[Suggestion]
  }

}
