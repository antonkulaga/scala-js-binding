package org.denigma.semantic.shapes

import org.denigma.binding.binders.{GeneralBinder, NavigationBinding}
import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.shaped.OccursBinder
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex._
import rx.Var

import scala.collection.immutable.Map

object ArcView {

  def apply(el:HTMLElement,params:Map[String,Any]) = {
    new JustArcView(el,params)
  }

  implicit def defaultBinders(view:ArcView) = new OccursBinder(view,view.arc)::new GeneralBinder(view)::new NavigationBinding(view)::Nil

  class JustArcView(val elem:HTMLElement, val params:Map[String,Any]) extends ArcView {

    override def activateMacro(): Unit = {extractors.foreach(_.extractEverything(this))}

    override protected def attachBinders(): Unit = binders =  defaultBinders(this)

  }

}



trait ArcView extends BindableView
{

  val arc = params("item").asInstanceOf[Var[ArcRule]]

  require(params.contains("item"), "ArcView should contain arc item inside")

}

