package org.denigma.endpoints

import org.denigma.binding.messages._
import play.api.mvc.{Controller, Request}





trait AjaxModelEndpoint {
  self:Controller=>

  type ModelRequest <:Request[ModelMessages.ModelMessage]

  type ModelResult //either result or future result


  def onCreate(createMessage:ModelMessages.Create)(implicit request:ModelRequest):ModelResult
  def onRead(readMessage:ModelMessages.Read)(implicit request:ModelRequest):ModelResult
  def onUpdate(updateMessage:ModelMessages.Update)(implicit request:ModelRequest):ModelResult
  def onDelete(deleteMessage:ModelMessages.Delete)(implicit request:ModelRequest):ModelResult
  def onSuggest(suggestMessage:ModelMessages.Suggest):ModelResult


  def onBadModelMessage(message:ModelMessages.ModelMessage):ModelResult  = onBadModelMessage(message,"wrong model message type!")
  def onBadModelMessage(message:ModelMessages.ModelMessage, reason:String):ModelResult

  def onModelMessage(message:ModelMessages.ModelMessage)(implicit request:ModelRequest):ModelResult= message match {
    case m:ModelMessages.Create=>this.onCreate(m)
    case m:ModelMessages.Read=>this.onRead(m)
    case m:ModelMessages.Update=>this.onUpdate(m)
    case m:ModelMessages.Delete=>this.onDelete(m)
    case m:ModelMessages.Suggest=>this.onSuggest(m)


    case other=> onBadModelMessage(other)
  }

}