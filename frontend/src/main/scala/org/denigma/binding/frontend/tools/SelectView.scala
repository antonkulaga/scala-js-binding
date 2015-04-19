package org.denigma.binding.frontend.tools

import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.selectize.Selectize
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.jquery.jQuery
import org.denigma.semweb.rdf.IRI
import rx.core.Var

import scala.scalajs.js
import scala.scalajs.js.{GlobalScope => g}


class SelectView(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView {

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  val columns = Var("ui page grid")


  val spec= IRI("http://en.wikipedia.org/wiki/Spectrometers" )
  val star = IRI("http://en.wikipedia.org/wiki/Star_chart")
  val elec = IRI("http://en.wikipedia.org/wiki/Electrical_tape")
  val items = js.Array(
    js.Dynamic.literal( id = spec.stringValue , title = "Spectometer"),
    js.Dynamic.literal( id = star.stringValue, title = "Star Chart"),
    js.Dynamic.literal( id = elec.stringValue, title = "Electrical Tape" )

  )

  val testItem =     js.Dynamic.literal( id = 4, title = "Denigma", url = "http://denigma.de/" )

  def typeHandler(str:String) = {
    dom.console.log("typed = "+str)
  }

  def changeHandler(str:String) = {
    dom.console.log("changed = "+str)
  }


  val selectParams =  js.Dynamic.literal(
  delimiter = "|",
  persist = false,
  valueField = "id",
  labelField = "title",
  searchField = "title",
  onType = typeHandler _ ,
  onChange = changeHandler _,
  options =items

  )



  def attachBinders() = {
    this.binders = BindableView.defaultBinders(this)
  }


  override def bindView(el:HTMLElement) = {
    super.bindView(el)

    val sel = jQuery(el).dyn.selectize(selectParams)

    val s = el.dyn.selectize
    val ss = s.asInstanceOf[Selectize]



  }




}