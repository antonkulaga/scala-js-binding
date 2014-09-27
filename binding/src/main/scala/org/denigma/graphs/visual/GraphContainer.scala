package org.denigma.graphs.visual

import org.denigma.graphs.core.SimpleGraph
import org.denigma.graphs.layouts.GraphLayout
import org.denigma.graphs.misc.Randomizable
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.threejs.extensions.Container3D


class GraphContainer(val container:HTMLElement, val width:Double = dom.window.innerWidth, val height:Double = dom.window.innerHeight)
  extends Container3D with SimpleGraph
{

  type MyLayout = GraphLayout[SimpleGraph.Node,SimpleGraph.Edge]

  var layouts:List[MyLayout] = List.empty

  drawGraph()

  def drawGraph() = {
  //does nothing


  }

  def addLayout(layout:MyLayout,activate:Boolean = false) = {
    this.layouts = layout::layouts
    if(activate)layout.start(nodes,edges)
  }

  override def onEnterFrame() = {
    this.layouts.foreach{case l=>
      if(l.active) l.tick(nodes,edges)
        dom.console.info(s"l is ${l.active}")
    }
    super.onEnterFrame()
  }
}
