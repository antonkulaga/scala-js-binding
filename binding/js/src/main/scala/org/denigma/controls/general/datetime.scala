package org.denigma.controls.general

import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.scalajs.dom.raw.HTMLElement
import org.querki.jquery._

import scala.scalajs.js

class DatePairView(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any])  extends BasicDatePairView {


}

abstract class BasicDatePairView extends BindableView{



  override def bindView(el:HTMLElement) {
    this.bind(el)
    //require(this.params.contains("data") && this.params("data").)
    val pair = $(el).find(".date").dyn.datepicker(js.Dynamic.literal(
      format = "yyyy/m/d",
      autoclose = true
    )
    )
    new Datepair(el)
  }



}

class Datepair(el:HTMLElement) extends js.Object {

}