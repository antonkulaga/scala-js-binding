package org.denigma.semantic.grids

import org.denigma.binding.binders.{GeneralBinder, NavigationBinding}
import org.denigma.binding.messages.ExploreMessages
import org.denigma.binding.messages.ExploreMessages.{Explore, ExploreSuggestion}
import org.denigma.semantic.binders.ExploreBinder
import org.denigma.semantic.models.collections.AjaxModelCollection
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf._
import rx.core.Rx

import scala.collection.immutable.Map
import scala.concurrent.Future

object ExplorableCollection {

  def defaultBinders(view:ExplorableCollection)  =    new ExploreBinder(view)::new GeneralBinder(view)::new NavigationBinding(view)::Nil

}

/**
 * Observable colelction
 */
abstract class ExplorableCollection(name:String,elem:HTMLElement,params:Map[String,Any]) extends AjaxModelCollection(name,elem,params)
with ExplorableView
{
  lazy val defaultExplore = ExploreMessages.Explore(
    this.query,
    this.shapeRes,
    this.filters().values.toList,
    this.searchTerms().values.toList,
    this.sorts().values.toList,
    this.exploreStorage.genId(),
    exploreStorage.channel
  )

  def loadTyped(key: IRI, typed:String): Future[ExploreSuggestion] = this.exploreStorage.suggest(typed,key,this.explorer.now)

  override val explorer: Rx[Explore] = Rx( this.defaultExplore   )


}




