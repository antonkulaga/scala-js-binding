package org.denigma.semantic.shapes

import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.semantic.models.binders.GeneralSelectBinder
import org.denigma.semantic.rdf.RDFBinder
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex._
import org.denigma.binding.extensions._
import org.denigma.semantic.rdf.{Selector, ModelInside}
import org.scalajs.dom.HTMLElement
import org.scalajs.jquery._
import org.scalax.semweb.rdf._
import rx.Var
import org.denigma.binding.extensions._

import scala.scalajs.js
import scala.collection.immutable.Map
import scala.scalajs.js.Dynamic
import org.scalax.semweb.shex.rs
import rx.ops._


object ArcView {

  def apply(el:HTMLElement,params:Map[String,Any]) = {
    new JustArcView(el,params)
  }


  class JustArcView(val elem:HTMLElement, val params:Map[String,Any]) extends ArcView {

    override def activateMacro(): Unit = {extractors.foreach(_.extractEverything(this))}

    override protected def attachBinders(): Unit = BindableView.defaultBinders(this)

  }

}
//
//class OccursSelector(val el:HTMLElement,arc:Var[ArcRule]) extends Selector {
//
//
//  val occurs = arc.map(a=>a.occurs)
//
//  override val key: IRI = rs / "occurs"
//
//  override protected def selectParams(el: HTMLElement): Dynamic = ???
//
//  override protected def itemRemoveHandler(value: String): Unit = {
//    //arc() = arc.now.copy(occurs = )
//  }
//
//  override protected def itemAddHandler(value: String, item: js.Any): Unit = ???
//
//
//}


trait ArcView extends BindableView
{

  val arc = Var(params("item").asInstanceOf[ArcRule])

  require(params.contains("item"), "ArcView should contain arc item inside")

}

