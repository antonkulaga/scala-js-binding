package org.denigma.graphs

import org.denigma.graphs.tools._
import org.scalajs.dom
import org.scalajs.dom.{HTMLHeadingElement, HTMLElement}
import org.scalajs.threejs._

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

import scala.scalajs.js
import scala.scalajs.js.Dynamic


class GraphContainer(container:HTMLElement,width:Double = dom.window.innerWidth, height:Double = dom.window.innerHeight)
  extends SceneContainer(container,width,height)
{



  override type RendererType =  WebGLRenderer

  protected def initRenderer= {
    val params = Dynamic.literal(
      antialias = true,
      alpha = true
      //canvas = container
    ).asInstanceOf[ WebGLRendererParameters]
    val vr = new WebGLRenderer(params)

    vr.domElement.style.position = "absolute"
    vr.domElement.style.top	  = "0"
    vr.domElement.style.margin	  = "0"
    vr.domElement.style.padding  = "0"
    vr.setSize(width,height)
    vr
  }
  val  cssScene = new Scene()

  val cssRenderer:CSS3DRenderer = this.initCSSRenderer


  protected def initCSSRenderer = {
    val rendererCSS = new CSS3DRenderer()
    rendererCSS.setSize(width,height)
    rendererCSS.domElement.style.position = "absolute"
    rendererCSS.domElement.style.top	  = "0"
    rendererCSS.domElement.style.margin	  = "0"
    rendererCSS.domElement.style.padding  = "0"
    rendererCSS
  }


  val controls = new OrbitControls(camera,this.container)
  container.style.width = width.toString
  container.style.height = height.toString


  def addSlide(path:String) = {
    val element:dom.HTMLIFrameElement	= dom.document.createElement("iframe").asInstanceOf[dom.HTMLIFrameElement]
    element.src = "/slides/data"
    var elementWidth = width
    // force iframe to have same relative dimensions as planeGeometry
    var elementHeight = height  //* this.aspectRatio
    element.style.width  = elementWidth + "px"
    element.style.height = elementHeight + "px"

    var cssObject = new CSS3DObject( element )
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



  container.appendChild( cssRenderer.domElement )
  //container.appendChild( cssRenderer.domElement )
  cssRenderer.domElement.appendChild( renderer.domElement )

  //addSlide("bind")
  //addMesh()
  drawGraph()

  def rand() = (0.5-Math.random()) * distance
  def rand2() = new Vector2(rand(),rand())
  def rand3() = new Vector3(rand(),rand(),rand())
  def randColor() = Math.random() * 0xFFFFFF


  def drawGraph() = {
    for{ i <- 1 until 10}{

      drawBox(rand3())
      drawSprite("HELLO_#"+i,rand3())
    }

  }


  override def onEnterFrame() = {
    controls.update()
    cssRenderer.render( cssScene, camera )
    renderer.render( scene, camera )
  }


  def drawBox(pos:Vector3) = {
    val geometry = new BoxGeometry( 50, 50,50 )
    val matParams = Dynamic.literal( color = Math.random() * 0xFFFFFF, opacity = 0.5 ).asInstanceOf[MeshBasicMaterialParameters]
    val m = new Mesh( geometry, new MeshBasicMaterial( matParams ) )
    scene.add(m)
  }

  def drawSprite(text:String, pos:Vector3) = {

    val h: TypedTag[HTMLHeadingElement] = h1(id:="title", "This is a title",  input("some text")   )
    val title = h.render
    title.style.background = s"#${this.randColor().toString}"
    //title.textContent = text
    title.contentEditable = "true"
    val sp  = new CSS3DSprite(title)
    sp.position = pos
    cssScene.add(sp)
  }
}
