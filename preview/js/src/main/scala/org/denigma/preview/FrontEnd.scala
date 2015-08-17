package org.denigma.preview

import org.denigma.binding.binders.{NavigationBinding, GeneralBinder}
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.{OrganizedView, ViewInjector, BindableView}
import org.denigma.controls.binders.CodeBinder
import org.querki.jquery._
import org.scalajs.dom
import org.scalajs.dom.raw.{Selection, HTMLElement}
import org.semantic.SidebarConfig
import org.semantic.ui._

import scala.collection.immutable.Map
import scala.scalajs.js.annotation.JSExport
import scala.util.Try

/**
 * Just a simple view for the whole app, if interested ( see https://github.com/antonkulaga/scala-js-binding )
 */
@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  override def name = "main"

  override val params: Map[String, Any] = Map.empty

  lazy val elem: HTMLElement = dom.document.body

  val sidebarargs = SidebarConfig.exclusive(false).dimPage(false).closable(false).useLegacy(false)



/*  new CodeBinder(this),
  new RDFModelBinder[Plantain](this,
    graph,
    resolver)*/

  /**
   * Register views
   */
  override lazy val injector = defaultInjector
    .register("sidebar"){
      case (el, args) =>
        new SidebarView(el,args).withBinder(new GeneralBinder(_))
    }
    .register("menu"){
      case (el,args) => new MenuView(el,args)
        .withBinder(new GeneralBinder(_))
        .withBinder(new NavigationBinding(_))
    }
    .register("testmenu"){
      case (el,args) => new MenuView(el,args)
        .withBinder(new GeneralBinder(_))
        .withBinder(new NavigationBinding(_))
    }
    .register("BindSlide"){
      case (el,args)=>new BindSlide(el,args).withBinder(new CodeBinder(_))
    }
      .register("CollectionSlide")
      {case (el, args) =>
        new CollectionSlide(el,args).withBinder(view=>new CodeBinder(view))
    }
    .register("random"){case (el,args)=> new RandomView(el,args).withBinder(view=>new CodeBinder(view)) }
    .register("lists"){ case (el,args)=>new LongListView(el,args).withBinder(view=>new CodeBinder(view))}
    .register("test-macro"){case (el,args)=>new TestMacroView(el,args).withBinder(view=>new CodeBinder(view))}
    .register("RdfSlide"){case (el,args)=>new RdfSlide(el,args).withBinder(view=>new CodeBinder(view))}
    .register("Selection"){case (el,args)=>new SelectionView(el,args).withBinder(view=>new CodeBinder(view))}
    //.register("RdfSlide", (el,args)=>Try(new RdfSlide(el,args)))



  @JSExport
  def main(): Unit = {
    this.bindView(this.viewElement)
  }

  @JSExport
  def showLeftSidebar() = {
    $(".left.sidebar").sidebar(sidebarargs).show()
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

  this.binders = List(new GeneralBinder(this),new NavigationBinding(this))

}
