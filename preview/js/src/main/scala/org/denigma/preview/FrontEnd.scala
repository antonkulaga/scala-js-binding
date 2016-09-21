package org.denigma.preview

import org.denigma.binding.binders.{GeneralBinder, NavigationBinder}
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.denigma.controls.charts.{LinearScale, ScatterPlot}
import org.denigma.controls.code.CodeBinder
import org.denigma.controls.login.AjaxSession
import org.denigma.preview.charts.ChartsView
import org.denigma.preview.slides._
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import rx._
import rx.Ctx.Owner.Unsafe.Unsafe

import scala.scalajs.js.annotation.JSExport

/**
 * Just a simple view for the whole app, if interested ( see https://github.com/antonkulaga/scala-js-binding )
 */
@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  lazy val elem: Element = dom.document.body

  val session = new AjaxSession()

  /**
   * Register views
   */
  override lazy val injector = defaultInjector
    .register("experiments"){
      case (el, args) =>  new Experiments(el).withBinder(new GeneralBinder(_))
    }
    .register("menu"){
      case (el, args) => new MenuView(el)
        .withBinder(new GeneralBinder(_))
        .withBinder(new NavigationBinder(_))
    }
    .register("BindSlide"){ case (el, args)=> new BindSlide(el).withBinder(v => new CodeBinder(v)) }
    .register("CollectionSlide")
      {case (el, args) =>   new CollectionSlide(el).withBinder(view=>new CodeBinder(view))
    }.register("ControlSlide"){ case (el, args) =>
      new ControlSlide(el).withBinder(new CodeBinder(_))
    }
    .register("Annotations"){ case (el, args) =>
      new AnnotationsView(el, args.get("paper").map(v=>v.toString).getOrElse("toggle_switch/403339a0.pdf")).withBinder(new CodeBinder(_))
    }
    /*
    .register("Publication"){ case (el, args) =>
      new PaperView(el).withBinder(new CodeBinder(_))
    }
    */
    .register("RdfSlide"){case (el, args) => new RdfSlide(el).withBinder(view => new CodeBinder(view))}
    .register("promo"){case (el, args) => new PromoView(el).withBinder(view => new CodeBinder(view))}
    .register("HelloPlot"){case (el, args)=>
      new ScatterPlot(
        el,
        Var(LinearScale("OX", 0.0, 1000.0, 100.0, 1000.0)),
        Var(LinearScale("OY", 0.0, 1000.0, 100.0, 1000.0, inverted = true))
        ).withBinder{ view => new GeneralBinder(view)}
    }
    .register("StartSlide"){case (el, args) =>
      new StartSlide(el).withBinder{ view => new CodeBinder(view)}
    }
    .register("ChartsView"){case (el, args) =>
      new ChartsView(el).withBinder(view => new CodeBinder(view))
    }
    .register("PropertiesBinderSlide"){ case (el, args) =>
        new PropertiesBinderSlide(el).withBinder(view => new CodeBinder(view))
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
      intoElement <- sq.byId(into)}{
      this.loadElementInto(intoElement, ins.innerHTML)
      ins.parentNode.removeChild(ins)
    }
  }

  withBinders(me => List(new GeneralBinder(me), new NavigationBinder(me)))

}
