package org.denigma.preview.charts

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.controls.charts._
import org.denigma.controls.charts.ode.{XYSeries, ODESeries}
import org.scalajs.dom.Element
import rx.core.{Rx, Var}
import rx.ops._
import org.denigma.binding.extensions._
import scala.collection.immutable._

class ProteinsTime(val elem: Element) extends LinesPlot {

  val scaleX: rx.Var[Scale] = Var(LinearScale("LacI", 0, 5000, 1000, 500))

  val scaleY: rx.Var[Scale] = Var(LinearScale("TetR", 0, 2000, 500, 500, inverted = true))

  val odes = Var(CompBioODEs())

  val items: Var[Seq[Item]] = Var(Seq.empty)

  override lazy val injector = defaultInjector
    .register("ox"){case (el, args) => new AxisView(el, scaleX, chartStyles.map(_.scaleX))
      .withBinder(new GeneralBinder(_))}
    .register("oy"){case (el, args) => new AxisView(el, scaleY, chartStyles.map(_.scaleY))
      .withBinder(new GeneralBinder(_))}
    .register("legend"){case (el, args) => new LegendView(el, items)
      .withBinder(new GeneralBinder(_))}

  lazy val solve = Var(Events.createMouseEvent)
  solve.handler{
    val coords = odes.now.computeAll(Array(0.0, 0.0, 0.0, 0.0), 2, 3)
    require(coords.length > 3, "odes should include 4 elements")
    val lacI_mRNA = new StaticSeries("LacI mRNA", coords(0).toList).withStrokeColor("pink")
    val tetR_mRNA = new StaticSeries("TetR mRNA", coords(1).toList).withStrokeColor("cyan")
    val lacI = new StaticSeries("LacI", coords(2).toList).withStrokeColor("red")
    val tetR = new StaticSeries("TetR", coords(3).toList).withStrokeColor("blue")
    items() = Seq(Var(lacI_mRNA), Var(tetR_mRNA), Var(lacI), Var(tetR))
  }

  override def newItem(item: Item): SeriesView = constructItemView(item){
    case (el, mp) => new SeriesView(el, item, transform).withBinder(new GeneralBinder(_))
  }

  lazy val lacI_Prod = odes.map(o => o.lacIProduction.k)
  lazy val tetR_Prod = odes.map(o => o.tetRProduction.k)
  lazy val lacI_Delusion = odes.map(o => o.lacIProduction.k)
  lazy val tetR_Delusion = odes.map(o => o.tetRProduction.k)


}

class ProteinsXY(val elem: Element) extends LinesPlot {


  override type ItemView = SeriesView

  val scaleX: rx.Var[Scale] = Var(LinearScale("LacI", 0.0, 2000.0, 500.0, 500.0))

  val scaleY: rx.Var[Scale] = Var(LinearScale("TetR", 0.0, 2000.0, 500.0, 500.0, inverted = true))

  val odes = CompBioODEs(tEnd = 20000.0)

  val xy = Var(new StaticSeries("LacI | TetR", List.empty))

  override val items = Var(Seq(xy))

  lazy val solve = Var(Events.createMouseEvent)
  solve.handler{
    xy() = xy.now.copy(points = odes.computeXY(Array(0.0, 0.0, 0.0, 0.0), 2, 3))
  }


  override lazy val injector = defaultInjector
    .register("ox"){case (el, args) => new AxisView(el, scaleX, chartStyles.map(_.scaleX))
      .withBinder(new GeneralBinder(_))}
    .register("oy"){case (el, args) => new AxisView(el, scaleY, chartStyles.map(_.scaleY))
      .withBinder(new GeneralBinder(_))}
    .register("legend"){case (el, args) => new LegendView(el, items)
      .withBinder(new GeneralBinder(_))}
}
