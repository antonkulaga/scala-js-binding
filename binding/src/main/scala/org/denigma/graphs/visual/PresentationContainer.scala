package org.denigma.graphs.visual

import org.denigma.graphs.misc.Randomizable
import org.denigma.graphs.tools._
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, HTMLHeadingElement}
import org.scalajs.threejs._
import org.scalajs.threejs.extensions.Container3D

import scala.scalajs.js.Dynamic
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

class PresentationContainer(val container:HTMLElement, val width:Double = dom.window.innerWidth, val height:Double = dom.window.innerHeight) extends Container3D {

  def addSlide(path:String) = {
    val element:dom.HTMLIFrameElement	= dom.document.createElement("iframe").asInstanceOf[dom.HTMLIFrameElement]
    element.src = "/slides/data"
    var elementWidth = width
    // force iframe to have same relative dimensions as planeGeometry
    var elementHeight = height  //* this.aspectRatio
    element.style.width  = elementWidth + "px"
    element.style.height = elementHeight + "px"

    var cssObject = new HtmlObject( element )
    cssObject.scale = new Vector3(0.5,0.5,0.5)
    cssObject.position = new Vector3(0,0,0)
    cssScene.add(cssObject)
  }


  def drawBox() = {

    val geometry = new BoxGeometry( 100, 100, 100 )

    val matParams = Dynamic.literal( color = Math.random() * 0xFFFFFF, opacity = 0.5 ).asInstanceOf[MeshBasicMaterialParameters]

    val material = new MeshBasicMaterial( matParams )

    val mesh: Mesh = new Mesh( geometry, material )

    mesh.position = new Vector3(0,0,0)



    scene.add(mesh)
  }


  def drawBox(pos:Vector3) = {
    val geometry = new BoxGeometry( 50, 50,50 )
    val matParams = Dynamic.literal( color = Math.random() * 0xFFFFFF, opacity = 0.5 ).asInstanceOf[MeshBasicMaterialParameters]
    val m = new Mesh( geometry, new MeshBasicMaterial( matParams ) )
    scene.add(m)
  }





}
