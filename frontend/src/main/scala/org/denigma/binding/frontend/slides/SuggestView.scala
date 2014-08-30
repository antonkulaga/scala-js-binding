package org.denigma.binding.frontend.slides

import org.denigma.binding.semantic.ModelInside
import org.denigma.semantic.binding
import org.denigma.semantic.binding.ModelCollection
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, MouseEvent}
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf.{IRI, StringLiteral}
import org.scalax.semweb.shex.PropertyModel
import rx._

import scala.collection.immutable.Map
import scalatags.Text.Tag

class SuggestView(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends ModelCollection
{


  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)




  override def bindView(el:HTMLElement) {
    //jQuery(el).slideUp()
    super.bindView(el)
    this.subscribeUpdates()
    dom.console.log("collection prefixes = " + prefixes.toString())

    val p1 =binding.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("one")) )
    val p2= binding.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("two")))
    val p3 =binding.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("three")))
    val p4 =binding.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("four")))
    val p5 =binding.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("five")))
    val p6 =binding.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("six")))

    this.items() = List(Var(p1),Var(p2),Var(p3),Var(p4),Var(p5),Var(p6))

  }



}