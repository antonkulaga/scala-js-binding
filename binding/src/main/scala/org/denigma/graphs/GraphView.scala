package org.denigma.graphs

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.views.BindableView
import org.denigma.graphs.layouts.ForceLayout
import org.denigma.graphs.visual.GraphContainer
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import rx.core.Var
import rx.ops._
import rx.extensions._


abstract class GraphView(val elem:HTMLElement,val params:Map[String,Any]) extends BindableView
{
  lazy val containerId = this.resolveKeyOption("graph-container"){
    case cont:String=>cont
  }.getOrElse("graph-container")

  lazy val container: HTMLElement  = dom.document.getElementById(containerId)

  val graphContainer:GraphContainer


  override def bindView(el:HTMLElement) =
  {
    super.bindView(el)
    graphContainer.drawGraph()
    graphContainer.render()

  }




}