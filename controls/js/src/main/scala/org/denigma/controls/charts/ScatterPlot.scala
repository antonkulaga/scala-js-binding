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

class ScatterPlot(val elem:Element, val scaleX:Var[Scale], val scaleY:Var[Scale]) extends PointSeries
{
  self=>

  lazy val zeroX = Rx{if(scaleX().inverted) scaleX().end else scaleX().start}
  lazy val zeroY = Rx{if(scaleY().inverted) scaleY().end else scaleY().start}

  val strokeWidth = "3 px"
  val strokeYColor = "red"
  val strokeXColor = "blue"

  override lazy val injector = defaultInjector
    .register("ox"){case (el,args)=> new AxisView(el,scaleX,"m")
      .withBinder(new GeneralBinder(_))}
    .register("oy"){case (el,args)=> new AxisView(el,scaleY,"m")
      .withBinder(new GeneralBinder(_))}


  override def newItem(item: Item): ItemView = constructItemView(item){
    case (el,mp)=> new PointValueView(el,item).withBinder(new GeneralBinder(_))
  }

  override val items: Rx[Seq[Var[PointValue]]] = Var(Seq.empty)

  override protected def subscribeUpdates() = {
    this.items.now.foreach(i=>this.addItemView(i,this.newItem(i)))
    updates.onChange("ItemsUpdates")(upd=>{
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
      upd.moved.foreach(onMove)
    })
  }
}
