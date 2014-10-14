package org.denigma.binding.frontend.slides

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.extensions._
import org.denigma.graphs._
import org.denigma.graphs.layouts.ForceLayout
import org.denigma.graphs.simple.SimpleGraph
import org.denigma.graphs.visual.{SpriteMaker, Defs}
import org.scalajs.dom
import org.scalajs.dom.{HTMLDivElement, Event, HTMLElement, MouseEvent}
import org.scalax.semweb.rdf.Quad
import rx.Var
import scalatags.JsDom.all._


import scala.collection.immutable.Map
import scala.util.Random

class GraphSlide(elem:HTMLElement, params:Map[String,Any]) extends AjaxGraphView(elem:HTMLElement,params:Map[String,Any])
{


  def defWidth = 1280 //NAPILNIK
  def defHeight = 768 //TODO: fix


  type VizGraph =  SimpleGraph

  lazy val graph:VizGraph = new SimpleGraph(this.container,defWidth,defHeight)


  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit = withBinders(new GeneralBinder(this))

  def updateLayout() = {
    this.graph.layouts = Nil
    val force = new ForceLayout(graph.width,graph.height)
    this.graph.addLayout(force,true)
  }


  override def onLoad(results:List[Quad]) =
  {
    val res = results.foldLeft("RESULTS")( (acc,el)=> acc+"\n"+s"${el.sub.stringValue} => ${el.pred.stringValue} => ${el.obj.stringValue}" )
    dom.console.info(s"new graph data arrived $res")
    this.fillGraph(graph)
    this.updateLayout()
    graph.render()
  }



  def fillGraph(sg:VizGraph) =
  {
    for{
      i <- 1 to 30
      n1 = Random.nextInt(sg.nodes.size+20)
      n2 = Random.nextInt(sg.nodes.size+20)
    }
    if(n1!=n2)
    {
      val nid1 = "node_"+n1
      val no1 = sg.nodes.getOrElse(nid1, {
        val cn = Defs.colorName
        sg.addNode(nid1, Var(nid1), nodeTagFromTitle(nid1, cn), cn)
      })

      val nid2 = "node_"+n2
      val no2 = sg.nodes.getOrElse(nid2, {
        val cn = Defs.colorName
        sg.addNode(nid2, Var(nid2), nodeTagFromTitle(nid2, cn), cn)
      })
      val eid1 = "edge_"+n1+"=>"+n2
      val e = sg.addEdge(eid1,no1,no2,Var(eid1),edgeTagFromTitle(eid1,no1.view.colorName))
      e.view.update()
    }

  }


}