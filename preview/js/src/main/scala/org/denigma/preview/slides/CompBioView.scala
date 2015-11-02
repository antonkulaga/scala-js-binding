package org.denigma.preview.slides

import org.denigma.binding.binders.{GeneralBinder, ReactiveBinder}
import org.denigma.binding.views.BindableView
import org.denigma.controls.charts._
import org.denigma.preview.charts.{CellsChart, ODESeries, Solver}
import org.scalajs.dom._
import rx.core.{Rx, Var}
import rx.ops._
import scala.collection.immutable.{Seq, _}


class CompBioView(val elem:Element) extends BindableView with Plot
{

  val scaleX: rx.Var[Scale] = Var(LinearScale("LacI",0,20,1,1000))
  val scaleY: rx.Var[Scale] = Var(LinearScale("TetR",0,20,1,1000,inverted = true))
  lazy val paddingX = Var(50.0)
  lazy val paddingY = Var(50.0)

  val chartStyles = Var(ChartStyles.default)

  val helloSeries =
    Var(new StaticSeries(
      "hello", List(
        Point(1,1),
        Point(2,3),
        Point(3,1),
        Point(4,3)),
      LineStyles.default.copy(strokeColor = "blue")
    ))

  def ode(t: Double, y: Double) = 2.0 * t       // solution to differential equation is t^2

  val lineXplus100Series = Rx{ LineSeries("2*x",scaleX().start,scaleX().end,LineStyles.default.copy(strokeColor = "red"))(x=>Point(x,x+1))}
  val lineX2 = Rx { StepSeries("x^2",scaleX().start,scaleX().end,0.5,LineStyles.default.copy(strokeColor = "pink",opacity = 0.5))(x=>Point(x,Math.pow(x,2))) }
  val derX2 = Rx{
    new ODESeries("dy = x*2",scaleX().start,scaleX().end,0.0,0.1,LineStyles.default.copy(strokeColor = "yellow",opacity = 0.5))(ode)
  }

  val series: Var[Seq[Rx[Series]]] = Var(
   Seq(
     helloSeries,lineXplus100Series,lineX2,derX2
     )
  )
  val legend = series.map{
    case ser=> ser.map{ case s=> s.now.title + s.now.style.strokeColor}
  }

  val cellSide = Var(30)
  val cellRows = Var(3)
  val cellCols = Var(5)

  override lazy val injector = defaultInjector
    .register("chart"){
      case (el,params)=>
        new LinesPlot(el,scaleX,scaleY,series).withBinder(new GeneralBinder(_,this.binders.collectFirst{case r:ReactiveBinder=>r}))
          }
    .register("cells"){
      case (el,params)=>
        new CellsChart(el,cellRows,cellCols,cellSide).withBinder(new GeneralBinder(_))
      //new LinesPlot(el,scaleX,scaleY,series).withBinder(new GeneralBinder(_,this.binders.collectFirst{case r:ReactiveBinder=>r}))
    }
}