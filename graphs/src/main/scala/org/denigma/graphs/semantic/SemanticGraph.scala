package org.denigma.graphs

import org.denigma.graphs.core.SpriteGraph
import org.denigma.graphs.semantic.{SemanticEdge, SemanticNode}
import org.denigma.graphs.tools.HtmlSprite
import org.denigma.graphs.visual.{Defs, EdgeView, LineParams, NodeView}
import org.scalajs.dom
import org.scalajs.dom.{Event, HTMLElement}
import org.scalax.semweb.rdf.{BasicTriplet, IRI, Res}
import org.scalax.semweb.shex.PropertyModel
import rx.core.Var

class SemanticGraph(val container:HTMLElement,
                  val width:Double = dom.window.innerWidth,
                  val height:Double = dom.window.innerHeight)
  extends SpriteGraph
{


  override type NodeId = Res
  override type EdgeId = BasicTriplet
  override type NodeData = Var[PropertyModel]
  override type EdgeData = Var[IRI]

  override type ViewOfNode  =  NodeView[NodeData]
  override type ViewOfEdge =   EdgeView[EdgeData]

  type Node = SemanticNode
  type Edge = SemanticEdge


  override def removeEdge(id:EdgeId): this.type = edges.get(id) match {
    case Some(e)=>
      cssScene.remove(e.view.sprite)
      scene.remove(e.view.arrow)
      edges = edges - id
      this
    case None=> throw new Exception("node that should be removed is not found"); this
  }

  override def removeNode(id:NodeId):this.type = nodes.get(id) match {
    case Some(n)=>
      cssScene.remove(n.view.sprite)
      val toRemove = edges.filter{case (key,value)=>value.from==n || value.to==n}
      toRemove.foreach{case (key,value)=>this.removeEdge(key)}
      nodes = nodes - id; this
    case None => throw new Exception("node that should be removed is not found"); this
  }




  def addNode(id:NodeId,data:NodeData, element:HTMLElement, colorName:String):Node =
    this.addNode(id,data, new ViewOfNode(data,new HtmlSprite(element),colorName))


  override def addNode(id:NodeId,data:NodeData, view:ViewOfNode):Node =
  {
    import view.{sprite => sp}
    this.randomPos(view.sprite)
    val n = new SemanticNode(data,view)
    sp.element.addEventListener( "mousedown", (this.onMouseDown(sp) _).asInstanceOf[Function[Event,_ ]] )
    cssScene.add(view.sprite)
    this.nodes = nodes + (id->n)
    n
  }


  def addEdge(id:EdgeId,from:Node,to:Node, data: EdgeData,element:HTMLElement):Edge =
  {
    val color = Defs.colorMap.get(from.view.colorName) match {
      case Some(c)=>c
      case None=>Defs.color
    }
    val sp = new HtmlSprite(element)
    element.addEventListener( "mousedown", (this.onMouseDown(sp) _).asInstanceOf[Function[Event,_ ]] )
    this.controls.moveTo(sp.position)
    //sp.visible = false

    addEdge(id,from,to,data,new EdgeView(from.view.sprite,to.view.sprite,data,sp, LineParams(color)))

  }

  override def addEdge(id:EdgeId,from:Node,to:Node, data: EdgeData,view:ViewOfEdge):Edge =
  {
    cssScene.add(view.sprite)
    val e  = new Edge(from,to,data,view)
    scene.add(view.arrow)
    edges = edges + (id->e)
    e
  }



}