package org.denigma.binding.play

import org.denigma.binding.messages._
import play.api.mvc.{Controller, Request}


trait AjaxExploreEndpoint {
  self:Controller=>

  type ExploreRequest <:Request[ExploreMessages.ExploreMessage]

  type ExploreResult //either result or future result

  def onExplore(exploreMessage:  ExploreMessages.Explore)(implicit request: ExploreRequest):ExploreResult

  def onExploreSuggest(suggestMessage:  ExploreMessages.ExploreSuggest)(implicit request: ExploreRequest):ExploreResult

  def onSelect(selectMessage:  ExploreMessages.SelectQuery)(implicit request: ExploreRequest):ExploreResult

  def onBadExploreMessage(message:ModelMessages.ModelMessage)(implicit request: ExploreRequest):ExploreResult

  def onExploreMessage(message:ExploreMessages.ExploreMessage)(implicit request:ExploreRequest):ExploreResult = message match {
    case m:ExploreMessages.Explore=>onExplore(m)
    case m:ExploreMessages.SelectQuery=>onSelect(m)
    case m:ExploreMessages.ExploreSuggest=>onExploreSuggest(m)
    case other=>onBadExploreMessage(message)
  }



}


trait AjaxModelEndpoint {
  self:Controller=>

  type ModelRequest <:Request[ModelMessages.ModelMessage]

  type ModelResult //either result or future result


  def onCreate(createMessage:ModelMessages.Create)(implicit request:ModelRequest):ModelResult
  def onRead(readMessage:ModelMessages.Read)(implicit request:ModelRequest):ModelResult
  def onUpdate(updateMessage:ModelMessages.Update)(implicit request:ModelRequest):ModelResult
  def onDelete(deleteMessage:ModelMessages.Delete)(implicit request:ModelRequest):ModelResult
  def onSuggest(suggestMessage:ModelMessages.Suggest):ModelResult


  def onBadModelMessage(message:ModelMessages.ModelMessage):ModelResult

  def onModelMessage(message:ModelMessages.ModelMessage)(implicit request:ModelRequest):ModelResult= message match {
    case m:ModelMessages.Create=>this.onCreate(m)
    case m:ModelMessages.Read=>this.onRead(m)
    case m:ModelMessages.Update=>this.onUpdate(m)
    case m:ModelMessages.Delete=>this.onDelete(m)
    case m:ModelMessages.Suggest=>this.onSuggest(m)


    case other=> onBadModelMessage(other)
  }

}