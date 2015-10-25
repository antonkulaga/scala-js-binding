package org.denigma.preview.slides

import org.denigma.binding.binders.{ReactiveBinder, GeneralBinder}
import org.denigma.binding.views.BindableView
import org.denigma.controls.charts.ScatterPlot
import org.scalajs.dom._

class CompBioView(val elem:Element) extends BindableView{
  override lazy val injector = defaultInjector
    .register("chart"){ case (el,params)=>new ChartView(el).withBinder(new GeneralBinder(_,this.binders.collectFirst{case r:ReactiveBinder=>r}))}
}

class ChartView(elem:Element) extends ScatterPlot(elem)
{

}