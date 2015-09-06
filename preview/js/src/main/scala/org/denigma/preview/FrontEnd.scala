package org.denigma.preview

import org.denigma.binding.binders.{GeneralBinder, NavigationBinder}
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.denigma.controls.code.CodeBinder
import org.denigma.controls.login.{LoginView, AjaxSession}
import org.denigma.controls.selection.{TypedSuggester, TextSelectionView}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.semantic.SidebarConfig
import rx.core.Var

import scala.collection.immutable.Map
import scala.scalajs.js.annotation.JSExport

/**
 * Just a simple view for the whole app, if interested ( see https://github.com/antonkulaga/scala-js-binding )
 */
@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  override def name = "main"

  override val params: Map[String, Any] = Map.empty

  lazy val elem: HTMLElement = dom.document.body

  val sidebarargs = SidebarConfig.exclusive(false).dimPage(false).closable(false).useLegacy(true)

  val session = new AjaxSession()

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
        .withBinder(new NavigationBinder(_))
    }
    .register("login") { case (el, args) =>
      new LoginView(el,session,args).withBinder(new GeneralBinder(_))
    }
    .register("testmenu"){
      case (el,args) => new MenuView(el,args)
        .withBinder(new GeneralBinder(_))
        .withBinder(new NavigationBinder(_))
    }
    .register("BindSlide"){
      case (el,args)=>new BindSlide(el,args).withBinder(new CodeBinder(_))
    }
      .register("CollectionSlide")
      {case (el, args) =>
        new CollectionSlide(el,args).withBinder(view=>new CodeBinder(view))
    }.register("ControlSlide"){ case (el,args) =>
      new ControlSlide(el,args).withBinder(new CodeBinder(_))
    }
    .register("random"){case (el,args)=> new RandomView(el,args).withBinder(view=>new CodeBinder(view)) }
    .register("lists"){ case (el,args)=>new LongListView(el,args).withBinder(view=>new CodeBinder(view))}
    .register("test-macro"){case (el,args)=>new TestMacroView(el,args).withBinder(view=>new CodeBinder(view))}
    .register("RdfSlide"){case (el,args)=>new RdfSlide(el,args).withBinder(view=>new CodeBinder(view))}
    .register("promo"){case (el,args)=>new PromoView(el,args).withBinder(view=>new CodeBinder(view))}
    .register("Selection"){case (el,args)=>
      new TestPromoSelection(el,args).withBinder{case view=>new GeneralBinder(view)}
    }

  class TestPromoSelection(val elem:HTMLElement, val params:Map[String,Any]) extends TextSelectionView{
    override val suggester = new TypedSuggester(input,Var(TestOptions.options))
    override lazy val items:Var[collection.immutable.SortedSet[Item]] = Var(TestOptions.items.map(Var(_)))
  }

  @JSExport
  def main(): Unit = {
    this.bindView()
  }

  @JSExport
  def showLeftSidebar() = {
/*    import org.denigma.binding.extensions._
    val sidebar = $(".top.sidebar").sidebar(sidebarargs)
    sidebar.asInstanceOf[js.Dynamic]("settings","transition","uncover")
    sidebar.show()
    //.dyn.sidebar("settings","transition","uncover"))*/
    //$(".main.menu").asInstanceOf[js.Dynamic].sticky()
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
