package org.denigma.preview.slides

import org.denigma.binding.views.BindableView
import org.scalajs.dom.Element
import rx.Var

class PropertiesBinderSlide(val elem: Element) extends BindableView{

  val registration = Var(
    """
      |object MyParentView(val elem: Element) extends BindableView
      |{
      | //injector is responsible for child views registration and initialization
      | override lazy val injector = defaultInjector
      |    .register("CodeExampleView"){
      |      case (el, args) => new BindSlide(el).withBinder(new GeneralBinder(_))
      |    }
      | }
    """.stripMargin)

  val visibilityCode = Var("")

  val bindCode = Var("")
  val bindHtml = Var("")




}
