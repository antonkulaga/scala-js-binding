package org.denigma.binding.frontend.papers

import org.denigma.binding.extensions._
import org.denigma.binding.storages.AjaxModelStorage
import org.denigma.controls.semantic.{AjaxModelCollection, SelectableModelView}
import org.scalajs.dom.{HTMLElement, MouseEvent}
import org.scalajs.jquery._
import org.scalax.semweb.rdf.{Res, IRI}
import rx.{Rx, Var}

import scala.collection.immutable.Map
import scala.scalajs.js
import scalatags.Text.Tag

/**
 * Shows papers reports
 */
class ReportsView(elem:HTMLElement,params:Map[String,Any]) extends AjaxModelCollection("ReportsView",elem,params){
  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def mouseEvents: Predef.Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  val sidebarParams =  js.Dynamic.literal(exclusive = false)

  val accordionParams = js.Dynamic.literal(
    exclusive = false
  )




  override def newItem(item:Item) = {
    val view = super.newItem(item)

    def activateAccordion() =
    {
      jQuery(".ui.accordion",view.viewElement).dyn.accordion(accordionParams)
    }

    org.scalajs.dom.setTimeout(activateAccordion _,1500)
    view
  }


}

class Report(val elem:HTMLElement,val params:Map[String,Any]) extends SelectableModelView{

  require(params.contains("shape"),"there is not shape")


  override val name = "Report"

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def mouseEvents: Predef.Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)


  override def bindView(el:HTMLElement) = this.bind(el)

  //override def path: String = params.get("path").map(v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)).get


}
