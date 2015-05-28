package org.denigma.semantic.schema

import org.denigma.semantic.shapes.ArcView
import org.scalajs.dom.raw.HTMLElement

class ShapeProperty(val elem:HTMLElement, val params:Map[String,Any]) extends ArcView
{

  override protected def attachBinders(): Unit = binders =  ArcView.defaultBinders(this)

  override def activateMacro(): Unit = {extractors.foreach(_.extractEverything(this))}

}
