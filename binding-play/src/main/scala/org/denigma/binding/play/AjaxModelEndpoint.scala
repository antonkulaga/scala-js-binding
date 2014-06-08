package org.denigma.binding.play

import play.api.mvc.{Result, Request, Controller}
import org.denigma.binding.models.ModelMessages._
import org.denigma.binding.models.ModelMessages


trait AjaxModelEndpoint {
  self:Controller=>

  type RequestType <:Request[ReadMessage]

  def onCreate(createMessage:ModelMessages.Create)(implicit request:RequestType):Result
  def onRead(readMessage:ModelMessages.Read)(implicit request:RequestType):Result
  def onUpdate(updateMessage:ModelMessages.Update)(implicit request:RequestType):Result
  def onDelete(deleteMessage:ModelMessages.Delete)(implicit request:RequestType):Result

  def onMessage(message:ModelMessages.ModelMessage)(implicit request:RequestType) = message match {
    case m:ModelMessages.Create=>this.onCreate(m)
    case m:ModelMessages.Read=>this.onRead(m)
    case m:ModelMessages.Update=>this.onUpdate(m)
    case m:ModelMessages.Delete=>this.onDelete(m)

    case _=> this.BadRequest("wrong model message format")
  }


}
