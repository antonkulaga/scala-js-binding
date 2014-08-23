package org.denigma.controls.general

import importedjs.CodeMirror.{EditorConfiguration, CodeMirror, Editor}
import org.denigma.binding.views.OrdinaryView
import org.scalajs.dom
import org.scalajs.dom.{MouseEvent, HTMLElement}
import rx._
import org.scalajs.jquery.jQuery

import scala.scalajs.js
import org.denigma.binding.extensions._

import scalatags.Text.Tag

class DatePairView(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any])  extends BasicDatePairView {

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

}

abstract class BasicDatePairView extends OrdinaryView{



  override def bindView(el:HTMLElement) {

    super.bindView(el)
    //require(this.params.contains("data") && this.params("data").)
    val pair = jQuery(el).find(".date").dyn.datepicker(js.Dynamic.literal(
      format = "m/d/yyyy",
      autoclose = true
    )
    )
    new Datepair(el)
  }



}

class Datepair(el:HTMLElement) extends js.Object {

}