package org.denigma.graphs.layouts

import org.denigma.graphs.core.{VisualEdge, VisualNode}
import org.denigma.graphs.simple.{SimpleNode, SimpleEdge}
import org.scalajs.dom


class ForceLayout(val width:Double = 1000, val height:Double = 1000, val attractionMult:Double =  5,val repulsionMult:Double =  0.5) extends GraphLayout
{

  override type Node = SimpleNode
  override type Edge = SimpleEdge


  var info = Map.empty[Node,LayoutInfo]

  private var _nodes:Seq[Node] = Seq.empty[Node]
  def nodes_=(value:Seq[Node]) = {
    _nodes = value
    info =  value.map(v=>v->info.getOrElse(v, new LayoutInfo())).toMap
  }


  def nodes:Seq[Node] = _nodes
  var edges:Seq[Edge] = Seq.empty[Edge]



  val mode =  "3d"
  val maxIterations = 200


  var EPSILON = 0.00001
  var layoutIterations = 0
  var temperature = width / 50.0
  

  private var _active = false
  def active_=(value:Boolean) = if(_active!=value){
    _active = value
  }

  def active = _active

  def start(nodes:Seq[Node],edges:Seq[Edge]) = {
    this.nodes = nodes
    this.edges = edges
    layoutIterations = 0
    active = true
  }




  def tick() = if(keepGoing(nodes.size))
  {
    dom.console.info("tick me up")
    var forceConstant = Math.sqrt(this.height * this.width / nodes.size)

    val repulsion = this.repulsionMult * forceConstant
    this.repulse(nodes,repulsion)

    val attraction = this.attractionMult * forceConstant
    this.attract(this.edges,attraction)

    this.position(nodes)
    this.update(this.edges)

  }
  
  

  def repulse(nodes:Seq[Node],repulsion:Double) = {
    for {i <- 0 until  nodes.size} {

      val no1 = nodes(i)
      val n1 = no1.view
      val l1 = info(no1)
      if(i==0) l1.setOffsets(0, 0, 0)

      l1.force = 0
      l1.init(n1.sprite.position)

      for {j <- (i + 1) until  nodes.size; if i != j} {
        val no2 = nodes(j)
        val n2 = no2.view
        val l2 =info(no2)
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

  def attract(edges:Seq[Edge],attraction:Double) =
    for {i <- 0 until  edges.size}
    {
      val edge = edges(i)
      //val l1 = edge.from.view.layout
      //val l2 = edge.to.view.layout
      val l1 = info(edge.from)
      val l2 = info(edge.to)

      val deltaX = l1.pos.x - l2.pos.x
      val deltaY = l1.pos.y - l2.pos.y
      val deltaZ = l1.pos.z - l2.pos.z

      val distance = Math.max(EPSILON,l1.pos.distanceTo(l2.pos))

      val force = (distance * distance) / attraction


      l1.force -= force
      l2.force += force

      l1.offset.x -= (deltaX / distance) * force
      l1.offset.y -= (deltaY / distance) * force
      l1.offset.z -= (deltaZ / distance) * force


      l2.offset.x += (deltaX / distance) * force
      l2.offset.y += (deltaY / distance) * force
      l2.offset.z += (deltaZ / distance) * force

   }



  def position(nodes:Seq[Node]) = {
    for {i <- 0 until nodes.size} {
      val node = nodes(i)//.view
      val view = node.view
      val l = info(node)

      val length = Math.max(EPSILON,l.offset.length())

      //val length = Math.max(EPSILON, Math.sqrt(l.offset.x * l.offset.x + l.offset.y * l.offset.y))
      //val length_z = Math.max(EPSILON, Math.sqrt(l.offset.z * l.offset.z + l.offset.y * l.offset.y))
      

      l.pos.x += (l.offset.x / length) * Math.min(length, temperature)
      l.pos.y += (l.offset.y / length) * Math.min(length, temperature)
      l.pos.z += (l.offset.z / length) * Math.min(length, temperature)

      view.sprite.position.x -= (view.sprite.position.x - l.pos.x) / 10
      view.sprite.position.y -= (view.sprite.position.y - l.pos.y) / 10
      view.sprite.position.z -= (view.sprite.position.z - l.pos.z) / 10


    }
    temperature *= (1 - (layoutIterations / this.maxIterations))
    layoutIterations += 1

  }

  def update(edges:Seq[Edge]) = {
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
