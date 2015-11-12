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

  val odes = CompBioODEs()

  val items: Var[Seq[Item]] = Var(Seq.empty)


  override lazy val injector = defaultInjector
    .register("ox"){case (el, args) => new AxisView(el, scaleX, chartStyles.map(_.scaleX))
      .withBinder(new GeneralBinder(_))}
    .register("oy"){case (el, args) => new AxisView(el, scaleY, chartStyles.map(_.scaleY))
      .withBinder(new GeneralBinder(_))}

  lazy val solve = Var(Events.createMouseEvent)
  solve.handler{
    val coords = odes.computeAll(Array(0.0, 0.0, 0.0, 0.0), 2, 3)
    require(coords.length > 3, "odes should include 4 elements")
    val lacI_mRNA = new StaticSeries("LacI mRNA", coords(0).toList)
    val tetR_mRNA = new StaticSeries("TetR mRNA", coords(1).toList)
    val lacI = new StaticSeries("LacI", coords(2).toList)
    val tetR = new StaticSeries("TetR", coords(3).toList)
    items() = Seq(Var(lacI_mRNA), Var(tetR_mRNA), Var(lacI), Var(tetR))
  }

  override def newItem(item: Item): SeriesView = constructItemView(item){
    case (el, mp) => new SeriesView(el, item, transform).withBinder(new GeneralBinder(_))
  }

}

class ProteinsXY(val elem: Element) extends LinesPlot {


  override type ItemView = SeriesView

  val scaleX: rx.Var[Scale] = Var(LinearScale("LacI", 0, 2000, 500, 500))

  val scaleY: rx.Var[Scale] = Var(LinearScale("TetR", 0, 2000, 500, 500, inverted = true))

  val odes = CompBioODEs(tEnd = 20000)

  override val items = Var(Seq.empty[Item])

  lazy val solve = Var(Events.createMouseEvent)
  solve.handler{
    val points = odes.computeXY( Array(0.0, 0.0, 0.0, 0.0), 2, 3)
    //println("[" + points.mkString(" ") + " ]")
    items() = Seq(Var(new StaticSeries("XY", points)))
  }


  override lazy val injector = defaultInjector
    .register("ox"){case (el, args) => new AxisView(el, scaleX, chartStyles.map(_.scaleX))
      .withBinder(new GeneralBinder(_))}
    .register("oy"){case (el, args) => new AxisView(el, scaleY, chartStyles.map(_.scaleY))
      .withBinder(new GeneralBinder(_))}
}
