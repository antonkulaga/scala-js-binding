package org.denigma.semantic.grids

import org.denigma.binding.messages.ExploreMessages.ExploreSuggestion
import org.denigma.binding.messages.{Sort, Filters}
import org.denigma.binding.views.BindableView
import org.scalax.semweb.rdf.IRI
import rx.core.Var

import scala.concurrent.Future

/**
 *
 */

trait ExplorableView extends BindableView{

  val filters = Var(Map.empty[IRI,Filters.Filter])
  val searchTerms = Var(Map.empty[IRI,String])
  val sorts = Var(Map.empty[IRI,Sort])

  //val explorer: Rx[Explore]
  def loadTyped(key: IRI, typed:String): Future[ExploreSuggestion]
}
