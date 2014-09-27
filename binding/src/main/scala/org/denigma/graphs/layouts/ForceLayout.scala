package org.denigma.graphs.layouts

import org.denigma.graphs.core.SimpleGraph
import org.scalajs.dom
import org.scalajs.threejs.Vector3

import scala.scalajs.js


class ForceLayout(val width:Double = 1000, val height:Double = 1000, val attractionMult:Double =  6,val repulsionMult:Double =  0.6) extends GraphLayout[SimpleGraph.Node,SimpleGraph.Edge]
{

  type Node = SimpleGraph.Node
  type Edge = SimpleGraph.Edge


  val mode =  "3d"
  val maxIterations = 1000


  var EPSILON = 0.0001
  var layoutIterations = 0
  var temperature = width / 100.0
  

  private var _active = false
  def active_=(value:Boolean) = if(_active!=value){
    _active = value
  }

  def active = _active

  def start(nodes:js.Array[Node],edges:js.Array[Edge]) = {
    layoutIterations = 0
    active = true
  }




  def tick(nodes:js.Array[Node],edges:js.Array[Edge]) = if(keepGoing(nodes.size))
  {
    dom.console.info("tick me up")
    var forceConstant = Math.sqrt(this.height * this.width / nodes.size)

    val repulsion = this.repulsionMult * forceConstant
    this.repulse(nodes,repulsion)

    val attraction = this.attractionMult * forceConstant
    this.attract(edges,attraction)

    this.position(nodes)
    this.update(edges)

  }
  
  

  def repulse(nodes:js.Array[Node],repulsion:Double) = {
    for {i <- 0 until  nodes.size} {

      val n1 = nodes(i).view
      val l1 = n1.layout
      if(i==0) l1.setOffsets(0, 0, 0)

      l1.force = 0
      l1.init(n1.sprite.position)

      for {j <- (i + 1) until  nodes.size; if i != j} {
        val n2 = nodes(j).view
        val l2 = n2.layout
        l2.init(n2.sprite.position)

        val deltaX = l1.pos.x - l2.pos.x
        val deltaY = l1.pos.y - l2.pos.y
        val deltaZ = l1.pos.z - l2.pos.z

        val distance = Math.max(EPSILON,l1.pos.distanceTo(l2.pos))


        val force =  (repulsion * repulsion) / distance
        l1.force += force
        l1.offset.x = l1.offset.x + (deltaX / distance) * force
        l1.offset.y = l1.offset.y + (deltaY / distance) * force
        l1.offset.z = l1.offset.z + (deltaZ / distance) * force

        if(i==0){
          l2.setOffsets(0,0,0)
        }

        l2.force += force
        l2.offset.x = l2.offset.x - (deltaX / distance) * force
        l2.offset.y = l2.offset.y - (deltaY / distance) * force
        l2.offset.z = l2.offset.z - (deltaZ / distance) * force
      }

    }
  }

  def attract(edges:js.Array[Edge],attraction:Double) =
    for {i <- 0 until  edges.size}
    {
      val edge = edges(i)
      val l1 = edge.from.view.layout
      val l2 = edge.to.view.layout
      val deltaX = l1.pos.x - l2.pos.x
      val deltaY = l1.pos.y - l2.pos.y
      val deltaZ = l1.pos.z - l2.pos.z

      val distance = Math.max(EPSILON,l1.pos.distanceTo(l2.pos))

      val force = (distance * distance) / attraction


      edge.from.view.layout.force -= force
      edge.to.view.layout.force += force

      edge.from.view.layout.offset.x -= (deltaX / distance) * force
      edge.from.view.layout.offset.y -= (deltaY / distance) * force
      edge.from.view.layout.offset.z -= (deltaZ / distance) * force


      edge.to.view.layout.offset.x += (deltaX / distance) * force
      edge.to.view.layout.offset.y += (deltaY / distance) * force
      edge.to.view.layout.offset.z += (deltaZ / distance) * force

   }



  def position(nodes:js.Array[Node]) = {
    for {i <- 0 until nodes.size} {
      val node = nodes(i).view
      val l1 = node.layout

      val length = Math.max(EPSILON,node.layout.offset.length())

      //val length = Math.max(EPSILON, Math.sqrt(node.layout.offset.x * node.layout.offset.x + node.layout.offset.y * node.layout.offset.y))
      //val length_z = Math.max(EPSILON, Math.sqrt(node.layout.offset.z * node.layout.offset.z + node.layout.offset.y * node.layout.offset.y))
      

      node.layout.pos.x += (node.layout.offset.x / length) * Math.min(length, temperature)
      node.layout.pos.y += (node.layout.offset.y / length) * Math.min(length, temperature)
      node.layout.pos.z += (node.layout.offset.z / length) * Math.min(length, temperature)

      node.sprite.position.x -= (node.sprite.position.x - node.layout.pos.x) / 10
      node.sprite.position.y -= (node.sprite.position.y - node.layout.pos.y) / 10
      node.sprite.position.z -= (node.sprite.position.z - node.layout.pos.z) / 10


    }
    temperature *= (1 - (layoutIterations / this.maxIterations))
    layoutIterations += 1

  }

  def update(edges:js.Array[Edge]) = {
    edges.foreach(e=>e.view.update())
  }




  def keepGoing(size:Int): Boolean  = size>0 && layoutIterations < this.maxIterations && temperature > 0.000001




  def pause() = {
    active = false
  }
  /**
   * Stops the calculation by setting the current_iterations to max_iterations.
   */
  def stop() =
  {
    layoutIterations = this.maxIterations
    active = false
  }
}
