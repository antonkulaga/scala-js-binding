package org.denigma.semantic.storages

import java.util.Date

import org.denigma.binding.extensions.sq
import org.denigma.binding.messages.Suggestion
import org.scalajs.dom
import org.scalax.semweb.messages.ShapeMessages
import org.scalax.semweb.rdf.Res
import org.scalax.semweb.shex.{ShEx, Shape}
import prickle.{Unpickle, Pickle}

import scala.concurrent.Future


class ShapeStorage(path:String)  extends Storage {
  override def channel: String = this.path
  import org.scalax.semweb.composites.SemanticComposites._
  /**
   * Get ShapeExpression either form query or from ShEx id
   * @param query
   * @param id
   * @param channel
   * @param time
   * @return
   */
  def getShex(query:Res,id:String = this.genId(),channel:String = this.channel, time:Date = new Date()):Future[ShEx] = {
    val data = ShapeMessages.GetShEx(query)
    dom.console.log(s"getShex with query $query //ShapeMessages must be rewritten")
    sq.tryPost(path,data){ d=> Pickle.intoString[ShapeMessages.ShapeMessage](d) }{ s=> Unpickle[ShEx].fromString(s)    }
  }


  def create(shape:Shape) = {

  }

  def suggestProperty(typed:String):Future[Suggestion] = {
    val data: ShapeMessages.SuggestProperty = ShapeMessages.SuggestProperty(typed,id = this.genId(),channel = this.channel, time = new Date())
    //sq.post(path,data):Future[Suggestion]
    ???
  }

}
