package org.denigma.graphs

import org.denigma.binding.extensions.sq
import org.denigma.graphs.layouts.ForceLayout
import org.denigma.graphs.visual.Defs
import org.scalajs.dom.{Event, HTMLLabelElement, HTMLSpanElement, HTMLElement}
import org.scalax.semweb.rdf._
import org.scalax.semweb.shex.PropertyModel
import rx.core.Var
import org.scalajs.dom.extensions._

import scala.util.{Failure, Success}
import org.scalajs.dom


import scalatags.JsDom.all._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

abstract class SemanticGraphView(val elem:HTMLElement,val params:Map[String,Any]) extends AjaxGraphView(elem,params) with GraphUpdates
{

  type VizGraph  = SemanticGraph
  override lazy val graph:VizGraph = new VizGraph(elem,1280,768)


  def expand(res:Res) = {
    this.storage.explore(res).onComplete{
      case Success(results)=> this.onLoad(results,true)
      case Failure(th)=>dom.console.error("no ne data to load")
    }
  }


  def makeNodeTag(data:graph.NodeData,colorName:String): HTMLElement =
  {
    val node = data.now
    val iri = node.id

    def cl(ins:String) = s"ui ${ins} inverted header"

    val l =
      label(`class`:=cl(s"large $colorName"),
        padding :="10px",
        borderRadius := "10px",
        iri.label, onclick := {() => expand(iri) }
      ).render


    def onMouseOver(event:Event) = {
      l.textContent = iri.stringValue
      l.className = cl(s"big $colorName")
      l.contentEditable = "true"
    }

    def onMouseOut(event:Event) = {
      l.textContent = iri.label
      l.className = cl(s"large $colorName")
      l.contentEditable = "false"
    }

    def onMouseDown(event:Event) = {
//      l.textContent = if(l.textContent==iri.label) iri.stringValue else iri.label
    }

    l.addEventListener( "mouseover", onMouseOver _ )
    l.addEventListener( "mouseout", onMouseOut _ )
    l.addEventListener( "mousedown", onMouseDown _ )

    l
  }


  def makeEdgeTag(data:graph.EdgeData, colorName:String): HTMLElement = {
    val iri = data.now
    val l = label(iri.label, `class` := s"ui tiny ${colorName} header", margin := "0px").render
    l.contentEditable = "true"
    l
  }


  def updateLayout() = {
    this.graph.layouts = Nil
    val repM = 0.4 + Math.sqrt(graph.nodes.size)/40
    val force = new ForceLayout(graph.width,graph.height, repulsionMult = repM)
    this.graph.addLayout(force,true)
  }



  override def onLoad(results:List[Quad],onlyNew:Boolean) =
  {
    val res = results.foldLeft("RESULTS")( (acc,el)=> acc+"\n"+s"${el.sub.stringValue} => ${el.pred.stringValue} => ${el.obj.stringValue}" )
    //dom.console.info(s"new graph data arrived $res")
    this.addStatements(results)
    this.updateLayout()
    graph.render()
  }




}

trait GraphUpdates {
  self:SemanticGraphView=>


  def addStatements[T<:BasicTriplet](results:Seq[T],onlyNew:Boolean = false) =
  {
    val (es:Seq[T], ps:Seq[T]) = results.partition(p=>p.obj.isInstanceOf[Res])
    val ns = (results.map(p=>p.sub)++es.map(e=>e.obj.asInstanceOf[Res])).toSet
    addNodes(ns,ps,onlyNew)
    addEdges(es.toSet)
  }

  def collectProps[T<:BasicTriplet](ps:Seq[T])(n:Res) = ps.collect{case p if p.sub==n=>(p.pred,p.obj)}

  def updateNodes[T<:BasicTriplet](upd:Set[Res],ps:Seq[T]) =
    upd.foreach(u=>
      graph.nodes.get(u) match {
        case Some(n)=>
          val cp= collectProps(ps)(u).groupBy(kv=>kv._1).map{case (key,value)=>key->value.map(kv=>kv._2).toSet}
          val pm = n.data.now
          n.data() = pm.copy(pm.id,pm.properties++cp)

        case None=>
      }
    )

  def makeNode[T<:BasicTriplet](ps:Seq[T])(n:Res) = {
    val cn = Defs.colorName
    val props: Seq[(IRI, RDFValue)] = collectProps(ps)(n)
    val pm:graph.NodeData= Var(PropertyModel(n,props:_*))
    n->graph.addNode(n, pm,makeNodeTag(pm,colorName = cn),cn)
  }



  def addNodes[T<:BasicTriplet](ns:Set[Res],ps:Seq[T],onlyNew:Boolean = false) = {
    import graph._
    val (upd: Set[Res],newcomers: Set[Res]) = ns.partition(nodes.contains)
    if(!onlyNew) updateNodes(upd,ps)
    //TODO: add normal property models
    nodes = nodes ++ newcomers.map(makeNode(ps))
  }

  def addEdges[T<:BasicTriplet](es:Set[T]) =
  {
    import graph._
    val (upd,newcomers) = es.partition(graph.edges.contains)
    //TODO: updates for edges
    val cre = es.map{case e=>

      (nodes.get(e.sub), nodes.get(e.obj.asInstanceOf[Res])) match {
        case (Some(from),Some(to))=>
          val data =Var(e.pred)
          val spe: HTMLElement = this.makeEdgeTag(data,from.view.colorName)
          graph.addEdge(e,from,to,data,spe)
          //this.bind(spe) //TODO: write unbind somewhere
        case _=>
          dom.console.error(s"nodes not found for ${e.pred}")

      }
    }
  }

}