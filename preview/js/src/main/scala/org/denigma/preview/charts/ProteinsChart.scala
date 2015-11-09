package org.denigma.preview.charts

import org.denigma.binding.binders.GeneralBinder
import org.denigma.controls.charts._
import org.denigma.controls.charts.ode.ODESeries
import org.scalajs.dom.Element
import rx.core.{Rx, Var}
import rx.ops._
import org.denigma.binding.extensions._
import scala.collection.immutable._

class ProteinsChart(val elem: Element) extends LinesPlot {

  val scaleX: rx.Var[Scale] = Var(LinearScale("Time", 0, 20, 1, 500))

  val scaleY: rx.Var[Scale] = Var(LinearScale("TetR", 0, 20, 1, 500, inverted = true))

  val helloSeries =
    Var(new StaticSeries(
      "hello", List(
        Point(1, 1),
        Point(2, 3),
        Point(3, 1),
        Point(4, 3)),
      LineStyles.default.copy(strokeColor = "blue")
    ))

  def ode(t: Double, y: Double): Double = 2.0 * t // solution to differential equation is t^2


  val items: Rx[Seq[Item]] = Var(
    Seq(
      helloSeries
    )
  )

  override lazy val injector = defaultInjector
    .register("ox"){case (el, args) => new AxisView(el, scaleX, chartStyles.map(_.scaleX))
      .withBinder(new GeneralBinder(_))}
    .register("oy"){case (el, args) => new AxisView(el, scaleY, chartStyles.map(_.scaleY))
      .withBinder(new GeneralBinder(_))}
}
