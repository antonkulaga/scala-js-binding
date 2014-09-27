package org.denigma.graphs.core

import org.denigma.graphs.misc.Randomizable
import org.denigma.graphs.tools.HtmlSprite
import org.scalajs.threejs.Vector3
import org.scalajs.threejs.extensions.Container3D
import rx._

import scala.scalajs.js
import scala.util.Random
import scalatags.JsDom.all._

object SimpleGraph extends GraphTypes
{
  override type DataType = Var[String]
}

trait SimpleGraph extends BasicGraph
{
  self:Container3D => // with  Randomizable=>

  type Node = SimpleGraph.Node
  type Edge = SimpleGraph.Edge


  val nodes:js.Array[Node] = js.Array() //optimized for speed
  val edges:js.Array[Edge] = js.Array() //optimized for speed

  val colors = List("green","red","blue","orange","purple","teal")
  val colorMap= Map("green"->0xA1CF64,"red"->0xD95C5C,"blue" -> 0x6ECFF5,"orange" ->0xF05940,"purple"->0x564F8A,"teal"->0x00B5AD)

  def randColorName = colors(Random.nextInt(colors.size))

  protected def nodeTagFromTitle(title:String,colorName:String) =
    label(title,   `class`:=s"ui large ${colorName} label", margin := "0px").render


  protected def edgeTagFromTitle(title:String,colorName:String) =
    label(title,   `class`:=s"ui small ${colorName} header", margin := "0px").render

  def layout(view:SimpleGraph.NodeView) = {
    view.sprite.position = new Vector3(0,0,0)
  }

  def addNode(label:String) =
  {
    val colorName = this.randColorName
    val color = colorMap(colorName)
    val t = nodeTagFromTitle(label,colorName)
    val sp = new HtmlSprite(t)
    val data = Var(label)
    val view = new SimpleGraph.NodeView(data,t,sp,color)
    cssScene.add(sp)
    val n = new SimpleGraph.Node(data,view)
    nodes.push(n)
    this.layout(n.view)
    n
  }

  def addEdge(from:SimpleGraph.Node,to:SimpleGraph.Node, label:String) = {

    val colorName = this.colorMap.collectFirst{ case (key,value) if value==from.view.color=>key} match {
      case Some(c)=>c
      case None=> this.randColorName
    }

    val t = edgeTagFromTitle(label,colorName)
    val sp = new HtmlSprite(t)
    sp.visible = false
    cssScene.add(sp)
    val data = Var(label)

    val view = new SimpleGraph.EdgeView(from.view.sprite,to.view.sprite,data,sp, SimpleGraph.LineParams(from.view.color))
    val e  = new SimpleGraph.Edge(from,to,data,view)
    scene.add(view.arrow)
    edges.push(e)
    e
  }

  def removeEdge(edge:Edge) = edges.indexOf(edge) match {
    case ind if ind<0=> throw new Exception("node that should be removed is not found")
    case ind=> edges.splice(ind,1)
  }

  def removeNode(node:Node) = nodes.indexOf(node) match {
    case ind if ind<0=> throw new Exception("node that should be removed is not found")
    case ind=> nodes.splice(ind,1)
  }


}