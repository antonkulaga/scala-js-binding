package org.denigma.semantic.shapes


import org.denigma.semantic.binders.shaped.ShapePropertyBinder
import org.denigma.semantic.models.SelectableModelView
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex.ArcRule
import rx.Var

import scala.collection.immutable.Map
//
object PropertyView {
  def apply(el:HTMLElement,mp:Map[String,Any]) = {
    new JustPropertyView(el,mp)
  }

  def defaults(view:PropertyView) = {
    new ShapePropertyBinder(view,view.model,view.arc,suggest = view.suggest)
  }

}

class JustPropertyView(val elem:HTMLElement,val params:Map[String,Any]) extends PropertyView
{
  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit = {
    //binders = new PropertyBinder(elem,this.model,arc,)::Nil
    //NOT IMPLEMENTED
  }
}

/**
 * View to display property and its name
 * TODO: refactor in Future
 */
trait PropertyView extends SelectableModelView {


  val arc = this.resolveKey("arc"){case a:ArcRule=>a}

  override val model = this.resolveKey("model"){case m:Var[ModelInside]=>m}


}
