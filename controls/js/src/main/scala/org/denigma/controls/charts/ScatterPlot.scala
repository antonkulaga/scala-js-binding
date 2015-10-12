package org.denigma.controls.charts

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.BindableView
import org.scalajs.dom.raw.HTMLElement
import rx.core.Var

class ScatterPlot(val elem:HTMLElement) extends BindableView{

  val oxScale = LinearScale(0,1000,10)
  val oyScale = LinearScale(0,1000,10)
  println("scatter plot works!")

  override lazy val injector = defaultInjector
    .register("ox"){case (el,args)=> new AxisView(el,oxScale,"m").withBinder(new GeneralBinder(_))}
    .register("oy"){case (el,args)=> new AxisView(el,oyScale,"m").withBinder(new GeneralBinder(_))}

}
