package org.denigma.controls.charts
import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.extensions._
import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.scalajs.dom.Element
import rx._
//import rx.Ctx.Owner.voodoo
import rx.Ctx.Owner.Unsafe.Unsafe

import scala.collection.immutable.Seq

object LinesPlot {
  def apply(el: Element,
            scaleOX: Var[Scale],
            scaleOY: Var[Scale],
            series: rx.Rx[Seq[Rx[Series]]],
            styles: Rx[ChartStyles] = Var(ChartStyles.default)
  ): LinesPlot = new LinesPlot{
    self=>
    val elem  = el
    val scaleX: Var[Scale] = scaleOX
    val scaleY: Var[Scale] = scaleOY
    val items: rx.Rx[Seq[Rx[Series]]] = series
    override val chartStyles: Rx[ChartStyles] = styles
  }
}

case class Legend(name: String, color: String, unit: String)

trait LinesPlot extends ItemsSeqView with Plot
{
  self=>

  override type Item = Rx[Series]

  override type ItemView = SeriesView

  val paddingX = Var(50.0)

  val paddingY = Var(50.0)

  val chartStyles: Rx[ChartStyles] = Var(ChartStyles.default)

  lazy val transform: Rx[Point => Point]  = Rx{ p => p.copy(scaleX().coord(p.x), scaleY().coord(p.y)) }

  import scala.util.Random

  override def newItemView(item: Item): SeriesView = constructItemView(item){
    case (el, mp) => new SeriesView(el, item, transform).withBinder(new GeneralBinder(_))
  }

  lazy val fill: rx.Rx[String] = chartStyles.map(_.linesStyles.fill)

  lazy val linesStyles = chartStyles.map(_.linesStyles)

  lazy val chartClick = Var(Events.createMouseEvent())

  protected def flexibleScale(scale: LinearScale, max: Double, mult: Double = 1.2): LinearScale = if(max > scale.end) {
      val newEnd = max * mult
      //println(s"maximizing from ${scale.end} to ${newEnd}")
      scale.copy(end = newEnd, stepSize = newEnd / scale.ticks.size)
    } else if(scale.end * 2 > max) {
      //println(s"minimizing from ${scale.end} to $max")
      scale.copy(end = scale.end / 2, stepSize = scale.stepSize / 2)
    } else scale

/*
  override protected def subscribeUpdates() = {
    super.subscribeUpdates()
    this.items.now.foreach(i => this.addItemView(i, this.newItemView(i)))
    updates.onChange(upd=>{
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
      upd.moved.foreach(onMove)
    })
  }
*/
  override lazy val injector = defaultInjector
    .register("ox"){case (el, args) => new AxisView(el, scaleX, chartStyles.map(_.scaleX))
      .withBinder(new GeneralBinder(_))}
    .register("oy"){case (el, args) => new AxisView(el, scaleY, chartStyles.map(_.scaleY))
      .withBinder(new GeneralBinder(_))}
    .register("legend"){case (el, args) => new LegendView(el, items)
      .withBinder(new GeneralBinder(_))}
}

class LegendView(val elem: Element, val items: rx.Rx[Seq[Rx[Series]]]) extends ItemsSeqView with BindableView{

  type Item = Rx[Series]
  type ItemView = LegendItemView

  override def newItemView(item: Rx[Series]): LegendItemView = this.constructItemView(item){ case (el, mp)=>
    new LegendItemView(el, item).withBinder(new GeneralBinder(_))
  }
}

class LegendItemView(val elem: Element, series: Rx[Series]) extends BindableView
{
  val color = series.map(s => s.style.strokeColor)
  val title = series.map(s => s.title)

}