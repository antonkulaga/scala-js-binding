package org.denigma.controls.charts

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.extensions._
import org.denigma.binding.views.ItemsSeqView
import org.scalajs.dom.Element
import rx._
import rx.core.{Var, Rx}
import rx.ops._
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

trait LinesPlot extends ItemsSeqView with Plot
{
  self=>

  override type Item = Rx[Series]

  override type ItemView = SeriesView

  val paddingX = Var(50.0)

  val paddingY = Var(50.0)

  val chartStyles: Rx[ChartStyles] = Var(ChartStyles.default)

  lazy val transform: Rx[Point => Point]  = Rx{ p =>p.copy(scaleX().coord(p.x),scaleY().coord(p.y)) }

  override def newItem(item: Item): SeriesView = constructItemView(item){
    case (el, mp) => new SeriesView(el, item, transform).withBinder(new GeneralBinder(_))
  }

  import rx.ops._

  lazy val fill: rx.Rx[String] = chartStyles.map(_.linesStyles.fill)

  lazy val linesStyles = chartStyles.map(_.linesStyles)

  override lazy val injector = defaultInjector
    .register("ox"){case (el, args) => new AxisView(el, scaleX, chartStyles.map(_.scaleX))
      .withBinder(new GeneralBinder(_))}
    .register("oy"){case (el, args) => new AxisView(el, scaleY, chartStyles.map(_.scaleY))
      .withBinder(new GeneralBinder(_))}

  override protected def subscribeUpdates() = {
    this.items.now.foreach(i => this.addItemView(i, this.newItem(i) ))
    updates.onChange("ItemsUpdates")(upd=>{
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
      upd.moved.foreach(onMove)
    })
  }

}