package org.denigma.graphs.tools

import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.threejs._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName



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
