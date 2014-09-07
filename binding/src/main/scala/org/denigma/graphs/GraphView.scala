package org.denigma.graphs

import org.denigma.binding.extensions._
import org.denigma.binding.messages.GraphMessages
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.BindableView
import org.denigma.semantic.storages.Storage
import org.scalajs.dom.HTMLElement
import org.scalajs.sigma.{SigmaEdge, SigmaNode}
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.{IRI, Quad, Res}
import org.scalax.semweb.sparql.Pat

import scala.concurrent.Future

trait GraphView extends BindableView
{

  def path:String

  def resource:IRI

  def container: HTMLElement // = dom.document.getElementById("graph-container")

 lazy val storage = new GraphStorage(path)


//  override def bindView(el:HTMLElement) = {
//    //jQuery(el).slideUp()
//    super.bindView(el)
//    Sigma.utils.pkg("sigma.canvas.edges")
//    this.storage.explore(this.resource).onComplete{
//      case Success(data) =>
//        this.loadData(data)
//      case Failure(th)=>
//        this.error(s"failure in read of model for $path: \n ${th.getMessage} ")
//    }
//
//  }

  protected def loadData(data:List[Quad]) = {
    val nodes =  data.foldLeft(Set.empty[Res]){
      case (acc, el)=> el.obj match {
        case res:Res=> acc + el.sub + res
        case _=>acc +el.sub
      }
    } map ( r=>SigmaNode(r.stringValue,r.label) )
    val edges = data.map(d=> new SigmaEdge(id = d.toString,source = d.sub.stringValue,target = d.obj.stringValue))
    debug(nodes.toString()+"\n  edges = "+edges.toString()+ " \n initial data=" +data.toString())
    //nodes.foreach(n=>sigma.graph.addNode(n))
    //edges.foreach(e=>sigma.graph.addEdge(e))
  }

}

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