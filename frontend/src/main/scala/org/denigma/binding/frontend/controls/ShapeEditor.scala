package org.denigma.binding.frontend.controls

import org.denigma.binding.extensions._
import org.denigma.semantic.binding
import org.denigma.semantic.binding.ActiveModelView
import org.denigma.semantic.controls.AjaxModelCollection
import org.denigma.semantic.shapes.ShapeView
import org.scalajs.dom.{HTMLElement, MouseEvent}
import org.scalax.semweb.rdf.{vocabulary, IRI}
import org.scalax.semweb.rdf.vocabulary.XSD
import org.scalax.semweb.shex.{Star, ShapeBuilder}
import rx.Var
import rx.core.Rx

import scalatags.Text.Tag

class ShapeEditor (val elem:HTMLElement,val params:Map[String,Any]) extends  ShapeView
{

  override def getShape = {
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


  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def mouseEvents: Predef.Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)


  val addClick = Var(this.createMouseEvent())

  addClick handler {
    //this.addItem()
  }

}

class ShapeProperty(val elem:HTMLElement, val params:Map[String,Any]) extends ActiveModelView{



  val initial: Option[Var[binding.ModelInside]] = params.get("model").collect{case mi:Var[binding.ModelInside]=>mi}

  require(initial.isDefined,"No model received!")

  override val modelInside  = initial.get

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  val removeClick = Var(this.createMouseEvent())

  removeClick.handler{
    this.die()
  }

}