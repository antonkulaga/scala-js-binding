package org.denigma.preview

import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.querki.jquery._
import org.scalajs.dom.raw.HTMLElement
import rx._
import org.semantic.ui._

/**
 * Just a simple view for the sidebar, if interested ( see https://github.com/antonkulaga/scala-js-binding )
 */
class SidebarView (val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView {


  val title = Var("Scala-JS-Binding")

  val logo = Var("/resources/scala-js-logo.svg")

  override def bindElement(el:HTMLElement) = {
    super.bindElement(el)
    $(".ui.accordion").accordion()
  }



}
