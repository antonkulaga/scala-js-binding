package org.denigma.controls

import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.querki.jquery._
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js

class DatePairView(val elem:HTMLElement)  extends BasicDatePairView

abstract class BasicDatePairView extends BindableView{



  override def bindView() = {
    this.bindElement(viewElement)
    //require(this.params.contains("data") && this.params("data").)
    val pair = $(viewElement).find(".date").dyn.datepicker(js.Dynamic.literal(
      format = "yyyy/m/d",
      autoclose = true
    )
    )
    new Datepair(viewElement)
  }



}

class Datepair(el:HTMLElement) extends js.Object {

}