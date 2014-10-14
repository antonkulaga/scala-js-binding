package org.denigma.graphs

import org.denigma.graphs.core.{VisualEdge, VisualGraph, VisualNode}
import org.denigma.graphs.visual.{EdgeView, LineParams, NodeView}
import org.scalax.semweb.rdf.{RDFValue, Res, BasicTriplet, IRI}
import org.scalax.semweb.shex.PropertyModel
import rx.core.Var

import scala.collection.immutable.HashMap
import scala.collection.mutable

//
//class SemanticEdge(from:SemanticNode,to:SemanticNode,data:Var[IRI],view:EdgeView[Var[IRI]]) extends VisualEdge[SemanticNode,Var[IRI],EdgeView[Var[IRI]]](from,to,data,view)
//
//class SemanticNode(data:Var[PropertyModel], view:NodeView[Var[PropertyModel]]) extends VisualNode[Var[PropertyModel],NodeView[Var[PropertyModel]]](data,view)
//
//import org.denigma.graphs.tools.HtmlSprite
//import org.scalajs.dom
//import org.scalajs.dom.HTMLElement
//import org.scalajs.threejs.extensions.Container3D
//
//
//class SemanticGraph(val container:HTMLElement, val width:Double = dom.window.innerWidth, val height:Double = dom.window.innerHeight)
//  extends VisualGraph with Container3D
//{
//
//  type SemanticNodeView  =  NodeView[Var[PropertyModel]]
//  type SemanticEdgeView = EdgeView[Var[IRI]]
//
//  type Node = SemanticNode
//  type Edge = SemanticEdge
//
//  type NodeData = PropertyModel
//  type EdgeData = IRI
//
//
////  def addStatement[T<:BasicTriplet](results:List[T]) = {
////    val start:Map[Res,Var[PropertyModel]] = this.nodes.map{case n:SemanticNode=>
////      val p = n.data
////      p.now.id->p
////    }
////    results.foldLeft(start){
////      case (acc,el)=> el.obj match {
////        case r:Res=>acc.get(r) match {
////          case Some(prop)=>prop() = prop.now.update(el.pred,)
////        }
////        case other=>
////      }
////    }
////  }
////
//
//  override def addNode(props:NodeData):Node =
//  {
//    val colorName = this.randColorName
//    val color = colorMap(colorName)
//    val t = nodeTagFromTitle(props.id.label,colorName)
//    val sp = new HtmlSprite(t)
//    val data = Var(props)
//    val view = new SemanticNodeView(data,t,sp,color)
//    cssScene.add(sp)
//    val n = new SemanticNode(data,view)
//    nodes.push(n)
//    n
//  }
//
//  override def addEdge(from:SemanticNode,to:SemanticNode, iri:IRI):Edge = {
//
//    val colorName = this.colorMap.collectFirst{ case (key,value) if value==from.view.color=>key} match {
//      case Some(c)=>c
//      case None=> this.randColorName
//    }
//
//    val t = edgeTagFromTitle(iri.stringValue,colorName)
//    val sp = new HtmlSprite(t)
//    sp.visible = false
//    cssScene.add(sp)
//    val data = Var(iri)
//
//    val view = new SemanticEdgeView(from.view.sprite,to.view.sprite,data,sp, LineParams(from.view.color))
//    val e  = new SemanticEdge(from,to,data,view)
//    scene.add(view.arrow)
//    edges.push(e)
//    e
//  }
//
//  override def onEnterFrame() = {
//    super.onEnterFrame()
//    this.layouts.foreach{case l=>
//      if(l.active) l.tick(nodes,edges)
//      //dom.console.info(s"l is ${l.active}")
//    }
//  }
//
//
//
//
//}
