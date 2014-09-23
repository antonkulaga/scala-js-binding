package org.denigma.graphs

import org.denigma.binding.extensions._
import org.denigma.binding.messages.GraphMessages
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.BindableView
import org.denigma.semantic.storages.Storage
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.sigma.{SigmaEdge, SigmaNode}
import org.scalajs.spickling.PicklerRegistry
import org.scalajs.threejs._
import org.scalax.semweb.rdf.{IRI, Quad, Res}
import org.scalax.semweb.sparql.Pat

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Dynamic

/**
 * Created by antonkulaga on 9/23/14.
 */
class GraphStorage(path:String)(implicit registry:PicklerRegistry = rp) extends Storage {


  def channel:String = path


  /**
   *
   * @param resource resource to be explored
   * @param props if empty then all props are ok
   * @param patterns if empty then all paterns are ok
   * @param depth is 1 by default
   * @return
   */
  def explore(resource:Res,props:List[IRI] = List.empty,patterns:List[Pat] = List.empty, depth:Int = 1) = {
    sq.post(path,GraphMessages.NodeExplore(resource,props,patterns,depth, id = genId())):Future[List[Quad]]
  }

}
