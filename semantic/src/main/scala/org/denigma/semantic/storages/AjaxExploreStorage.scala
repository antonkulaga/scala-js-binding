package org.denigma.semantic.storages

import org.denigma.binding.composites.BindingComposites
import org.denigma.binding.extensions._
import org.denigma.binding.messages.ExploreMessages
import org.scalajs.dom
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex.PropertyModel
import prickle.{Pickle, Unpickle}

import scala.concurrent.Future

class AjaxExploreStorage(path:String) extends Storage{

  import org.denigma.binding.composites.BindingComposites._

  def channel = path



  protected def post(data:ExploreMessages.ExploreMessage) =   sq.tryPost(path,data)
  {  d=>  Pickle.intoString(data)(BindingComposites.exploreMessages.pickler,BindingComposites.config)  }
  {   s=>     Unpickle[Boolean].fromString(s)   }

  protected def postBackModelsList(data:ExploreMessages.ExploreMessage) = sq.tryPost[ExploreMessages.ExploreMessage,Seq[PropertyModel]](path,data){
    d=> Pickle.intoString[ExploreMessages.ExploreMessage](data)(BindingComposites.exploreMessages.pickler,BindingComposites.config)
  }{   s=>   Unpickle[Seq[PropertyModel]].fromString(s) }

  protected def postBackExploration(data:ExploreMessages.ExploreMessage) = sq.tryPost(path,data){
    d=> Pickle.intoString[ExploreMessages.ExploreMessage](data)(BindingComposites.exploreMessages.pickler,BindingComposites.config)
  }{   s=>
    //dom.console.log(s"UNPICKLE $s")
    val u = Unpickle[ExploreMessages.Exploration].fromString(s)
    if(u.isFailure) dom.console.error(s"FAILURE\n for $s")
    u
  }


  protected def postBackSuggestion(data:ExploreMessages.ExploreMessage) = sq.tryPost(path,data){
    d=> Pickle.intoString[ExploreMessages.ExploreMessage](data)(BindingComposites.exploreMessages.pickler,BindingComposites.config)
  }{   s=>   Unpickle[ExploreMessages.ExploreSuggestion].fromString(s) }




  def explore(explore:ExploreMessages.Explore) = {
    val data = explore
    postBackExploration(data)
    //sq.post(path,data):Future[ExploreMessages.Exploration]

  }


  /**
   * Select query
   * @param query
   * @param shape
   * @return
   */
  def select(query:Res,shape:Res): Future[Seq[PropertyModel]] = {
    val data: ExploreMessages.SelectQuery = ExploreMessages.SelectQuery(shape,query, genId(),  channel = path)
    postBackModelsList(data)
//    sq.post(path,data):Future[Seq[PropertyModel]]
  }




  def suggest(typed:String,prop:IRI,explore:ExploreMessages.Explore):Future[ExploreMessages.ExploreSuggestion] = { ExploreMessages.ExploreSuggest
    val data = ExploreMessages.ExploreSuggest(typed,prop,explore, genId())
    //sq.post(path,data):Future[ExploreMessages.ExploreSuggestion]
    postBackSuggestion(data)
  }




}
