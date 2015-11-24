package org.denigma.preview

import java.util.Date

import org.denigma.binding.binders.{GeneralBinder, NavigationBinder}
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.{BindableView, ItemsSetView}
import org.denigma.controls.charts.{LinearScale, ScatterPlot}
import org.denigma.controls.code.CodeBinder
import org.denigma.controls.login.{AjaxSession, LoginView}
import org.denigma.preview.charts.{ChartsView, CompBioView}
import org.denigma.preview.slides._
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import org.semantic.SidebarConfig
import rx.core.Var

import scala.collection.immutable.SortedSet
import scala.scalajs.js.annotation.JSExport

/**
 * Just a simple view for the whole app, if interested ( see https://github.com/antonkulaga/scala-js-binding )
 */
@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  lazy val elem: Element = dom.document.body

  val sidebarargs = SidebarConfig.exclusive(false).dimPage(false).closable(false).useLegacy(true)

  val session = new AjaxSession()

  /**
   * Register views
   */
  override lazy val injector = defaultInjector
    .register("experiments"){case (el, args) =>  new Experiments(el).withBinder(new GeneralBinder(_))}
    .register("sidebar"){
      case (el, args) =>
        new SidebarView(el).withBinder(new GeneralBinder(_))
    }
    .register("menu"){
      case (el, args) => new MenuView(el)
        .withBinder(new GeneralBinder(_))
        .withBinder(new NavigationBinder(_))
    }
    .register("login") { case (el, args) =>
      new LoginView(el, session).withBinder(new GeneralBinder(_))
    }
    .register("BindSlide"){ case (el, args)=> new BindSlide(el).withBinder(v => new CodeBinder(v)) }
    .register("CollectionSlide")
      {case (el, args) =>   new CollectionSlide(el).withBinder(view=>new CodeBinder(view))
    }.register("ControlSlide"){ case (el, args) =>
      new ControlSlide(el, args).withBinder(new CodeBinder(_))
    }
    .register("RdfSlide"){case (el, args) => new RdfSlide(el).withBinder(view => new CodeBinder(view))}
    .register("promo"){case (el, args) => new PromoView(el).withBinder(view => new CodeBinder(view))}
    .register("Selection"){case (el, args) =>
      new StatesSelectionView(el, "test").withBinder{case view => new GeneralBinder(view)}
    }
    .register("HelloPlot"){case (el, args)=>
      new ScatterPlot(
        el,
        Var(LinearScale("OX", 0.0, 1000.0, 100.0, 1000.0)),
        Var(LinearScale("OY", 0.0, 1000.0, 100.0, 1000.0, inverted = true))
        ).withBinder{case view => new GeneralBinder(view)}
    }
    .register("StartSlide"){case (el, args) =>
      new StartSlide(el).withBinder{case view => new CodeBinder(view)}
    }
    .register("ChartsView"){case (el, args) =>
      new ChartsView(el).withBinder(view => new CodeBinder(view))
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

  withBinders(me => List(new GeneralBinder(me), new NavigationBinder(me)))

}
