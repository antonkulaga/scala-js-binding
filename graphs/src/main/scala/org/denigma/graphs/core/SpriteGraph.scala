package org.denigma.graphs.core

import com.softwaremill.macwire.Wired
import org.denigma.graphs.semantic.SemanticNode
import org.denigma.graphs.visual.NodeView
import org.scalajs.threejs.Object3D
import org.scalajs.threejs.extensions.Container3D

import org.denigma.graphs.layouts.GraphLayout
import org.denigma.graphs.misc.Randomizable
import org.denigma.graphs.tools.HtmlSprite
import org.scalajs.dom
import org.scalajs.dom.{MouseEvent, Event, HTMLElement, HTMLLabelElement}
import org.scalajs.threejs.extensions.controls.JumpCameraControls


trait SpriteGraph extends VisualGraph with Randomizable with Container3D {


  def onMouseDown(obj:Object3D)( event:MouseEvent ):Unit =  if(event.button==0)
  {
    this.controls.moveTo(obj.position)
  }



  override val controls:JumpCameraControls = new  JumpCameraControls(camera,this.container,scene,this.width,this.height)

  override def defRandomDistance = distance * 0.6

  def randomPos(obj:Object3D) =  obj.position.set(rand(),rand(),rand())

  override type ViewOfNode <:{
    def sprite:HtmlSprite
  }


  override def onEnterFrame() = {
    super.onEnterFrame()
    this.layouts.foreach{case l=>
      if(l.active) l.tick()
      //dom.console.info(s"l is ${l.active}")
    }
  }

}
