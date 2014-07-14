package org.denigma.binding.frontend

import org.denigma.binding.views.OrdinaryView
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx.{Rx, Var}

import scalatags.Text.Tag

/**
 * View for article with some text
 */
class ArticleView(val elem:HTMLElement, val params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView
{

  override val name = "article"

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)


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
