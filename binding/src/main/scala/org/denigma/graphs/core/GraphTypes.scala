package org.denigma.graphs.core

import org.denigma.graphs.tools.HtmlSprite
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.threejs.{ArrowHelper, Vector3, Object3D}
import rx._


class GraphTypes
{

  def defColor =  0x00ff00

  def defHeadLength = 30

  def defHeadWidth= 10



  type DataType<:Var[_]



  class NodeView(val data:DataType,val tag:HTMLElement,val sprite:HtmlSprite, val color:Double = defColor) {

  }


  class Node(val data:DataType,val view:NodeView) extends DataHolder{
    type View = NodeView
    type Data = DataType

  }

  case class LineParams(lineColor:Double = defColor,headLength:Double = defHeadLength, headWidth:Double = defHeadWidth)

  class EdgeView(val from:Object3D,val to:Object3D, data:DataType, sprite:HtmlSprite, lp:LineParams = LineParams())
  {
    def middle = new Vector3((sourcePos.x+targetPos.x)/2,(sourcePos.y+targetPos.y)/2,(sourcePos.z+targetPos.z)/2)

    def sourcePos: Vector3 = from.position
    def targetPos: Vector3 = to.position
    def direction = new Vector3().subVectors(targetPos, sourcePos)

    def posArrow() = {
      arrow.setDirection(direction.normalize())
      arrow.setLength(direction.length())
    }

    def posSprite() = {

      sprite.position = this.middle
    }

    import lp._
    val arrow =  new ArrowHelper(direction.clone().normalize(), sourcePos, direction.length(), lineColor, headLength, headWidth)
    arrow.addEventListener("mouseover",this.onLineMouseOver _)
    arrow.addEventListener("mouseout",this.onLineMouseOver _)

    this.posSprite()

    def onLineMouseOver(event:Any):Unit = {
      this.sprite.visible = true
      dom.console.log("onMouseOver")
    }
    def onLineMouseOut(event:Any):Unit = {
      this.sprite.visible = false
      dom.console.log("onMouseOut")
    }

  }

  class Edge(val from:Node, val to:Node, val data:DataType, val view:EdgeView) extends EdgeLike[Node]
  {
    type View = EdgeView
    override type Data = DataType

  }
}
