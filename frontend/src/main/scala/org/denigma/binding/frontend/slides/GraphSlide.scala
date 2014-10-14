package org.denigma.binding.frontend.slides

import org.denigma.binding.binders.GeneralBinder
import org.denigma.graphs._
import org.scalajs.dom.HTMLElement

import scala.collection.immutable.Map

class GraphSlide(elem:HTMLElement, params:Map[String,Any]) extends SemanticGraphView(elem:HTMLElement,params:Map[String,Any])
{


  def defWidth = 1280 //NAPILNIK
  def defHeight = 768 //TODO: fix

  override lazy val graph:VizGraph = new VizGraph(this.container,defWidth,defHeight)


  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit = withBinders(new GeneralBinder(this))



}