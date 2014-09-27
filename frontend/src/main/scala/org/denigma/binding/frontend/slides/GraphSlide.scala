package org.denigma.binding.frontend.slides

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.graphs.GraphView
import org.denigma.graphs.core.SimpleGraph
import org.denigma.graphs.layouts.ForceLayout
import org.denigma.graphs.misc.Randomizable
import org.denigma.graphs.visual.GraphContainer
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.threejs.Vector3
import rx.core.Var

import scala.collection.immutable.Map
import scala.util.Random
import rx.extensions._

class GraphSlide(elem:HTMLElement, params:Map[String,Any]) extends GraphView(elem:HTMLElement,params:Map[String,Any])
{

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  val apply = Var(EventBinding.createMouseEvent())

  override protected def attachBinders(): Unit = withBinders(new GeneralBinder(this))



  override val graphContainer: GraphContainer = new TestGraphContainer(container)

  override def bindView(el:HTMLElement) =
  {
    super.bindView(el)
    val force = new ForceLayout(graphContainer.width,graphContainer.height)
    this.graphContainer.addLayout(force,true)
  }


  val layoutClick = Var(EventBinding.createMouseEvent())

  layoutClick.handler{
    dom.console.info("Layout click")
    val force = new ForceLayout(graphContainer.width,graphContainer.height)
    this.graphContainer.addLayout(force,true)
  }
}

class TestGraphContainer(container:HTMLElement,width:Double = 1024,height:Double = 768) extends GraphContainer(container,width,height)
with Randomizable
{

  def randomDistance = 1000
  override def layout(view:SimpleGraph.NodeView) = {
    view.sprite.position = rand3()
  }





  override def drawGraph() = {
    for{
      i <- 1 to 30
      n1 = Random.nextInt(nodes.size+20)
      n2 = Random.nextInt(nodes.size+20)
    }
    {
      if(n1!=n2){
        val no1 = if(n1>=nodes.size) this.addNode("node_"+nodes.size+1) else nodes(n1)
        val no2 = if(n2>=nodes.size) this.addNode("node_"+nodes.size+1) else nodes(n2)

        this.addEdge(no1,no2,"edge#"+i.toString)
      }

    }
  }

}