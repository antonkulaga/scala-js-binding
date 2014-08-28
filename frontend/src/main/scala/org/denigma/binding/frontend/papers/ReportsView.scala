package org.denigma.binding.frontend.papers

import org.denigma.binding.extensions._
import org.denigma.binding.messages.{Sort, Filters}
import org.denigma.storages.AjaxModelStorage
import org.denigma.controls.semantic.{ExplorableCollection, AjaxModelCollection, SelectableModelView}
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
class ReportsView(elem:HTMLElement, params:Map[String,Any]) extends ExplorableCollection("ReportsView",elem:HTMLElement,params:Map[String,Any]){


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


  val filterClick = Var(this.createMouseEvent())

  this.filterClick handler {
    this.loadData(this.explorer.now)
  }

  val clearClick = Var(this.createMouseEvent())
  clearClick handler {
    this.filters() = Map.empty[IRI,Filters.Filter]
    this.searchTerms() = Map.empty[IRI,String]
    this.sorts() = Map.empty[IRI,Sort]
    this.loadData(this.explorer.now)

  }

  val dirtyFilters = Rx( !(this.filters().isEmpty && this.sorts().isEmpty && this.searchTerms().isEmpty) )

}

class Report(val elem:HTMLElement,val params:Map[String,Any]) extends SelectableModelView{

  require(params.contains("shape"),"there is not shape")

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def mouseEvents: Predef.Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

}
