package org.denigma.binding.frontend

import org.denigma.views.OrdinaryView
import org.scalajs.dom.{MouseEvent, HTMLElement}
import rx.{Rx, Var}
import scalatags.HtmlTag
import org.scalajs.dom.HTMLElement
import rx.Var
import scalatags.HtmlTag

/**
 * View for article with some text
 */
class ArticleView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("article",element){
  override def tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)


  val authors = Var("Anton Kulaga")
  val title = Var("ScalaJS and scalajs-binding presentation")



  val text = Var(
    """
      <h3 class="ui blue message">Scala js allows to make dynamic property binding and share code between backend and frontend</h3>
      <a href="http://http://www.scala-js-fiddle.com/">YOU CAN TRY IT RIGHT NOW</a>
   """.stripMargin)

  val published = Var("17/05/2014")
  val lastEdited = Var("17/05/2014")



}
