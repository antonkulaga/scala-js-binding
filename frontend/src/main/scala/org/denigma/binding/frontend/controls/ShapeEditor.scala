package org.denigma.binding.frontend.controls

import org.denigma.binding.extensions._
import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.semantic.binding
import org.denigma.semantic.controls.EditModelView
import org.denigma.semantic.shapes.ShapeView
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.vocabulary.XSD
import org.scalax.semweb.rdf.{IRI, vocabulary}
import org.scalax.semweb.shex.{Shape, ShapeBuilder, Star}
import rx.Var

class ShapeEditor (val elem:HTMLElement,val params:Map[String,Any]) extends  ShapeView
{

  override protected def getShape: Shape = {

    val de = IRI("http://denigma.org/resource/")
    val dc = IRI(vocabulary.DCElements.namespace)
    val art = new ShapeBuilder(de / "Article_Shape")
    art has de /"is_authored_by" occurs Star //occurs Plus
    art has de / "is_published_in" occurs Star //occurs Plus
    art has dc / "title" occurs Star //occurs ExactlyOne
    //art has de / "date" occurs Star //occurs ExactlyOne
    art has de / "abstract" of XSD.StringDatatypeIRI  occurs Star//occurs Star
    art has  de / "excerpt" of XSD.StringDatatypeIRI  occurs Star//occurs Star
    art.result
  }

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  //  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}




  val addClick = Var(EventBinding.createMouseEvent())


  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  override def bindView(el: HTMLElement) = {
    super.bindView(el)
    this.subscribeUpdates()
  }


}

class ShapeProperty(val elem:HTMLElement, val params:Map[String,Any]) extends EditModelView{



  val initial: Option[Var[binding.ModelInside]] = params.get("model").collect{case mi:Var[binding.ModelInside]=>mi}

  require(initial.isDefined,"No model received!")

  override val modelInside  = initial.get

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


  val removeClick = Var(EventBinding.createMouseEvent())

  removeClick.handler{
    this.die()
  }

}