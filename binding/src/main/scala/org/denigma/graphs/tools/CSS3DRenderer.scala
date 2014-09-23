package org.denigma.graphs.tools

import org.scalajs.dom.HTMLElement
import org.scalajs.threejs
import org.scalajs.threejs._

import scala.scalajs.js.annotation.JSName

@JSName("THREE.CSS3DObject")
class CSS3DObject(element:HTMLElement) extends Object3D
{

}

@JSName("THREE.CSS3DSprite")
class CSS3DSprite(element:HTMLElement) extends CSS3DObject(element){

}


@JSName("THREE.CSS3DRenderer")
class CSS3DRenderer extends Renderer {

//  def render(scene: Scene, camera: Camera): Unit = ???
//  def setSize(width: Double, height: Double, updateStyle: Boolean = ???): Unit = ???
//  var domElement: HTMLCanvasElement = ???
  //def this(parameters: WebGLRendererParameters = ???) = this()

  def setSize(width:Double,height:Double):Unit = ???

  def epsilon(value:Double) = ???

  def getObjectCSSMatrix(matrix:threejs.Matrix3):String= ???

  def getCameraCSSMatrix(matrix:threejs.Matrix3):String= ???

  def renderObject(obj:CSS3DObject,camera:Camera):Unit = ???


}
