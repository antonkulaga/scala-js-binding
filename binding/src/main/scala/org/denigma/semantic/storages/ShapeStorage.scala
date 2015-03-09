package org.denigma.semantic.storages

import java.util.Date

import org.denigma.binding.extensions.sq
import org.denigma.binding.messages.Suggestion
import org.scalajs.dom
import org.scalax.semweb.messages.ShapeMessages
import org.scalax.semweb.rdf.Res
import org.scalax.semweb.shex.Shape

import scala.concurrent.Future


class ShapeStorage(path:String)  extends Storage {
  override def channel: String = this.path

  def getShapes(query:Option[Res] = None,id:String = this.genId(),channel:String = this.channel, time:Date = new Date()):Future[List[Shape]] = {
    //val data = ShapeMessages.GetShapes(query,id,channel,time)
    //sq.post(path,data):Future[List[Shape]]
    dom.console.error("GET SHAPES SHOULD BE rewriteen")
    ???
  }

  def create(shape:Shape) = {

  }

  def suggestProperty(typed:String):Future[Suggestion] = {
    val data: ShapeMessages.SuggestProperty = ShapeMessages.SuggestProperty(typed,id = this.genId(),channel = this.channel, time = new Date())
    //sq.post(path,data):Future[Suggestion]
    ???
  }

}
