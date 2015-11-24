package org.denigma.preview.charts

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.controls.charts._
import org.denigma.controls.charts.ode.{XYSeries, ODESeries}
import org.scalajs.dom.Element
import org.scalajs.dom.raw.MouseEvent
import rx.core.{Rx, Var}
import rx.ops._
import org.denigma.binding.extensions._
import scala.collection.immutable._

class ProteinsTime(val elem: Element, val odes: Rx[CompBioODEs], val initialConditions: Rx[Array[Double]]) extends LinesPlot {

  val scaleX: rx.Var[Scale] = Var(LinearScale("Time", 0.0, 5000, 1000, 400))

  val scaleY: rx.Var[Scale] = Var(LinearScale("Concentration", 0.0, 2000, 500, 400, inverted = true))

  val coords = odes.now.computeAll(initialConditions.now, 2, 3)

  lazy val lacI_Prod = odes.map(o => o.lacIProduction.production)
  lazy val tetR_Prod = odes.map(o => o.tetRProduction.production)
  lazy val lacI_Delusion = odes.map(o => o.lacIProduction.production)
  lazy val tetR_Delusion = odes.map(o => o.tetRProduction.production)

  val lacI_mRNA = Var(new StaticSeries("LacI mRNA", List.empty).withStrokeColor("pink"))
  val tetR_mRNA = Var(new StaticSeries("TetR mRNA", List.empty).withStrokeColor("cyan"))
  val lacI = Var(new StaticSeries("LacI", List.empty).withStrokeColor("red"))
  val tetR = Var(new StaticSeries("TetR", List.empty).withStrokeColor("blue"))

  lazy val solve = Var(Events.createMouseEvent)

  protected def onSolve() = {
    val coords = odes.now.computeAll(initialConditions.now, 2, 3)
    require(coords.length > 3, "odes should include 4 elements")
    lacI_mRNA() = lacI_mRNA.now.copy(points = coords(0).toList)
    tetR_mRNA() = tetR_mRNA.now.copy(points = coords(1).toList)
    lacI() = lacI.now.copy(points = coords(2).toList)
    tetR() = tetR.now.copy(points = coords(3).toList)
  }

  solve.handler{
    onSolve()
  }

  override def newItemView(item: Item): SeriesView = constructItemView(item){
    case (el, mp) => new SeriesView(el, item, transform).withBinder(new GeneralBinder(_))
  }

  val items: Var[Seq[Item]] = Var(Seq(lacI_mRNA, tetR_mRNA, lacI, tetR))
}

class ProteinsXY(val elem: Element,  val odes: Rx[CompBioODEs], val initialConditions: Rx[Array[Double]]) extends LinesPlot {


  override type ItemView = SeriesView

  val scaleX: rx.Var[Scale] = Var(LinearScale("LacI", 0.0, 2000.0, 500.0, 400.0))

  val scaleY: rx.Var[Scale] = Var(LinearScale("TetR", 0.0, 2000.0, 500.0, 400.0, inverted = true))

  val xy = Var(new StaticSeries("LacI | TetR", List.empty))

  override val items = Var(Seq(xy))

  def onChartClick(event: MouseEvent) = {

  }

  lazy val solve = Var(Events.createMouseEvent)
  solve.handler{
    xy() = xy.now.copy(points = odes.now.computeXY(initial = initialConditions.now, 2, 3))
  }
}

