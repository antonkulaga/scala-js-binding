package org.denigma.graphs.sigma

import org.denigma.binding.views.BindableView
import org.denigma.graphs.GraphStorage
import org.scalajs.dom.HTMLElement
import org.scalajs.sigma.{Sigma, SigmaEdge, SigmaGraphInit, SigmaNode}
import org.scalax.semweb.rdf.{IRI, Quad, Res}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.util.{Failure, Success}

trait SigmaGraphView extends BindableView
{

  def path:String

  def resource:IRI

  def container: HTMLElement // = dom.document.getElementById("graph-container")

  def initialGraph:SigmaGraphInit = SigmaGraphInit.empty

  def renderer = js.Dynamic.literal(
    container = this.container,
    `type` = "canvas"
  )

  lazy val initial = js.Dynamic.literal(
    graph = this.initialGraph.asInstanceOf[js.Dynamic],
    renderer = this.renderer
  )

  var sigma:Sigma = null

  def graph = sigma.graph

  lazy val storage = new GraphStorage(path)


  override def bindView(el:HTMLElement) = {
    //jQuery(el).slideUp()
    activateMacro()
    this.bind(el)
    Sigma.utils.pkg("sigma.canvas.edges")
    this.sigma =  new Sigma(initial)
    this.storage.explore(this.resource).onComplete{
      case Success(data) =>
        this.loadData(data)
      case Failure(th)=>
        this.error(s"failure in read of model for $path: \n ${th.getMessage} ")
    }

  }

  protected def loadData(data:List[Quad]) = {
    val nodes =  data.foldLeft(Set.empty[Res]){
      case (acc, el)=> el.obj match {
        case res:Res=> acc + el.sub + res
        case _=>acc +el.sub
      }
    } map ( r=>SigmaNode(r.stringValue,r.label) )
    val edges = data.map(d=> new SigmaEdge(id = d.toString,source = d.sub.stringValue,target = d.obj.stringValue))
    debug(nodes.toString()+"\n  edges = "+edges.toString()+ " \n initial data=" +data.toString())
    nodes.foreach(n=>sigma.graph.addNode(n))
    edges.foreach(e=>sigma.graph.addEdge(e))
  }


}