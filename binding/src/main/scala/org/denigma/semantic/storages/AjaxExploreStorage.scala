package org.denigma.semantic.storages

import org.denigma.binding.extensions._
import org.denigma.binding.messages.ExploreMessages
import org.denigma.binding.picklers.rp
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex.PropertyModel

import scala.concurrent.Future

class AjaxExploreStorage(path:String)(implicit registry:PicklerRegistry = rp)  extends Storage{

  def channel = path

  def explore(explore:ExploreMessages.Explore) = {
    val data = explore
    sq.post(path,data):Future[ExploreMessages.Exploration]
  }


  /**
   * Select query
   * @param query
   * @param shape
   * @return
   */
  def select(query:Res,shape:Res): Future[List[PropertyModel]] = {
    val data: ExploreMessages.SelectQuery = ExploreMessages.SelectQuery(shape,query, genId(),  channel = path)
    sq.post(path,data):Future[List[PropertyModel]]
  }




  def suggest(typed:String,prop:IRI,explore:ExploreMessages.Explore):Future[ExploreMessages.ExploreSuggestion] = { ExploreMessages.ExploreSuggest
    val data = ExploreMessages.ExploreSuggest(typed,prop,explore, genId())
    sq.post(path,data):Future[ExploreMessages.ExploreSuggestion]
  }




}
