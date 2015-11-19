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
/*
trait ArrayChart extends CollectionView {
  import org.denigma.binding.extensions._
  import rx.ops._

  val cols: Rx[Int]
  val rows: Rx[Int]

  def makeItem(i: Int, j: Int): Item


  val dimensions: Rx[(Int, Int)] = Rx( (cols(), rows()))
  val resize: Rx[((Int, Int), (Int, Int))] = dimensions.zip

  val items: Var[Array[Array[Item]]] = Var(Array())

  def neighbours[T](arr: Array[Array[T]], r: Int, c: Int) = arr
      .slice(r-1 ,r+1)
      .slice(c-1, c+1)
      .flatten
      .filterNot(item=>item==arr(r)(c))

  @inline protected def updateArrayRow(cOld:Int, c:Int) = {

  }

  protected def onResize(oldValue:(Int,Int), newValue: (Int, Int)) =
  {
    val (rOld, cOld) = oldValue
    val (r, c) = newValue
    val dr = r - rOld
    val dc = c - cOld
    val old = items.now
    if(dr > 0) {
      val arr = new Array[Array[Item]](r)
      for(i <- 0 until rOld) arr(i) = old(i)
      for(i <- rOld until r) {
        arr(i) = Array.tabulate(c)(j => makeItem(r, j))
        arr(i).foreach(item => this.addItemView(item,this.newItemView(item)) )
      }
    } else if(dr < 0)  {
      for(i <- r until rOld ) old(i).foreach(removeItemView)
      items.set(old.slice(0, r))
    }
  }

  override def subscribeUpdates(): Unit = {
    items() = Array.tabulate[Item](rows.now, cols.now)(makeItem)
    resize.onChange("onResize", uniqueValue = true, skipInitial = true)(change=>onResize(change._1,change._2))
  }
}

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

/*
class CellsChart(val elem: Element, val rows: Var[Int], val cols: Var[Int], val side: Var[Int]) extends CollectionView
{
  val width: Rx[Double] = Var(800.0)
  val height: Rx[Double] = Var(800.0)

  override type Item = Rx[Cell]
  override type ItemView = CellView

  def isEven(v: Int): Boolean = v % 2 == 0
  def isOdd(v: Int): Boolean = v % 2 != 0
  def vertSide(s: Double) =  Math.sqrt(Math.pow(s, 2.0) - Math.pow(s / 2.0, 2.0))

  //def neighbours(cell: Cell)

  override val items: Rx[Seq[Item]] = Rx{
    val matrix: Array[Array[Cell]] = generate(cols(), rows(), side())
    matrix.flatten.map(Var(_)).toList
  }

  def generate(rs: Int, cs: Int, s: Double) = {
    val s = side()
    val vert = this.vertSide(s)
    Array.tabulate(rs, cs){
      case (i, j) =>
        val xStart = if(isOdd(i)) 0 else 1.5 * s
        val pos = Point(xStart + s * 3 *j, vert * i)
        Cell( pos, s)
    }
  }

  def surrounding()

  override def newItemView(item: Item): ItemView = this.constructItemView(item){
    case (el, mp) => new CellView(el, item).withBinder(new GeneralBinder(_))
  }

/*
  val items: Rx[Seq[Item]]

  lazy val updates: Rx[SequenceUpdate[Item]] = items.updates

  protected def onMove(mv: Moved[Item]) = {
    val fr = itemViews(items.now(mv.from))
    val t = itemViews(items.now(mv.to))
    this.replace(t.viewElement, fr.viewElement)
  }

  override protected def subscribeUpdates() = {
    template.hide()
    this.items.now.foreach(i => this.addItemView(i, this.newItem(i)))
    updates.onChange("ItemsUpdates")(upd => {
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
      upd.moved.foreach(onMove)
    })
  }*/

}*/

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