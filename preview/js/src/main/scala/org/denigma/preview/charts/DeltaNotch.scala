package org.denigma.preview.charts

import org.denigma.binding.binders.GeneralBinder
import org.denigma.controls.charts.Point
import org.denigma.controls.charts.ode.{VectorODESolver, ODEs}
import org.scalajs.dom.Element
import rx._
import rx.core.Var
/*

case class NotchDeltaCell(position: Point, side: Double, ode: DeltaNotchODEs) extends Cell {

}

class NotchDeltaView(elem: Element, cell: Rx[NotchDeltaCell]) extends CellView(elem, cell) {


}

class DeltaNotch(el: Element, rows: Var[Int], cols: Var[Int], side: Var[Int]) extends CellsChart(el, rows, cols, side) {

  override type Item = Rx[NotchDeltaCell]

  override type ItemView = NotchDeltaView

  override def newItemView(item: Item): ItemView = this.constructItemView(item){
    case (e, mp) => new NotchDeltaView(e, item).withBinder(new GeneralBinder(_))
  }

}


case class DeltaNotchODEs(notch: ProductionDelusion,
                          delta: ProductionDelusion,
                          kCis: Double,
                          kTrans: Double,
                          tEnd: Double = 5000,
                          override val step: Double = 1)
                         (neighbours: Array[DeltaNotchODEs]) extends ODEs
{

  override val tStart = 0.0

 def d_Notch(t: Double, p: Array[Double]): Double = {
   val n = p(0)
   notch(n)
 }

  def d_Delta(t: Double, p: Array[Double]): Double = {
    val d = p(1)
    delta(d)
  }

  lazy val derivatives: Array[VectorDerivative] = Array(d_Notch ,d_Delta)
  import VectorODESolver._

  def solve(n: Double, d: Double) = {
    val result = this.compute(Array(n, d))
    result
  }

}
*/
