package org.denigma.graphs.core

import org.denigma.graphs.layouts.LayoutInfo
import org.denigma.graphs.tools.HtmlSprite
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.threejs.{ArrowHelper, Vector3, Object3D}
import rx._




class VisualNode[Data,View](val data:Data,val view:View) extends DataHolder[Data,View]  with Subject
{

  override def receive:PartialFunction[Any,Unit] = {

    case other => dom.console.log(s"unknown message $other")
  }

  override var observers: List[Subject]  = List.empty[Subject]
}

class VisualEdge[Node<:Subject,EdgeDataType,EdgeView](val from:Node, val to:Node, val data:EdgeDataType, val view:EdgeView) extends EdgeLike[Node,EdgeDataType,EdgeView] with Subject
{
  override var observers:List[Subject] = from::to::Nil

  observers.foreach(o=>o.observers= this::o.observers)

  override def receive:PartialFunction[Any,Unit] = {

    case other => dom.console.log(s"unknown message $other")
    //nothing
  }


}

