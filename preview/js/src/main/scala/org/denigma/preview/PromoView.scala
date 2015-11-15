package org.denigma.preview

import org.denigma.binding.views.BindableView
import org.scalajs.dom.Element
import rx.core.{Rx, Var}

class PromoView(val elem: Element) extends BindableView{

  val logo = Var("/resources/logo.svg")
  val greeting = Var("Hello,")
  val username = Var("User")
  val title = Rx(greeting() + " " + username())

  val htmlCode = Var("""<h1 class="ui header" data-bind="title"></h1>""")
  val scalaCode = Var(
    """val greeting = Var("Hello,")
      |val username = Var("User")
      |val title = Rx(greeting()+" "+username())""".stripMargin)


}