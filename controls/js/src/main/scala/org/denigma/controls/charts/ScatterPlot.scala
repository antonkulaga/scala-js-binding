package org.denigma.controls.charts

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.extensions._
import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.scalajs.dom.Element
import org.scalajs.dom.ext.Color
import rx.Rx
import rx.core.Var
import rx.ops._

import scala.collection.immutable.Seq

class ScatterPlot(val elem:Element,
                  val scaleX:Var[Scale],
                  val scaleY:Var[Scale],
                  val chartStyles:Rx[ChartStyles]= Var(ChartStyles.default)
                   ) extends PointPlot
{
  self=>

  lazy val paddingX = Var(50.0)
  lazy val paddingY = Var(50.0)

  override lazy val injector = defaultInjector
    .register("ox"){case (el,args)=> new AxisView(el,scaleX,chartStyles.map(_.scaleX))
      .withBinder(new GeneralBinder(_))}
    .register("oy"){case (el,args)=> new AxisView(el,scaleY,chartStyles.map(_.scaleY))
      .withBinder(new GeneralBinder(_))}


  override def newItemView(item: Item): ItemView = constructItemView(item){
    case (el,mp)=> new PointValueView(el,item ,chartStyles.map(_.linesStyles)).withBinder(new GeneralBinder(_))
  }

  override val items: Rx[Seq[Var[PointValue]]] = Var(Seq.empty)

  override protected def subscribeUpdates() = {
    this.items.now.foreach(i=>this.addItemView(i,this.newItemView(i)))
    updates.onChange("ItemsUpdates")(upd=>{
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
      upd.moved.foreach(onMove)
    })
  }
}
