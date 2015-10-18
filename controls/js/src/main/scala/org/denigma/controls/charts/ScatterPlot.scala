package org.denigma.controls.charts

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.{ItemsSeqView, BindableView}
import org.scalajs.dom.Element
import org.scalajs.dom.ext.Color
import org.scalajs.dom.raw.HTMLElement
import rx.Rx
import rx.ops._
import rx.core.Var

import scala.collection.immutable.Seq

case class PointValue(x:Double,y:Double,radius:Double=1,color:Color = Color.Green, label:String="")

class PointValueView(val elem:Element,point:Var[PointValue]) extends BindableView {

  val x = point.map(p=>p.x)
  val y = point.map(p=>p.y)
  val label = point.map(p=>p.label)
  val hasLabel = point.map(p=>p.label!="")
  val color = point.map(p=>p.color.toString())
  val radius = point.map(p=>p.radius)

}

class ScatterPlot(val elem:Element) extends ItemsSeqView{

  val oxScale = LinearScale(0,1000,100)
  val oyScale = LinearScale(0,1000,100)

  override lazy val injector = defaultInjector
    .register("ox"){case (el,args)=> new AxisView(el,oxScale,"m").withBinder(new GeneralBinder(_))}
    .register("oy"){case (el,args)=> new AxisView(el,oyScale,"m").withBinder(new GeneralBinder(_))}

  override type Item = Var[PointValue]
  override type ItemView = PointValueView

  override def newItem(item: Item): ItemView = constructItemView(item){
    case (el,mp)=> new PointValueView(el,item).withBinder(new GeneralBinder(_))
  }

  override val items: Rx[Seq[Var[PointValue]]] = Var(Seq.empty)
}
