package org.denigma.preview.charts

import org.denigma.binding.binders.{GeneralBinder, ReactiveBinder}
import org.denigma.binding.views.BindableView
import org.denigma.controls.code.CodeBinder
import org.scalajs.dom._
import rx._
import rx.Ctx.Owner.Unsafe.Unsafe

class ChartsView(val elem: Element) extends BindableView with CodeForCharts{

  self=>

  override lazy val injector = defaultInjector
    .register("SimplePlot") {
      case (el, params) =>
        new SimplePlot(el).withBinder(new GeneralBinder(_, self.binders.collectFirst { case r: ReactiveBinder => r }))
    }
    .register("CompBioView"){case (el, args) =>
      new CompBioView(el).withBinder(view => new CodeBinder(view))
    }
    .register("cells") { case (el, params) =>
        new CellsChart(el, side = Var(30), rows = Var(5), cols = Var(4)).withBinder(new CodeBinder(_))
    }
}


trait CodeForCharts {

  val cellsHTML = Var(
    """
      |<div class="ui equal width grid"  data-view="cells">
      |    <row class="row">
      |        <column class="column">
      |            <div class="ui vertical blue menu">
      |                <item class="item">
      |                    <label class="ui attached label">rows</label><input data-bind="rows">
      |                </item>
      |                <item class="item">
      |                    <label class="ui attached label">cols</label><input data-bind="cols">
      |                </item>
      |                <item class="item">
      |                    <label class="ui attached label">side</label><input data-bind="side">
      |                </item>
      |            </div>
      |        </column>
      |    </row>
      |    <row class="row">
      |        <column class="column">
      |            <svg data-bind-height="height" data-bind-width="width">
      |                <polygon data-template="true" class="hex" data-bind-points="points" fill="green" stroke="red" stroke-width="3" fill-opacity="0.5"></polygon>
      |            </svg>
      |        </column>
      |    </row>
      |</div>
    """.stripMargin
  )


  val plotHTML = Var(
    """
      |<div data-view="@{plotView}">
      |  <h1 class="ui orange header">title</h1>
      |  <svg data-bind-height="height" data-bind-width="width">
      |    <text font-size="18pt">@title</text>
      |    <svg class="lines" data-bind-x="left" data-bind-y="top">
      |      <svg data-template="true">
      |        <path
      |        data-bind-d="path"
      |        data-bind-stroke="strokeColor"
      |        data-bind-stroke-opacity="strokeOpacity"
      |        data-bind-stroke-width="strokeWidth"
      |        data-up-bind-fill="fill"
      |        ></path>
      |      </svg>
      |    </svg>
      |    @org.denigma.controls.charts.html.scaleX()
      |    @org.denigma.controls.charts.html.scaleY()
      |  </svg>
      |  <div class="ui button" data-event-click="solve">Solve</div>
      |</div> |
    """.stripMargin)


  val plotCode = Var(
    """
      |class SimplePlot(val elem: Element) extends LinesPlot {
      |
      |  val scaleX: rx.Var[Scale] = Var(LinearScale("Time", 0.0, 20.0, 1.0, 500.0))
      |
      |  val scaleY: rx.Var[Scale] = Var(LinearScale("TetR", 0.0, 20.0, 1.0, 500.0, inverted = true))
      |
      |  val justSomeLines =
      |    Var(
      |      new StaticSeries("Points: [1, 1] , [2, 3], [3 ,1], [4, 3]", List(
      |        Point(1.0, 1.0),
      |        Point(2.0, 3.0),
      |        Point(3.0, 1.0),
      |        Point(4.0, 3.0)),
      |      LineStyles.default.copy(strokeColor = "blue")
      |    ))
      |
      |  val lineXplus1Series = Rx {
      |    // line chart
      |    LineSeries("y = x + 1", scaleX().start, scaleX().end, LineStyles.default.copy(strokeColor = "red"))(x => Point(x, x + 1))
      |  }
      |
      |  val lineX2 = Rx {
      |    // square chart
      |    StepSeries("y = x ^ 2", scaleX().start, scaleX().end, 0.5, LineStyles.default.copy(strokeColor = "pink", opacity = 0.5))(x => Point(x, Math.pow(x, 2)))
      |  }
      |
      |  val derX2 = Rx {
      |    def ode(t: Double, y: Double): Double = 2.0 * t // solution for differential equation is t^2
      |    new ODESeries("dy/dt = x * 2", scaleX().start, scaleX().end, 0.0, 0.01, LineStyles.default.copy(strokeColor = "yellow", opacity = 0.5))(ode)
      |  }
      |
      |  val items: Var[Seq[Rx[Series]]] = Var(
      |    Seq(
      |      justSomeLines, lineXplus1Series, lineX2, derX2 // list of charts
      |    )
      |  )
      |}
    """.stripMargin)

}
