package org.denigma.endpoints

import org.scalax.semweb.messages.ShapeMessages
import org.scalax.semweb.messages.ShapeMessages.ShapeMessage
import play.api.mvc.{Controller, Request}

/**
 * Provides set of methods
 */
trait AjaxShapeEndpoint {
  self:Controller=>

  type ShapeRequest <:Request[ShapeMessage]

  type ShapeResult //usually either result or future result



  def onSuggestProperty(suggestMessage:ShapeMessages.SuggestProperty):ShapeResult
  def getShapes(suggestMessage:ShapeMessages.GetShapes):ShapeResult


  def onBadShapeMessage(message:ShapeMessages.ShapeMessage):ShapeResult  = onBadShapeMessage(message,"wrong model message type!")
  def onBadShapeMessage(message:ShapeMessages.ShapeMessage, reason:String):ShapeResult

  def onShapeMessage(message:ShapeMessages.ShapeMessage)(implicit request:ShapeRequest):ShapeResult= message match {
    case m:ShapeMessages.GetShapes=>this.getShapes(m)
    case m:ShapeMessages.SuggestProperty=>this.onSuggestProperty(m)
    case other=> onBadShapeMessage(other)
  }

}