package org.denigma.graphs.semantic

import org.denigma.graphs.core.{Subject, VisualNode}
import org.denigma.graphs.visual._
import org.scalajs.dom
import org.scalajs.dom.{Event, MouseEvent}
import org.scalax.semweb.shex.PropertyModel
import rx.core.Var


class SemanticNode(data:Var[PropertyModel], view:NodeView[Var[PropertyModel]]) extends VisualNode[Var[PropertyModel],NodeView[Var[PropertyModel]]](data,view)
{
  def id = data.now

  override def receive:PartialFunction[Any,Unit] = {

    case other => dom.console.log(s"unknown message $other")
    //nothing
  }

  def onMouseOver( event:MouseEvent ):Unit =   {
    send("mouseover")
  }


  def onMouseOut( event:MouseEvent ):Unit =   {
    send("mouseout")
  }

  view.sprite.element.addEventListener( "mouseover", (this.onMouseOver _).asInstanceOf[Function[Event,_ ]] )
  view.sprite.element.addEventListener( "mouseout", (this.onMouseOut _).asInstanceOf[Function[Event,_ ]] )


}

