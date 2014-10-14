package org.denigma.graphs

import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.denigma.graphs.core.{SpriteGraph, VisualGraph}
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}



abstract class AjaxGraphView(elem:HTMLElement, params:Map[String,Any]) extends GraphView
{

  type VizGraph  <: SpriteGraph

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

  def onLoad(results:List[Quad],onlyNew:Boolean = false):Unit

}

trait GraphView extends BindableView
{
  lazy val containerId = this.resolveKeyOption("graph-container"){
    case cont:String=>cont
  }.getOrElse("graph-container")

  type VizGraph<:VisualGraph
  val graph:VizGraph

  lazy val container: HTMLElement  = dom.document.getElementById(containerId)


}