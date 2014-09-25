package org.denigma.graphs.core

import org.denigma.graphs.misc.Randomizable
import org.denigma.graphs.tools.HtmlSprite
import org.denigma.graphs.visual.Container3D
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.threejs.{ArrowHelper, Vector3, Object3D}
import rx._

import scala.util.Random
import scalatags.JsDom.all._

object SimpleGraph extends GraphTypes
{
  override type DataType = Var[String]
}

trait SimpleGraph extends BasicGraph
{
  self:Container3D with  Randomizable=>

  type Node = SimpleGraph.Node
  type Edge = SimpleGraph.Edge

  var nodes:List[Node] = List.empty
  var edges:List[Edge] = List.empty

  val colors = List("green","red","blue","orange","purple","teal")

  def randColorName = colors(Random.nextInt(colors.size))

  protected def nodeTagFromTitle(title:String,color:Double) =
    label(title,   `class`:=s"ui large ${this.randColorName} label" ).render


  protected def edgeTagFromTitle(title:String,color:Double) =
    label(title,   `class`:=s"ui small ${this.randColorName} label" ).render

  def layout(view:SimpleGraph.NodeView) = {
    view.sprite.position = this.rand3()
  }

  def addNode(label:String) =
  {
    val color = randColor()
    val t = nodeTagFromTitle(label,color)
    val sp = new HtmlSprite(t)
    val data = Var(label)
    val view = new SimpleGraph.NodeView(data,t,sp,color)
    cssScene.add(sp)
    val n = new SimpleGraph.Node(data,view)
    nodes = n::nodes
    this.layout(n.view)
    this

  }

  def addEdge(from:SimpleGraph.Node,to:SimpleGraph.Node, label:String) = {
    val color = from.view.color
    val t = edgeTagFromTitle(label,color)
    val sp = new HtmlSprite(t)
    sp.visible = false
    cssScene.add(sp)
    val data = Var(label)

    val view = new SimpleGraph.EdgeView(from.view.sprite,to.view.sprite,data,sp, SimpleGraph.LineParams(color))
    val e  = new SimpleGraph.Edge(from,to,data,view)
    scene.add(view.arrow)
    edges = e::edges
    this
  }

  def removeEdge(edge:Edge) = {
    //edges = edges./:()
  }

  def removeNode(node:Node) = {
    edges.filter(e=>e.from==node || e.to == node).foreach(removeEdge)

  }


}