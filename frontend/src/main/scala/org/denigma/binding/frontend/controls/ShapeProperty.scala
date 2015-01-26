package org.denigma.binding.frontend.controls

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.semantic.shapes.ArcView
import org.scalajs.dom.HTMLElement
import rx.core.Var

class ShapeProperty(val elem:HTMLElement, val params:Map[String,Any]) extends ArcView
{

  override protected def attachBinders(): Unit = binders =  ArcView.defaultBinders(this)

  override def activateMacro(): Unit = {extractors.foreach(_.extractEverything(this))}

  val removeClick = Var(EventBinding.createMouseEvent())

}
