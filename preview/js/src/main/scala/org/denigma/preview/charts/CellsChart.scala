package org.denigma.preview.charts

import org.denigma.binding.binders.{GeneralBinder, ReactiveBinder}
import org.denigma.binding.views.{BindableView, CollectionView}
import org.denigma.controls.charts.Point
import org.scalajs.dom.Element
import rx._
import rx.Ctx.Owner.Unsafe.Unsafe

import scala.scalajs.js

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

  val cols: Rx[Int]

  val rows: Rx[Int]

  def makeItem(r: Int, c: Int): Item

  protected def onInsert(item: Item): ItemView

  val dimensions: Rx[(Int, Int)] = Rx((rows(), cols()))

  val resize = dimensions.zip

  val items: Var[js.Array[js.Array[Item]]] = Var(new js.Array[js.Array[Item]](0))

  def neighbours(arr: js.Array[js.Array[Item]], r: Int, c: Int): js.Array[Item] = arr
    .slice(r - 1, r + 1)
    .slice(c - 1, c + 1)
    .flatten
    .filterNot(item => item == arr(r)(c))


  protected def makeRow(old: js.Array[js.Array[Item]], r: Int, cOld: Int, c: Int): js.Array[Item] = if (r >= old.length)
  {
    createRow(r, c)(makeItem)
  }
  else
  {
    val oldRow = old(r)
    val dc = c - cOld
    dc match
    {
      case 0 => oldRow

      case less if less < 0 =>
        for(j <- c to cOld) onRemove(oldRow(j))
        oldRow.slice(0, c)

      case more if more > 0 =>
        val arr =  new js.Array[Item](c)
        for(j <- 0 until cOld) arr(j) = oldRow(j)
        for(j <- cOld until c) {
          val item = makeItem(r, j)
          arr(j) = item
          onInsert(item)
        }
        arr
    }
  }

  protected def createRow(r: Int, c: Int)(fun: (Int, Int) => Item) =
  {
    val row: js.Array[Item] = new js.Array(c)
    for(j <- 0 until c) {
      val item = fun(r,j)
      row(j) = item
      onInsert(item)
    }
    row
  }

  protected def createArray2(r: Int, c: Int)(fun: (Int, Int) => Item) = {
    val arr: js.Array[js.Array[Item]] = new js.Array(r)
    for(i <- 0 until r) arr(i) = createRow(i, c)(fun)
    arr
  }

  protected def onResize(oldValue: (Int, Int), newValue: (Int, Int)): Unit =
  {
    //println(s"resizing started with ${oldValue} and ${newValue}")
    (oldValue, newValue) match
    {

      case ((0, 0), (r, c)) =>
        val arr = createArray2(r, c)(makeItem)
        items.set(arr)

      case ((rOld, cOld), (r, c)) if rOld == r && cOld ==c =>  // do nothing
      //println("nothing happenz")

      case ((rOld, cOld), (r, c)) if rOld == r =>
        //println(s"COLS CHANGED OLD is $cOld and NEW is $c")
        val old = items.now // NOTE: can be dangerious in terms of subscription
        for(i <- 0 until r) old(i) =  makeRow(old, i, cOld, c)
        items.set(old) //nothing changes just something inside mutable old array updates


      case ((rOld, cOld), (r, c)) =>
        val old = items.now
        val arr = new js.Array[js.Array[Item]](r)
        //println(s"ROWS CHANGED OLD is $rOld and NEW is $r")
        if(r > rOld) {
          for(i <- 0 until rOld) arr(i) = old(i)
          for(i <- rOld until r) arr(i) = makeRow(old, i, cOld, c)
        }
        else if (r < rOld)
        {
          for(i <- 0 until r) arr(i) = old(i)
          for{
            i <- r until rOld
            item <- old(i)
          } onRemove(item)
        }
        items.set(arr)
    }
  }


  override def subscribeUpdates(): Unit = {
    template.hide()
    resize.onChange(change => onResize(change._1, change._2))
    val arr = createArray2(rows.now, cols.now)(makeItem)
    items.set(arr)
  }
}

class CellsChart(val elem: Element, val cols: Var[Int], val rows: Var[Int], val side: Var[Int]) extends ArrayChart
{
  val width: Rx[Double] = Var(800)
  val height: Rx[Double] =Var(800)

  override type Item = Rx[Cell]
  override type ItemView = CellView

  def isEven(v: Int): Boolean = v % 2 == 0
  def isOdd(v: Int): Boolean = v % 2 != 0
  def vertSide(s: Double): Double =  Math.sqrt(Math.pow(s, 2) - Math.pow(s/2, 2))


  def newItemView(item: Item): ItemView = this.constructItemView(item){
    case (el, mp) => new CellView(el, item).withBinder(new GeneralBinder(_))
  }

  protected def onInsert(item: Item): ItemView = this.addItemView(item, this.newItemView(item))

  override def makeItem(r: Int, c: Int): Rx[Cell] = {
    val s = side.now
    val vert = this.vertSide(s)
    val xStart = s * ( if (isOdd(r)) 0.5 else 2.0 )
    Var(Cell(Point(xStart + s * 3 * c,  vert * (r +1) ), s))
  }

/*  private def printAll(rows: js.Array[js.Array[Item]]) = {
    println("<ARRAY>\n" +rows.toList.map(i=>i.map(_.now.position).toList.mkString(" | ")).mkString("\n")+"</ARRAY>\n")
  }*/

  import org.denigma.binding.extensions._

  override protected def makeRow(old: js.Array[js.Array[Item]], r: Int, cOld: Int, c: Int): js.Array[Item] = if (r >= old.length)
  {
    createRow(r, c)(makeItem)
  }
  else
  {
    val oldRow = old(r)
    c - cOld match
    {
      case 0 => oldRow

      case less if less < 0 =>
        for(j <- c until cOld) onRemove(oldRow(j))
        oldRow.slice(0, c)

      case more if more > 0 =>
        val arr =  new js.Array[Item](c)
        for(j <- 0 until cOld) arr(j) = oldRow(j)
        for(j <- cOld until c) {
          val item = makeItem(r, j)
          arr(j) = item
          onInsert(item)
        }
        arr
    }
  }

}

class CellView(val elem: Element, val cell: Rx[Cell]) extends BindableView {

  self =>



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
}