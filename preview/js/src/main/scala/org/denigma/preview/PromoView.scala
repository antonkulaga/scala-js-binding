package org.denigma.preview

import org.denigma.binding.views.BindableView
import org.scalajs.dom.raw.HTMLElement
import rx.core.{Rx, Var}

class PromoView(val elem:HTMLElement) extends BindableView{

  val logo = Var("/resources/logo.svg")
  val greeting = Var("Hello,")
  val username = Var("User")
  val say = Rx(greeting()+" "+username())

  val htmlCode = Var("""<h1 class="ui header" data-bind="say"></h1>""")
  val scalaCode = Var(
    """val greeting = Var("Hello,")
      |val username = Var("User")
      |val say = Rx(greeting()+" "+username())
    """.stripMargin)


}