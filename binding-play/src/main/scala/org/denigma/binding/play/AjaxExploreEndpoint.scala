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

  def onBadExploreMessage(message:ExploreMessages.ExploreMessage)(implicit request: ExploreRequest):ExploreResult= onBadExploreMessage(message,"wrong explore message type!")

  def onBadExploreMessage(message:ExploreMessages.ExploreMessage, reason:String)(implicit request: ExploreRequest):ExploreResult



  def onExploreMessage(message:ExploreMessages.ExploreMessage)(implicit request:ExploreRequest):ExploreResult = message match {
    case m:ExploreMessages.Explore=>onExplore(m)
    case m:ExploreMessages.SelectQuery=>onSelect(m)
    case m:ExploreMessages.ExploreSuggest=>onExploreSuggest(m)
    case other=>onBadExploreMessage(message)
  }



}
