package org.denigma.preview.charts

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.controls.charts._
import org.denigma.controls.charts.ode.{ODESeries, XYSeries}
import org.scalajs.dom.Element
import org.scalajs.dom.raw.MouseEvent
import rx._
import rx.Ctx.Owner.Unsafe.Unsafe
import org.denigma.binding.extensions._
import rx.Rx.Dynamic

import scala.collection.immutable._

/**
  * Example charts with protein
  * Element
  * @param elem
  * @param odes
  * @param initialConditions initial conditions of ODE simulation
  */
class ProteinsTime(val elem: Element, val odes: Rx[CompBioODEs], val initialConditions: Rx[Array[Double]]) extends LinesPlot {
  self=>

  val scaleX: rx.Var[Scale] = Var(LinearScale("Time", 0.0, 5000, 1000, 400))

  val scaleY: rx.Var[Scale] = Var(LinearScale("Concentration", 0.0, 2000, 500, 400, inverted = true))

  val coords: Array[Array[Point]] = odes.now.computeAll(initialConditions.now, 2, 3)

  lazy val lacI_Prod: Rx[Double] = odes.map(o => o.lacIProduction.production)
  lazy val tetR_Prod: Rx[Double] = odes.map(o => o.tetRProduction.production)
  lazy val lacI_Delusion: Rx[Double] = odes.map(o => o.lacIProduction.production)
  lazy val tetR_Delusion: Rx[Double] = odes.map(o => o.tetRProduction.production)

  val lacI_mRNA = Var(StaticSeries("LacI mRNA", List.empty).withStrokeColor("pink"))
  val tetR_mRNA = Var(StaticSeries("TetR mRNA", List.empty).withStrokeColor("cyan"))
  val lacI = Var(StaticSeries("LacI", List.empty).withStrokeColor("red"))
  val tetR = Var(StaticSeries("TetR", List.empty).withStrokeColor("blue"))

  lazy val solve = Var(Events.createMouseEvent)

  protected def onSolve() = {
    val coords = odes.now.computeAll(initialConditions.now, 2, 3)
    require(coords.length > 3, "odes should include 4 elements")
    lacI_mRNA() = lacI_mRNA.now.copy(points = coords(0).toList)
    tetR_mRNA() = tetR_mRNA.now.copy(points = coords(1).toList)
    lacI() = lacI.now.copy(points = coords(2).toList)
    tetR() = tetR.now.copy(points = coords(3).toList)
  }

  solve.triggerLater{
    onSolve()
  }


  /*
  override def newItemView(item: Item): SeriesView = constructItemView(item){
    case (el, mp) => new SeriesView(el, item, transform).withBinder(new GeneralBinder(_))
  }
  */
  override def newItemView(key: String, value: Series): ItemView = this.constructItemView(key){
    case (el, _) => new ItemView(el, Var(value), self.transform).withBinder(v=>new GeneralBinder(v))
  }

  //val items: Var[Seq[Item]] = Var(Seq(lacI_mRNA, tetR_mRNA, lacI, tetR))

  lazy val items: Rx[SortedMap[Key, Value]] = Rx{
    val seq = Seq(lacI_mRNA, tetR_mRNA, lacI, tetR).map{ v => (v.now.title, v()) }
    SortedMap(seq:_*)
  }
}

class ProteinsXY(val elem: Element, val odes: Rx[CompBioODEs], val conditionSource: InitialConditions) extends LinesPlot {
  self=>

  override type ItemView = SeriesView

  lazy val initialConditions: Rx[Array[Double]] = conditionSource.initialConditions

  val scaleX: rx.Var[Scale] = Var(LinearScale("LacI", 0.0, 2000.0, 500.0, 400.0))

  val scaleY: rx.Var[Scale] = Var(LinearScale("TetR", 0.0, 2000.0, 500.0, 400.0, inverted = true))

  val xy = Var(StaticSeries("LacI | TetR", List.empty))

  //override val items = Var(Seq(xy))

  chartClick.onChange{
    event=> onChartClick(event)
  }


  def onChartClick(event: MouseEvent): Unit = if (event.currentTarget == event.target)
  {
    event.target match {
      case el: Element =>
        //println("HTML =" +elem.outerHTML)
        val rect = el.getBoundingClientRect()
        val x = event.clientX - rect.left - left.now
        val y = event.clientY - rect.top - top.now
        val sx = scaleX.now.chartCoord(x)
        val sy = scaleY.now.chartCoord(y)
        //println(s"chart click works! with [$x ; $y] coords [$sx : $sy]")
        conditionSource.lacI_start() = sx
        conditionSource.tetR_start() = sy
        event.preventDefault()
      case _ =>
    }
  }

  lazy val solve = Var(Events.createMouseEvent)
  solve.triggerLater{
    xy() = xy.now.copy(points = odes.now.computeXY(initial = initialConditions.now, 2, 3))
  }

  lazy val items: Rx[SortedMap[Key, Value]] = xy.map{ ss =>
    SortedMap(("xy", ss))
  }
  override def newItemView(key: String, value: Series): ItemView = this.constructItemView(key){
    case (el, _) => new ItemView(el, Var(value), self.transform).withBinder(v=>new GeneralBinder(v))
  }


}

