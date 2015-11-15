package org.denigma.preview.charts

import org.denigma.binding.binders.GeneralBinder
import org.denigma.controls.charts._
import org.denigma.controls.charts.ode.{ ODESeries}
import org.scalajs.dom._
import rx.core.{Rx, Var}
import rx.ops._

import scala.collection.immutable.{Seq, _}

class SimplePlot(val elem: Element) extends LinesPlot {

  // here we create a scale for OX
  val scaleX: rx.Var[Scale] = Var(LinearScale("Time", 0.0, 20.0, 1.0, 500.0))

  // here we create a scale for OY
  val scaleY: rx.Var[Scale] = Var(LinearScale("TetR", 0.0, 20.0, 1.0, 500.0, inverted = true))

  val justSomeLines =
    Var(
      new StaticSeries("Points: [1, 1] , [2, 3], [3 ,1], [4, 3]", List(
        Point(1.0, 1.0),
        Point(2.0, 3.0),
        Point(3.0, 1.0),
        Point(4.0, 3.0)),
      LineStyles.default.copy(strokeColor = "blue")
    ))

  val lineXplus1Series = Rx {
    // line chart
    LineSeries("y = x + 1", scaleX().start, scaleX().end, LineStyles.default.copy(strokeColor = "red"))(x => Point(x, x + 1))
  }

  val lineX2 = Rx {
    // square chart
    StepSeries("y = x ^ 2", scaleX().start, scaleX().end, 0.5, LineStyles.default.copy(strokeColor = "pink", opacity = 0.5))(x => Point(x, Math.pow(x, 2)))
  }

  val derX2 = Rx {
    def ode(t: Double, y: Double): Double = 2.0 * t // solution for differential equation is t^2
    new ODESeries("dy/dt = x * 2", scaleX().start, scaleX().end, 0.0, 0.01, LineStyles.default.copy(strokeColor = "yellow", opacity = 0.5))(ode)
  }

  // sequence of series
  val items: Var[Seq[Rx[Series]]] = Var(Seq(justSomeLines, lineXplus1Series, lineX2, derX2))

}
