package org.denigma.binding.frontend

import org.scalajs.dom
import org.scalajs.threejs._
import org.denigma.binding.extensions._


import scala.scalajs.js
import scala.scalajs.js.annotation.{JSName, JSExport}


object Graph {

  val layout = "3d"
  val layout_options = js.Dynamic.literal(
    attraction = 5,
    repulsion = 0.5,
    showStats = true,
    showInfo = true
  )
  val show_stats = false
  val show_info = false
  val sho_labels = true
  val selection = true
  val limit = 10
  val nodes_count = 20
  val edges_count = 10


  def init()= {



    js.eval (
      """
        |new Drawing.SimpleGraph({layout: '3d', numNodes: 10, showLabels:true, graphLayout:{attraction: 5, repulsion: 0.5}, showStats: true, showInfo: true})
      """.stripMargin)

   //new Graph()

  }
}

class Graph(val layout:String = "3d") {

  //var nodes: List

  lazy val width = dom.window.innerWidth

  lazy val height = dom.window.innerHeight

  val rendererParams = js.Dynamic.literal(alpha = true).asInstanceOf[WebGLRendererParameters]

  // Three.js initialization
  lazy val renderer = {
    val r = new WebGLRenderer(rendererParams)
    r.setSize(width, height)
    r
  }

  lazy val camera = {
    val cam = new PerspectiveCamera(40, width / height, 1, 1000000)
    cam.position.z = 5000
    cam
  }

  val scene = new Scene()
  dom.document.body.appendChild(renderer.domElement)

  val box = this.makeTestBox()

  scene.add(box)

  renderer.render(scene, camera)

  animate()


  def geo = layout.toLowerCase match {
    case "3d" => new BoxGeometry(25, 25, 25, 25)
    case _ => new BoxGeometry(50, 50, 50, 0)
  }


  protected def makeTestBox() = {
    val geometry = new BoxGeometry(200, 200, 200)

    val params = js.Dynamic.literal(
      color = 0xff000,
      wireframe = true
    ).asInstanceOf[MeshBasicMaterialParameters]

    val material = new MeshBasicMaterial(params)

    new Mesh(geometry, material)
  }

  def makeMaterial() = {
    val col = Math.random() * 0xffffff
    val params = js.Dynamic.literal(
      color = col,
      wireframe = true
    ).asInstanceOf[MeshBasicMaterialParameters]

    new MeshBasicMaterial(params)
  }

  def randomPos(area:Int = 5000) = {  Math.floor(Math.random() * (area + area + 1) - area)   }


 def randomPosition(mesh:Mesh) =
 {
   mesh.position.x = randomPos()
   mesh.position.y = randomPos()
   if (layout == "3d") mesh.position.z = randomPos()


 }


  def makeNode(id:Integer,title:String) = {

    val mesh = makeNodeMesh()
    scene.add(mesh)

    new Node(id, title, mesh)
  }

   protected def makeNodeMesh() =
   {
     val mat: Material = this.makeMaterial()
     val box = new BoxGeometry(200, 200, 200)
     val mesh = new Mesh(box, mat)
     mesh
   }

//
//  function drawNode (node) {
//    var draw_object = new THREE.Mesh(geometry, new THREE.MeshBasicMaterial({
//      color: Math.random () * 0xffffff, opacity: 0.5
//    }));
//
//    if (that.show_labels) {
//      if (node.data.title != undefined) {
//        var label_object = new THREE.Label(node.data.title);
//      } else {
//        var label_object = new THREE.Label(node.id);
//      }
//      node.data.label_object = label_object;
//      scene.add(node.data.label_object);
//    }
//
//    var area = 5000;
//    draw_object.position.x = Math.floor(Math.random() * (area + area + 1) - area);
//    draw_object.position.y = Math.floor(Math.random() * (area + area + 1) - area);
//
//    if (that.layout === "3d") {
//      draw_object.position.z = Math.floor(Math.random() * (area + area + 1) - area);
//    }
//
//    draw_object.id = node.id;
//    node.data.draw_object = draw_object;
//    node.position = draw_object.position;
//    scene.add(node.data.draw_object);



  def animate(double:Double = 0):js.Any =
  {
    box.rotation.x += 0.01
    box.rotation.y += 0.02
    renderer.render( scene, camera )
    dom.requestAnimationFrame( animate _ )

  }


}


class Node(id:Int,title:String, mesh:Mesh)
{
}