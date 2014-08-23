package org.denigma.binding.frontend.slides



import org.denigma.binding.views.OrdinaryView
import org.denigma.controls.graph._
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import play.api.libs.json.JsArray
import rx.Rx
import org.scalajs.dom.HTMLElement
import rx.Var
import rx.core.Dynamic
import scala.scalajs.js.annotation.JSExport
import scalatags.Text.Tag
import scalajs.js
import scalajs.js.{GlobalScope=>g}
import org.denigma.binding.extensions._


class GraphSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView
{

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)


  protected def generateInit() = {

    val cont: HTMLElement = dom.document.getElementById("graph-container")

    val colors = List(
      "#617db4",
      "#668f3c",
      "#c6583e",
      "#b956af")

    val g = SigmaGraphInit(List(
      SigmaNode("n0","A node",0,0,4),
      SigmaNode("n1","A node",2,1,4),
      SigmaNode("n2","NEW NODE", 1, 2 , 4)
    ),List(SigmaEdge("e0","n0","n1"),
      SigmaEdge("e1","n1","n2"),
      SigmaEdge("e2","n2","n0")))

    //SigmaInit(dom.document.getElementById("graph-container"),g)


    val renderer = js.Dynamic.literal(
      container = cont,
      `type` = "canvas"
    )

    js.Dynamic.literal(
      graph = g.asInstanceOf[js.Dynamic],
      renderer = renderer
    )


    //new SigmaInit(g,renderer)



  }



  override def bindView(el:HTMLElement) {
    //jQuery(el).slideUp()
    super.bindView(el)
    Sigma.utils.pkg("sigma.canvas.edges")
    val init = this.generateInit()
    val s = new Sigma(init)
    s.graph.addNode(SigmaNode("n4","A node",0,0,4))
    s.graph.addEdge(SigmaEdge("e4","n4","n0"))
    s.startForceAtlas2()

  }



}



