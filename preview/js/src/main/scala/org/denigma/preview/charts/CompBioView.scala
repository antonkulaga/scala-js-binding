package org.denigma.preview.charts

import org.denigma.binding.binders.{Events, GeneralBinder, ReactiveBinder}
import org.denigma.binding.views.BindableView
import org.denigma.controls.charts.{Point, StaticSeries}
import org.scalajs.dom._
import rx.core.Var


class CompBioView(val elem: Element) extends BindableView  {
  self=>
  val solve: Var[MouseEvent] = Var(Events.createMouseEvent())
  import rx.ops._
  import org.denigma.binding.extensions._

  val cellSide = Var(30)

  val cellRows = Var(5)

  val cellCols = Var(4)

  override lazy val injector = defaultInjector
    .register("SimplePlot") {
      case (el, params) =>
        new SimplePlot(el).withBinder(new GeneralBinder(_, self.binders.collectFirst { case r: ReactiveBinder => r }))
    }
    .register("ProteinsTime") {
      case (el, params) =>
        new ProteinsTime(el).withBinder(new GeneralBinder(_, self.binders.collectFirst { case r: ReactiveBinder => r }))
    }
    .register("ProteinsXY") {
      case (el, params) =>
        new ProteinsXY(el).withBinder(new GeneralBinder(_, self.binders.collectFirst { case r: ReactiveBinder => r }))
    }
    .register("cells") {
      case (el, params) =>
        new CellsChart(el, cellRows, cellCols, cellSide).withBinder(new GeneralBinder(_))
      //new LinesPlot(el,scaleX,scaleY,series).withBinder(new GeneralBinder(_,this.binders.collectFirst{case r:ReactiveBinder=>r}))
    }
}


