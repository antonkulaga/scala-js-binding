package org.denigma.graphs.visual

import org.denigma.graphs.core.SimpleGraph
import org.denigma.graphs.misc.Randomizable
import org.denigma.graphs.tools._
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, HTMLHeadingElement}
import org.scalajs.threejs
import org.scalajs.threejs._
import rx.Var

import scala.scalajs.js.Dynamic
import scala.util.Random
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._


class GraphContainer(val container:HTMLElement, val width:Double = dom.window.innerWidth, val height:Double = dom.window.innerHeight)
  extends Container3D with SimpleGraph with  Randomizable
{

  def randomDistance = 1500

  drawGraph()

  def drawGraph() = {
    for{i <- 1 to 20}
    {
      this.addNode("node_"+i)
    }
    for{i <- 1 to 50} {
      val n1 = Random.nextInt(nodes.size)
      val n2 = Random.nextInt(nodes.size)
      if(n1!=n2){
        this.addEdge(nodes(n1),nodes(n2),"edge#"+i.toString)
      }

    }

  }
}
