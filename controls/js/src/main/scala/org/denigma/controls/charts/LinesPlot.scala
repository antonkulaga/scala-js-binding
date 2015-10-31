package org.denigma.controls.charts

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.extensions._
import org.denigma.binding.views.ItemsSeqView
import org.scalajs.dom.Element
import rx._
import rx.core.{Var, Rx}
import rx.ops._
import scala.collection.immutable.Seq

class LinesPlot(val elem:Element,
                  val scaleX:Var[Scale],
                  val scaleY:Var[Scale],
                  val items: rx.Rx[Seq[Rx[Series]]],
                  val chartStyles:Rx[ChartStyles] = Var(ChartStyles.default)
                   ) extends ItemsSeqView with Plot
{
  self=>

  val paddingX = Var(50.0)
  val paddingY = Var(50.0)

  lazy val transform:Rx[Point=>Point]  = Rx{ p =>p.copy(scaleX().coord(p.x),scaleY().coord(p.y)) }

  override type Item = Rx[Series]

  override type ItemView = SeriesView

  override def newItem(item: Item): SeriesView = constructItemView(item){
    case (el,mp)=> new SeriesView(el,item,transform).withBinder(new GeneralBinder(_))
  }

  import rx.ops._

  val fill: rx.Rx[String] = chartStyles.map(_.linesStyles.fill)

  val linesStyles = chartStyles.map(_.linesStyles)

  override lazy val injector = defaultInjector
    .register("ox"){case (el,args)=> new AxisView(el,scaleX,chartStyles.map(_.scaleX))
      .withBinder(new GeneralBinder(_))}
    .register("oy"){case (el,args)=> new AxisView(el,scaleY,chartStyles.map(_.scaleY))
      .withBinder(new GeneralBinder(_))}

  override protected def subscribeUpdates() = {
    this.items.now.foreach(i=>this.addItemView(i,this.newItem(i)))
    updates.onChange("ItemsUpdates")(upd=>{
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
      upd.moved.foreach(onMove)
    })
  }

}