package org.denigma.graphs.tools

import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.threejs._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@JSName("THREE.OrbitControls")
class OrbitControls( camera:Camera, element:HTMLElement) extends js.Object
{

  def this(camera:Camera) = this(camera, null)

  def rotateLeft(angle:Double):Unit = ???

  def rotateRight(angle:Double):Unit = ???

  def rotateUp(angle:Double):Unit = ???

  def rotateDown(angle:Double):Unit = ???

  def zoomIn( zoomScale:Double):Unit = ???

  def zoomOut( zoomScale:Double):Unit = ???

  def pan(distance:Vector3):Unit = ???


  def update():Unit = ???

  def getAutoRotationAngle():Double = ???

  def  getZoomScale():Double = ???

  def onMouseDown(event:dom.MouseEvent):Unit = ???

  def onMouseUp(event:dom.MouseEvent):Unit = ???

  def onMouseWheel(event:dom.MouseEvent):Unit = ???

  def onMouseMove(event:dom.MouseEvent):Unit = ???

  def onKeyDown(event:dom.KeyboardEvent):Unit = ???

  def onKeyUp(event:dom.KeyboardEvent):Unit = ???



}

@JSName("THREE.TrackballControls")
class TrackBallControls( camera:Camera) extends js.Object{

  def getMouseOnScreen(clientX:Double,clientY:Double):Vector2 = ???

  def getMouseProjectionOnBall(clientX:Double,clientY:Double):Vector3 = ???

  def rotateCamera():Unit = ???

  def zoomCamera():Unit = ???

  def panCamera():Unit = ???

  def update():Unit = ???

  def keydown( event:dom.KeyboardEvent):Unit = ???

  def keyup( event:dom.KeyboardEvent):Unit = ???

  def mousedown( event:dom.MouseEvent):Unit = ???


  def mouseup( event:dom.MouseEvent):Unit = ???

  def mousmove( event:dom.MouseEvent):Unit = ???



}
