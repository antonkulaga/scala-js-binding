package org.denigma.binding.frontend


import org.denigma.views.OrdinaryView
import org.scalajs.dom.{MouseEvent, HTMLElement}
import rx.{Rx, Var}
import scalatags.HtmlTag
import org.scalajs.dom.HTMLElement
import rx.Var
import scalatags.HtmlTag
import org.scalajs.jquery.jQuery

/**
 * View for article with some text
 */
class SlideView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("slide",element){
  override def tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)

  override def bindView(el:HTMLElement) {
    //jQuery(el).slideUp()
    super.bindView(el)

  }



}
