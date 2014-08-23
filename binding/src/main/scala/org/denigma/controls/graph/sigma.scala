package org.denigma.controls.graph

import org.scalajs.dom
import org.scalajs.dom.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportDescendentObjects, JSName}

@JSName("sigma")
object Sigma extends js.Object {

  def utils:SigmaUtils = ???

  def canvas:SigmaCanvas = ???

}

@JSName("graph")
class SigmaGraph extends js.Object{

  def addEdge(edge:SigmaEdgeLike):Unit = ???

  def addNode(node:SigmaNodeLike):Unit = ???


}


@JSName("sigma")
class Sigma(init:Any) extends js.Object {

  def startForceAtlas2():Unit = ???


  def graph:SigmaGraph = ???

}

@JSName("utils")
class SigmaUtils extends js.Object{

  def pkg(str:String):Unit = ???

}

class SigmaCanvas extends js.Object {

}


case class SigmaNode(id:String,label:String,x:Int,y:Int,size:Int) extends SigmaNodeLike
case class SigmaEdge(id:String,source:String,target:String) extends SigmaEdgeLike

case class SigmaInit(graph:SigmaGraphInit,renderer:js.Dynamic)
{

}

@JSExportDescendentObjects
trait SigmaInitLike {

  @JSExport
  val graph:SigmaGraphInitLike

  @JSExport
  val renderer:js.Dynamic
}


@JSExport
case class SigmaGraphInit(vertices:Seq[SigmaNode],links:Seq[SigmaEdge]) extends SigmaGraphInitLike{

  @JSExport
  val nodes: js.Array[SigmaNode] = js.Array(vertices:_*)

  @JSExport
  val edges: js.Array[SigmaEdge] = js.Array(links:_*)


}




@JSExportDescendentObjects
trait SigmaGraphInitLike {
  @JSExport
  val nodes:js.Array[SigmaNode]

  @JSExport
  val edges:js.Array[SigmaEdge]
}

@JSExportDescendentObjects
trait SigmaNodeLike {

  @JSExport
  def id:String

  @JSExport
  def label:String

  @JSExport
  def x:Int

  @JSExport
  def y:Int

  @JSExport
  def size:Int

}

@JSExportDescendentObjects
trait SigmaEdgeLike {

  @JSExport
  def id:String

  @JSExport
  def source:String

  @JSExport
  def target:String

}