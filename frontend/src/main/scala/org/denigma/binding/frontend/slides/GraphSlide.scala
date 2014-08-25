package org.denigma.binding.frontend.slides


import org.denigma.binding.views.OrdinaryView
import org.denigma.controls.graph._
import org.denigma.controls.sigma.{Sigma, SigmaGraphInit}
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalax.semweb.rdf.{Res, Quad, IRI}
import play.api.libs.json.JsArray
import rx.Rx
import org.scalajs.dom.HTMLElement
import rx.Var
import rx.core.Dynamic
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success}
import scalatags.Text.Tag
import scalajs.js
import scalajs.js.{GlobalScope=>g}
import org.denigma.binding.extensions._


class GraphSlide(val elem:HTMLElement, val params:Map[String,Any]) extends GraphView
{


  lazy val path: String = this.params.get("path").map(_.toString).get

  lazy val resource = this.params.get("resource").map(v=>IRI(v.toString)).get

  //require(params.contains("path"))


  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)



  override def bindView(el:HTMLElement) = {
    super.bindView(el)
    this.draw()
    //jQuery(el).slideUp()
//    super.bindView(el)
//    Sigma.utils.pkg("sigma.canvas.edges")
//    this.sigma =  new Sigma(initial)
//    this.storage.explore(this.resource).onComplete{
//      case Success(data) =>
//        this.loadData(data)
//      case Failure(th)=>
//        this.error(s"failure in read of model for $path: \n ${th.getMessage} ")
//    }

  }


  protected def draw() = {
    js.eval (
      """
        |new Drawing.SimpleGraph({layout: '3d', numNodes: 10, showLabels:true, graphLayout:{attraction: 5, repulsion: 0.5}, showStats: true, showInfo: true})
      """.stripMargin)

  }


//  override protected def loadData(data:List[Quad]) = {
//    super.loadData(data)
//    sigma.startForceAtlas2()
//  }

  override def container: HTMLElement = dom.document.getElementById("graph-container")

}



