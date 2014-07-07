package org.denigma.binding.storages

import org.denigma.binding.extensions._
import org.denigma.binding.messages.ExploreMessages
import org.denigma.binding.messages.ExploreMessages.SuggestResult
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex.PropertyModel

import scala.concurrent.Future

class AjaxExploreStorage(path:String,query:Res,shape:Res)(implicit registry:PicklerRegistry)  extends ExploreStorage{

  def channel = path

//  val explore = Var(ExploreMessages.Explore(query,shape, id = this.genId()))
//
//
//  def explore() = {
//    val data = ExploreMessages.Explore(query,shape, id = this.genId())
//    sq.post(path,data):Future[ExploreMessages.Exploration]
//  }

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


  def suggest(shape:Res,modelRes:Res,prop:IRI,typed:String):Future[SuggestResult] = { ExploreMessages.Suggest
    val data = ExploreMessages.Suggest(shape,modelRes,prop,typed, genId(), channel = path)
    sq.post(path,data):Future[ExploreMessages.SuggestResult]
  }





}

trait ExploreStorage extends Storage{

  def select(query:Res,shape:Res): Future[List[PropertyModel]]

  def suggest(shape:Res,modelRes:Res,prop:IRI,typed:String):Future[SuggestResult]


}