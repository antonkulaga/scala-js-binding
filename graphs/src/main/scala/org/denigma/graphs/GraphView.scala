package org.denigma.graphs

import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.denigma.graphs.core.VisualGraph
import org.denigma.graphs.simple.SimpleGraph
import org.denigma.graphs.tools.HtmlSprite
import org.denigma.graphs.visual.SpriteMaker
import org.scalajs.dom
import org.scalajs.dom.{Event, HTMLDivElement, HTMLElement}
import org.scalax.semweb.rdf.{IRI, Quad, Res}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}
import scalatags.JsDom.all._


abstract class AjaxGraphView(val elem:HTMLElement,val params:Map[String,Any]) extends GraphView
{

  type VizGraph <: SimpleGraph//SemanticGraph
  val graph:VizGraph


  lazy val resource = this.resolveKey("resource"){
    case v:Res=> v
    case v:String if v.contains(":")=>IRI(v)
  }

  lazy val path:String = this.resolveKey("path"){
    case v:IRI=>v.stringValue
    case v:String if v.contains(":") =>v
    case v => sq.withHost(v.toString)
  }

  lazy val properties = this.resolveKeyOption("properties"){
    case props:List[IRI]=>props
    case props:String=>props.split(";").map(IRI.apply).toList
  }.getOrElse(List.empty[IRI])

  lazy val storage:GraphStorage = new GraphStorage(path)

  override def bindView(el:HTMLElement) =
  {
    this.attachBinders()
    activateMacro()
    this.bind(el)
    storage.explore(resource,this.properties).onComplete{
      case Success(results)=>this.onLoad(results)
      case Failure(th)=>dom.console.error("GRAPH ERROR"+th.toString)
    }
  }

  def onLoad(results:List[Quad]) = {
    dom.console.info(s"new graph data arrived ${results.toString()}")
    //graph.addStatements(results)
    graph.render()
  }

}


trait GraphView extends BindableView with SpriteMaker
{
  lazy val containerId = this.resolveKeyOption("graph-container"){
    case cont:String=>cont
  }.getOrElse("graph-container")

  type VizGraph<:VisualGraph
  val graph:VizGraph

  lazy val container: HTMLElement  = dom.document.getElementById(containerId)


  override def nodeTagFromTitle(title:String,colorName:String): HTMLElement = {

    def cl(ins:String) = s"ui ${ins} inverted header"

    val l: HTMLDivElement =  div(title,   `class`:=cl(s"large $colorName"), padding :="10px",  borderRadius := "10px"  ).render

    def onMouseOver(event:Event) = l.className = cl(s"huge $colorName")

    def onMouseOut(event:Event) = l.className = cl(s"large $colorName")

    l.addEventListener( "mouseover", onMouseOver _ )
    l.addEventListener( "mouseout", onMouseOut _ )


    l.contentEditable = "true"
    l
  }


  override def edgeTagFromTitle(title:String,colorName:String): HTMLElement = {
    val l = label(title, `class` := s"ui tiny ${colorName} header", margin := "0px").render
    l.contentEditable = "true"
    l
  }

}