package org.denigma.controls.charts

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.extensions._
import org.denigma.binding.views.{BindableView, CollectionSeqView, CollectionSortedMapView}
import org.scalajs.dom.Element
import rx._

import scala.collection.immutable.SortedMap
//import rx.Ctx.Owner.voodoo
import rx.Ctx.Owner.Unsafe.Unsafe

/**
  * View to display legend information
  * @param elem html element to bind to
  * @param items sortedmap of plot data series
  */
class LegendView(val elem: Element, val items: rx.Rx[SortedMap[String, Series]]) extends CollectionSortedMapView with BindableView{

  type Key = String
  type Value = Series
  type ItemView = PlotLegendItemView

  override def updateView(view: PlotLegendItemView, key: String, old: Series, current: Series): Unit = {
    view.series() = current
  }

  override def newItemView(key: String, value: Value): PlotLegendItemView = this.constructItemView(key) {
    case (el, _) => new PlotLegendItemView(el, Var(value)).withBinder(v=> new GeneralBinder(v))
  }
}

class PlotLegendItemView(val elem: Element, val series: Var[Series]) extends BindableView
{
  val color = series.map(s => s.style.strokeColor)
  val title = series.map(s => s.title)
}