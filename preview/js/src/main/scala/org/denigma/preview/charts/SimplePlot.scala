package org.denigma.preview.charts

import org.denigma.binding.binders.GeneralBinder
import org.denigma.controls.charts._
import org.denigma.controls.charts.ode.{ ODESeries}
import org.scalajs.dom._
import rx.core.{Rx, Var}
import rx.ops._

import scala.collection.immutable.{Seq, _}

class SimplePlot(val elem: Element) extends LinesPlot {

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

  val lineXplus100Series = Rx {
    LineSeries("2*seriesx", scaleX().start, scaleX().end, LineStyles.default.copy(strokeColor = "red"))(x => Point(x, x + 1))
  }
  val lineX2 = Rx {
    StepSeries("x^2", scaleX().start, scaleX().end, 0.5, LineStyles.default.copy(strokeColor = "pink", opacity = 0.5))(x => Point(x, Math.pow(x, 2)))
  }

  val derX2 = Rx {
    new ODESeries("dy = x*2", scaleX().start, scaleX().end, 0.0, 0.01, LineStyles.default.copy(strokeColor = "yellow", opacity = 0.5))(ode)
  }

  val items: Var[Seq[Rx[Series]]] = Var(
    Seq(
      helloSeries, lineXplus100Series, lineX2, derX2
    )
  )

  override lazy val injector = defaultInjector
    .register("ox"){case (el, args) => new AxisView(el, scaleX, chartStyles.map(_.scaleX))
      .withBinder(new GeneralBinder(_))}
    .register("oy"){case (el, args) => new AxisView(el, scaleY, chartStyles.map(_.scaleY))
      .withBinder(new GeneralBinder(_))}
}
