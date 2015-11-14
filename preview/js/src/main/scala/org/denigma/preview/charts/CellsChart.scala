package org.denigma.preview.charts

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.denigma.controls.charts.Point
import org.scalajs.dom.Element
import rx.Rx
import rx.core.Var

import scala.collection.immutable.Seq

case class Cell(position: Point, side: Double)

class CellsChart(val elem: Element, val rows: Var[Int], val cols: Var[Int], val side: Var[Int]) extends BindableView with ItemsSeqView
{
  val width: Rx[Double] = Var(800.0)
  val height: Rx[Double] =Var(800.0)

  override type Item = Rx[Cell]
  override type ItemView = CellView

  def isEven(v: Int): Boolean = v % 2 == 0
  def isOdd(v: Int): Boolean = v % 2 != 0
  def vertSide(s: Double) =  Math.sqrt(Math.pow(s, 2.0) - Math.pow(s / 2.0, 2.0))

  override val items: Rx[Seq[Item]] = Rx{
    val c = cols()
    val r = rows()
    val s = side()
    val vert = this.vertSide(s)
    for{
      i <- 1 to r
      xStart = if(isOdd(i)) 0 else 1.5 * s
      //yStart = if(isOdd(i)) 0 else vert
      j <- 1 to c
    } yield Var(Cell(Point(xStart + s * 3 *j, vert * i), s))
  }

  override def newItem(item: Item): ItemView = this.constructItemView(item){
    case (el, mp) => new CellView(el, item).withBinder(new GeneralBinder(_))
  }

}

class CellView(val elem: Element,val cell: Rx[Cell]) extends BindableView {

  import rx.ops._

  val dots = Rx{ //draws shape
    val Cell(Point(x, y), side) = cell()
    val half = side / 2
    val vert = Math.sqrt(Math.pow(side, 2)-Math.pow(side / 2,2))
    //val hyp = side * Math.sin(Math.PI / 3)
    List(
      Point(x - side, y),
      Point(x - half, y + vert),
      Point(x + half, y + vert),
      Point(x + side, y),
      Point(x + half, y - vert),
      Point(x - half, y - vert)
    )
  }
  val points = dots.map(_.foldLeft(""){ case (acc, Point(x, y)) =>
    acc+s"$x,$y "
  }.trim)
}