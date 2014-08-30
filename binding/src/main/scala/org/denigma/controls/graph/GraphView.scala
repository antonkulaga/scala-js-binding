package org.denigma.controls.graph


import org.denigma.binding.messages.{GraphMessages, ModelMessages}
import org.denigma.binding.messages.ModelMessages.Suggestion
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.OrdinaryView
import org.denigma.controls.sigma.{SigmaGraphInit, SigmaEdge, SigmaNode, Sigma}
import org.denigma.semantic.storages.Storage
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.{Quad, Res, IRI}
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.sparql.Pat
import rx.Rx
import org.scalajs.dom.HTMLElement
import rx.Var
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.{Success, Failure}
import scalatags.Text.Tag
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.denigma.binding.extensions._

trait GraphView extends OrdinaryView
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