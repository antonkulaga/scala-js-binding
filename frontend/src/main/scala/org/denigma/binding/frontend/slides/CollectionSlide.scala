package org.denigma.binding.frontend.slides


import org.scalajs.dom.{MouseEvent, HTMLElement}

import rx._
import scalatags._
import org.denigma.views.core.OrdinaryView

/**
 * Bind slide
 * @param element
 * @param params
 */
class CollectionSlide(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("bind",element) {
  override def tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override def bindView(el: HTMLElement) {
    //jQuery(el).slideUp()
    super.bindView(el)

  }

  val html_code = Var {
    """
      |<nav class="ui large blue inverted menu" data-view="menu" data-param-path="menu/what">
      |    <a data-template="true" class="active item header" data-item-bind-href="uri" data-item-bind="label" data-load-into="main">
      |        <i class="ui-icon-home"></i>
      |    </a>
      |</nav>
    """.stripMargin
  }

  val scala_code = Var {

    """

    """.stripMargin

  }

}
