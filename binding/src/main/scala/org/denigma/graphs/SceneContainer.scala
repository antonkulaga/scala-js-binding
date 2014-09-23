package org.denigma.graphs

import org.denigma.binding.extensions._
import org.denigma.binding.messages.GraphMessages
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.BindableView
import org.denigma.semantic.storages.Storage
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.sigma.{SigmaEdge, SigmaNode}
import org.scalajs.spickling.PicklerRegistry
import org.scalajs.threejs
import org.scalajs.threejs._
import org.scalax.semweb.rdf.{IRI, Quad, Res}
import org.scalax.semweb.sparql.Pat

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Dynamic

abstract class SceneContainer(val container:dom.HTMLElement, width:Double = dom.window.innerWidth, height:Double = dom.window.innerHeight) {

  type RendererType <:Renderer

  val scene = new Scene()


  lazy val distance = 1000


  lazy val renderer: RendererType = this.initRenderer()


  lazy val camera = initCamera()


  def aspectRatio = width /height


  protected def initRenderer():RendererType


  protected def initCamera() =
  {
    val camera = new PerspectiveCamera(40, this.aspectRatio, 1, 1000000)
    camera.position.z = distance
    camera
  }



  protected def onEnterFrameFunction(double: Double): Unit = {
    onEnterFrame()
    render()
  }

  def onEnterFrame():Unit = {
      renderer.render(scene, camera)
  }



  def render() =  dom.requestAnimationFrame(  onEnterFrameFunction _ )


}


