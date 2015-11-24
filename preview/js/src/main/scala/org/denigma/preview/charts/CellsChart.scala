package org.denigma.preview.charts

import org.denigma.binding.binders.{ReactiveBinder, GeneralBinder}
import org.denigma.binding.views.{CollectionView, BindableView, ItemsSeqView}
import org.denigma.controls.charts.Point
import org.denigma.controls.code.CodeBinder
import org.scalajs.dom.Element
import rx.Rx
import rx.core.Var
import rx.ops.{Moved, SequenceUpdate}

import scala.collection.immutable.Seq
import scala.reflect.ClassTag

object Cell {

  def apply(pos: Point, s: Double /*near: List[Cell]*/): Cell = new Cell{
    val position = pos
    val side = s
    //val neighbors = near
  }

}

trait Cell{
  val position: Point
  val side: Double
  //def color:(Int,Int,Int)
}

trait ArrayChart extends CollectionView {

  import org.denigma.binding.extensions._
  import rx.ops._


  //implicit def typeTag:ClassTag[Item]
  //type Item = T
  implicit def tag:ClassTag[Item]

  val cols: Rx[Int]
  val rows: Rx[Int]

  def makeItem(i: Int, j: Int): Item

  val dimensions: Rx[(Int, Int)] = Rx((cols(), rows()))

  val resize = dimensions.zip

  val items: Var[Array[Array[Item]]] = Var(Array())

/*  def neighbours[T](arr: Array[Array[T]], r: Int, c: Int): Array[T] = arr
    .slice(r - 1, r + 1)
    .slice(c - 1, c + 1)
    .flatten
    .filterNot(item => item == arr(r)(c))*/


  protected def makeRow(r: Int, cOld: Int, c: Int): Array[Item] = {
    val dc = c - cOld
    val oldRow = items.now(r)
    dc match
    {
      case 0 => oldRow
      case less if less < 0 =>
        for(j <- c to cOld) removeItemView(oldRow(j))
        oldRow.slice(0, c)
      case more if more > 0 =>
        val arr =  new Array[Item](c)
        for(j <- 0 until cOld) oldRow(j)
        for(j <- cOld until c) {
          val item = makeItem(r, j)
          arr(j) = item
          this.addItemView(item, newItemView(item))
        }
        arr
    }
  }

  protected def onResize(oldValue: (Int, Int), newValue: (Int, Int)): Unit = (oldValue, newValue) match
  {
    case ( (rOld, cOld), (r, c) ) if rOld == r && cOld ==c => //do nothing

    case ( (rOld, cOld), (r, c) ) if rOld == r =>
      val arr = items.now // NOTE: can be dangerious in terms of subscription
      for(i <- 0 until rOld) arr(i) =  makeRow(i, cOld, c)

    case ( (0, 0), (r, c) ) =>
      val arr:Array[Array[Item]] = Array.tabulate[Item](rows.now, cols.now)(makeItem)

      /*val arr:Array[Array[Item]] = new Array(r)//(rows.now, cols.now)(makeItem)
      for(i <- 0 until rows.now){
          arr(i) = Array[Item]()
          for(j <- 0 until cols.now)
            arr(i)(j) = makeItem(i, j)
      }*/
      //val arr: Array[Array[Item]] = Array.tabulate[Item](rows.now, cols.now)(makeItem)
      for( row <- arr; item <- row) this.addItemView(item, newItemView(item))
      items.set(arr)

    case ( (rOld, cOld), (r, c) ) =>
      val old = items.now
      val arr = new Array[Array[Item]](r)
      if(r > rOld) for(i <- 0 until r) arr(i) = makeRow(i, cOld, c)
      else
      {
        for(i <- 0 until rOld) arr(i) = old(i)
        for(i <- rOld until r) arr(i).foreach(removeItemView)
      }
  }

  override def subscribeUpdates(): Unit = {
    items() = Array.tabulate[Item](rows.now, cols.now)(makeItem)
    resize.onChange("onResize", uniqueValue = true, skipInitial = true)(change => onResize(change._1, change._2))
  }
}
/*
class CellsChart(val elem: Element, val rows: Var[Int], val cols: Var[Int], val side: Var[Int]) extends ArrayChart
{
  val width: Rx[Double] = Var(800)
  val height: Rx[Double] =Var(800)

  override type Item = Rx[Cell]
  override type ItemView = CellView


  override def newItemView(item: Item): ItemView = this.constructItemView(item){
    case (el, mp) => new CellView(el, item).withBinder(new GeneralBinder(_))
  }

}*/
class CellsChart(val elem: Element, val rows: Var[Int], val cols: Var[Int], val side: Var[Int]) extends ItemsSeqView
{

  val width: Rx[Double] = Var(800)
  val height: Rx[Double] =Var(800)

  override type Item = Rx[Cell]
  override type ItemView = CellView

  def isEven(v: Int): Boolean = v % 2 == 0
  def isOdd(v: Int): Boolean = v % 2 != 0
  def vertSide(s: Double): Double =  Math.sqrt(Math.pow(s, 2) - Math.pow(s/2, 2))

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
    } yield Var(Cell(Point(xStart + s * 3 *j,  vert * i ),s))
  }

  override def newItemView(item: Item): ItemView = this.constructItemView(item){
    case (el, mp) => new CellView(el, item).withBinder(new GeneralBinder(_))
  }

}




class CellView(val elem: Element, val cell: Rx[Cell]) extends BindableView {

  self =>

  import rx.ops._

  val cellSide = Var(30)

  val cellRows = Var(5)

  val cellCols = Var(4)

  val dots = Rx{ //draws shape
    val c = cell()
    val (x, y, side) = (c.position.x, c.position.y, c.side)
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

  override lazy val injector = defaultInjector
    .register("SimplePlot") {
      case (el, params) =>
        new SimplePlot(el).withBinder(new GeneralBinder(_, self.binders.collectFirst { case r: ReactiveBinder => r }))
    }
    .register("CompBioView"){case (el, args) =>
      new CompBioView(el).withBinder(view => new CodeBinder(view))
    }
    .register("cells") { case (el, params) =>
      new CellsChart(el, cellRows, cellCols, cellSide).withBinder(new CodeBinder(_))
    }
}