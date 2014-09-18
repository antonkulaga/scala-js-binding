package org.denigma.semantic.shapes


import org.denigma.binding.binders.GeneralBinder
import org.denigma.semantic.binders.shaped.ShapePropertyBinder
import org.denigma.semantic.models.RemoteModelView
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex.ArcRule

import scala.collection.immutable.Map
//
object PropertyView {

  def apply(el:HTMLElement,mp:Map[String,Any]) = {
    new JustPropertyView(el,mp)
  }

  def defaultBinders(view:PropertyView) = {
    new ShapePropertyBinder(view,view.model,view.arc,suggest = view.suggest)::new GeneralBinder(view)::Nil
  }

}

class JustPropertyView(val elem:HTMLElement,val params:Map[String,Any]) extends PropertyView
{

  debug("MODEL ="+model.now.toString)

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit = {
    binders = PropertyView.defaultBinders(this)
  }
}

/**
 * View to display property and its name
 * TODO: refactor in Future
 */
trait PropertyView extends RemoteModelView {


  val arc = this.resolveKey("arc"){case a:ArcRule=>a}

}
