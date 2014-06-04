package controllers
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sparql._
import org.scalax.semweb.rdf.IRI
import play.api.libs.json.JsValue
import org.scalajs.spickling.PicklerRegistry
import org.denigma.binding.models._

import models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalax.semweb.rdf.vocabulary.WI

import scala.concurrent.Future
import play.api.mvc.{AnyContent, Action}
import play.twirl.api.Html
import scala.collection.immutable.Map.Map3

object Application extends PJaxPlatformWith("index") {


  /**
   * Displays logo
   * @param variant
   * @return
   */
  def logo(variant:String) = UserAction.async{
    implicit request=>

      Future.successful{     Ok("assets/images/scala-js-logo.svg")    }
  }
  def page(uri:String)= UserAction{
    implicit request=>
      val pg = IRI("http://"+request.domain)
      val page: IRI = if(uri.contains(":")) IRI(uri) else pg / uri

      val text = ?("text")
      val title = ?("title")
      //val authors = ?("authors")
      val pageHtml: Html = Html(
        s"""
            |<article id="main_article" data-view="ArticleView" class="ui teal piled segment">
            |<h1 id="title" data-bind="title" class="ui large header"> ${page.stringValue} </h1>
            |<div id="textfield" contenteditable="true" style="ui horizontal segment" data-html = "text">$text</div>
            |</article>
            """.stripMargin)

      this.pj(pageHtml)(request)

  }



}