package org.denigma.controls.general

import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.jquery.jQuery

import scala.scalajs.js

class DatePairView(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any])  extends BasicDatePairView {

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit = BindableView.defaultBinders(this)
}

abstract class BasicDatePairView extends BindableView{



  override def bindView(el:HTMLElement) {
    activateMacro()
    this.bind(el)
    //require(this.params.contains("data") && this.params("data").)
    val pair = jQuery(el).find(".date").dyn.datepicker(js.Dynamic.literal(
      format = "yyyy/m/d",
      autoclose = true
    )
    )
    new Datepair(el)
  }



}

class Datepair(el:HTMLElement) extends js.Object {

}