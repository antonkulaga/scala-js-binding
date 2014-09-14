package org.denigma.binding.frontend.controls

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.semantic.models.EditModelView
import org.denigma.semantic.rdf.ModelInside
import org.denigma.semantic.shapes.{ArcView, ShapeView}
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.vocabulary.XSD
import org.scalax.semweb.rdf.{IRI, vocabulary}
import org.scalax.semweb.shex.{Shape, ShapeBuilder, Star}
import rx.Var

class ShapeEditor (val elem:HTMLElement,val params:Map[String,Any]) extends   ShapeView
{

 override lazy val initialShape: Shape =     {
    val de = IRI("http://denigma.org/resource/")
    val dc = IRI(vocabulary.DCElements.namespace)
    val art = new ShapeBuilder(de / "Article_Shape")
    art has de /"is_authored_by" occurs Star //*/occurs Plus
    art has de / "is_published_in" occurs Star //occurs Plus
    art has dc / "title" occurs Star //occurs ExactlyOne
    //art has de / "date" occurs Star //occurs ExactlyOne
    art has de / "abstract" of XSD.StringDatatypeIRI  occurs Star//occurs Star
    art has  de / "excerpt" of XSD.StringDatatypeIRI  occurs Star//occurs Star
    art.result
  }


  val addClick = Var(EventBinding.createMouseEvent())

  override protected def attachBinders(): Unit = binders = BindableView.defaultBinders(this)

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  override def bindView(el: HTMLElement) = {
    super.bindView(el)
    this.subscribeUpdates()
    updateShape(this.initialShape)
  }

}

class ShapeProperty(val elem:HTMLElement, val params:Map[String,Any]) extends ArcView
{

  override protected def attachBinders(): Unit = binders =  ArcView.defaultBinders(this)

  override def activateMacro(): Unit = {extractors.foreach(_.extractEverything(this))}



  val removeClick = Var(EventBinding.createMouseEvent())

  removeClick.handler{
    //this.die()
  }


}