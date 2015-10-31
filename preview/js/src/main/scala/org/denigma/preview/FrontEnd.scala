package org.denigma.preview

import java.util.Date

import org.denigma.binding.binders.{GeneralBinder, NavigationBinder}
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.{BindableView, ItemsSetView}
import org.denigma.controls.charts.{LinearScale, ScatterPlot}
import org.denigma.controls.code.CodeBinder
import org.denigma.controls.login.{AjaxSession, LoginView}
import org.denigma.preview.slides._
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import org.semantic.SidebarConfig
import rx.core.Var

import scala.collection.immutable.SortedSet
import scala.scalajs.js.annotation.JSExport

case class Device(name:String = "undefined",port:String)

object Measurement
{
  implicit val ordering = new Ordering[Measurement]{
    override def compare(x: Measurement, y: Measurement): Int = {
      x.date.compareTo(y.date) match {
        case 0 if x!=y=>  -1
        case other=>other
      }
    }
  }

  implicit val varOrdering = new Ordering[Var[Measurement]]{
    override def compare(x: Var[Measurement], y: Var[Measurement]): Int = {
      ordering.compare(x.now,y.now)
    }
  }
}


case class Measurement(sample:Sample = Sample("unknown","unknown"),diode:String = "unknown",value:Double,date:Date = new Date())
case class Sample(name:String,Description:String = "")
import rx.ops._
class MeasurementView(val elem:Element,measurement:Var[Measurement]) extends BindableView
{
  val sample = measurement.map(m=>m.sample.name)
  val datetime = measurement.map(m=>m.date.getTime.toString)
  val diode = measurement.map(m=>m.diode)
  val value = measurement.map(m=>m.value.toString)
}

class Experiments(val elem:Element) extends BindableView with ItemsSetView
{
  override type Item = Var[Measurement]

  override type ItemView = MeasurementView

  override val items: Var[SortedSet[Item]] =
    Var(SortedSet(
      Var(Measurement(Sample("sample1"),"diode1",3004.0)),
      Var(Measurement(Sample("sample2"),"diode2",3030.0)),
      Var(Measurement(Sample("sample3"),"diode3",3020.0)),
      Var(Measurement(Sample("sample4"),"diode4",3010.0))
    ))
  /*println(SortedSet(
    Var(Measurement(Sample("sample1"),"diode1",3004.0)),
    Var(Measurement(Sample("sample2"),"diode2",3030.0)),
    Var(Measurement(Sample("sample3"),"diode3",3020.0)),
    Var(Measurement(Sample("sample4"),"diode4",3010.0))
  ).size)*/
  //Var(SortedSet.empty)

  override def newItem(item: Item): MeasurementView = this.constructItemView(item){
    case (el,mp)=> new ItemView(el,item).withBinder(new GeneralBinder(_))
  }

}


/**
 * Just a simple view for the whole app, if interested ( see https://github.com/antonkulaga/scala-js-binding )
 */
@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  override def name = "main"

  lazy val elem: Element = dom.document.body

  val sidebarargs = SidebarConfig.exclusive(false).dimPage(false).closable(false).useLegacy(true)

  val session = new AjaxSession()

  /**
   * Register views
   */
  override lazy val injector = defaultInjector
    .register("experiments"){case (el,args)=> new Experiments(el).withBinder(new GeneralBinder(_))}
    .register("sidebar"){
      case (el, args) =>
        new SidebarView(el).withBinder(new GeneralBinder(_))
    }
    .register("menu"){
      case (el,args) => new MenuView(el)
        .withBinder(new GeneralBinder(_))
        .withBinder(new NavigationBinder(_))
    }
    .register("login") { case (el, args) =>
      new LoginView(el,session).withBinder(new GeneralBinder(_))
    }
    .register("testmenu"){
      case (el,args) => new MenuView(el)
        .withBinder(new GeneralBinder(_))
        .withBinder(new NavigationBinder(_))
    }
    .register("BindSlide"){  case (el,args)=>  new BindSlide(el).withBinder{ case v => new CodeBinder(v)  }  }
    .register("CollectionSlide")
      {case (el, args) =>   new CollectionSlide(el).withBinder(view=>new CodeBinder(view))
    }.register("ControlSlide"){ case (el,args) =>
      new ControlSlide(el,args).withBinder(new CodeBinder(_))
    }
    .register("lists"){ case (el,args)=>new LongListView(el).withBinder(view=>new CodeBinder(view))}
    .register("RdfSlide"){case (el,args)=>new RdfSlide(el).withBinder(view=>new CodeBinder(view))}
    .register("promo"){case (el,args)=>new PromoView(el).withBinder(view=>new CodeBinder(view))}
    .register("Selection"){case (el,args)=>
      new StatesSelectionView(el,"test").withBinder{case view=>new GeneralBinder(view)}
    }
    .register("HelloPlot"){case (el,args)=>
      new ScatterPlot(
        el,
        Var(LinearScale("OX",0,1000,100,1000)),
        Var(LinearScale("OY",0,1000,100,1000,inverted = true))
        ).withBinder{case view=>new GeneralBinder(view)}
    }
    .register("StartSlide"){case (el,args)=>
      new StartSlide(el).withBinder{case view=>new GeneralBinder(view)}
    }
    .register("CompBioView"){case (el,args)=>
      new CompBioView(el).withBinder{case view=>new GeneralBinder(view)}
    }

  @JSExport
  def main(): Unit = {
    this.bindView()
  }

  @JSExport
  def load(content: String, into: String): Unit = {
    dom.document.getElementById(into).innerHTML = content
  }

  @JSExport
  def moveInto(from: String, into: String): Unit = {
    for {
      ins <- sq.byId(from)
      intoElement <- sq.byId(into)
    } {
      this.loadElementInto(intoElement, ins.innerHTML)
      ins.parentNode.removeChild(ins)
    }
  }

  withBinders(me=>List(new GeneralBinder(me),new NavigationBinder(me)))

}
