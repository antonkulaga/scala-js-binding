package org.denigma.semantic.shapes


import org.denigma.binding.binders.{NavigationBinding, GeneralBinder}
import org.denigma.semantic.binders.SelectBinder
import org.denigma.semantic.binders.shaped.ShapedPropertyBinder
import org.denigma.semantic.models.RemoteModelView
import org.denigma.semantic.rdf.ShapeInside
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.scalax.semweb.shex.{Shape, ArcRule}
import rx.core.Var

import scala.collection.immutable.Map

object PropertyView {

  def apply(el:HTMLElement,mp:Map[String,Any]) =   new JustPropertyView(el,mp)

  def selectableBinders(view:PropertyView): List[ShapedPropertyBinder] = new ShapedPropertyBinder(view,view.model,view.arc)(suggest = view.suggest)::Nil

  class JustPropertyView(val elem:HTMLElement,val params:Map[String,Any]) extends PropertyView
  {

    override def activateMacro(): Unit = {}

    override protected def attachBinders(): Unit =    binders =     new ShapedPropertyBinder(this,this.model,this.arc)(suggest = this.suggest)::Nil

  }
}


/**
 * View to display property and its name
 * TODO: refactor in Future
 */
trait PropertyView extends RemoteModelView {


  lazy val arc = this.resolveKey("arc"){case a:ArcRule=>a}

}