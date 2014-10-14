package org.denigma.graphs.visual
import org.denigma.graphs.layouts.LayoutInfo
import org.denigma.graphs.tools.HtmlSprite
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.threejs.{ArrowHelper, Vector3, Object3D}
import rx._


import org.denigma.graphs.tools.HtmlSprite
import org.scalajs.dom
import org.scalajs.threejs.{ArrowHelper, Vector3, Object3D}

class EdgeView[EdgeDataType](val from:Object3D,val to:Object3D, val edgeData:EdgeDataType, val sprite:HtmlSprite, lp:LineParams = LineParams())
{

  def sourcePos: Vector3 = from.position
  def targetPos: Vector3 = to.position
  def middle = new Vector3((sourcePos.x+targetPos.x)/2,(sourcePos.y+targetPos.y)/2,(sourcePos.z+targetPos.z)/2)

  def direction = new Vector3().subVectors(targetPos, sourcePos)

  protected def posArrow() = {
    arrow.position = sourcePos
    arrow.setDirection(direction.normalize())
    arrow.setLength(direction.length()-10, lp.headLength, lp.headWidth)
  }

  protected def posSprite() = {
    val m = middle
    sprite.position.set(m.x,m.y,m.z)
  }

  import lp._
  val arrow =  new ArrowHelper(direction.normalize(), sourcePos, direction.length(), lineColor, headLength, headWidth)
  arrow.addEventListener("mouseover",this.onLineMouseOver _)
  arrow.addEventListener("mouseout",this.onLineMouseOver _)

  def onLineMouseOver(event:Any):Unit = {
    this.sprite.visible = true
    dom.console.log("onMouseOver")
  }

  def onLineMouseOut(event:Any):Unit = {
    this.sprite.visible = false
    dom.console.log("onMouseOut")
  }

  def update() = {
    posArrow()
    posSprite()
  }

  this.update()



}
