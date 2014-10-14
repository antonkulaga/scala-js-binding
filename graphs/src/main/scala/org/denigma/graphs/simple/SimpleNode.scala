package org.denigma.graphs.simple

import org.denigma.graphs.core.{Subject, VisualNode}
import org.denigma.graphs.visual._
import org.scalajs.dom
import org.scalajs.dom.{Event, MouseEvent}
import rx.core.Var




class SimpleNode(data:Var[String], view:NodeView[Var[String]]) extends VisualNode[Var[String],NodeView[Var[String]]](data,view)
{
  def id = data.now

  override def receive:PartialFunction[Any,Unit] = {


    case other => dom.console.log(s"unknown message $other")
    //nothing
  }

  def onMouseOver( event:MouseEvent ):Unit =   {
    send("mouseover")
  }


  def onMouseOut(sub:Subject)( event:MouseEvent ):Unit =   {
    send("mouseout")
  }

  view.sprite.element.addEventListener( "mouseover", (this.onMouseOver _).asInstanceOf[Function[Event,_ ]] )
  view.sprite.element.addEventListener( "mouseout", (this.onMouseOut _).asInstanceOf[Function[Event,_ ]] )


}




