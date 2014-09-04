package org.denigma.binding.frontend

import org.denigma.binding.views.BindableView
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx.{Rx, Var}

import scalatags.Text.Tag

/**
 * View for article with some text
 */
class ArticleView(val elem:HTMLElement, val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView
{

  override val name = "article"

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


  val authors = Var("Anton Kulaga")

  val title = Var("ScalaJS and scalajs-binding presentation")



  val text = Var(
    """
      <h3 class="ui blue message">Scala js allows to make dynamic property binding and share code between backend and frontend</h3>
      <a href="http://http://www.scala-js-fiddle.com/">YOU CAN TRY IT RIGHT NOW</a>
   """.stripMargin)

  val published = Var("17/05/2014")
  val lastEdited = Var("17/05/2014")

  override protected def attachBinders(): Unit = binders =  BindableView.defaultBinders(this)
}
