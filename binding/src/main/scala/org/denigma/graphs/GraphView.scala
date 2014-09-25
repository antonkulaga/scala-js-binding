package org.denigma.graphs

import org.denigma.binding.views.BindableView
import org.denigma.graphs.visual.GraphContainer
import org.scalajs.dom
import org.scalajs.dom.HTMLElement


abstract class GraphView(val elem:HTMLElement,val params:Map[String,Any]) extends BindableView
{
  lazy val containerId = this.resolveKeyOption("graph-container"){
    case cont:String=>cont
  }.getOrElse("graph-container")

  lazy val container: HTMLElement  = dom.document.getElementById(containerId)

  lazy val graphContainer =     new GraphContainer(container,1000,1000)


  override def bindView(el:HTMLElement) =
  {
    super.bindView(el)
    graphContainer.render()
  }


}