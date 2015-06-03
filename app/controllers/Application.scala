package controllers

import java.io.File

import org.denigma.endpoints.UserAction
import org.denigma.semweb.rdf.{IRI, Res}
import org.denigma.semweb.sparql._
import play.api.http.MimeTypes
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.Future
import scala.language.postfixOps
import scalacache._
import scalacache.lrumap.LruMapCache

object Application extends PjaxController {

  def bindingFile(file: String) = Action {    Ok.sendFile(new File(s"binding/$file")) }
  def modelsFile(file: String) = Action {    Ok.sendFile(new File(s"models/$file")) }
  def frontendFile(file: String) = Action {    Ok.sendFile(new File(s"frontend/$file")) }

  implicit val scalaCache = ScalaCache(LruMapCache(1000))



  /**
   * Displays logo
   * @param variant
   * @return
   */
  def logo(variant:String) = UserAction.async{
    implicit request=>

      Future.successful{     Ok("assets/images/scala-js-logo.svg")    }
  }


  def articleTemplate(text:String,page:Res) = {
    Html(s"""
            |<article id="main_article" data-view="ArticleView" class="ui teal piled segment">
            |<h1 id="title" data-bind="title" class="ui large header"> ${page.stringValue} </h1>
                                                                                            |<div id="textfield" contenteditable="true" style="ui horizontal segment" data-html = "text">$text</div>
                                                                                                                                                                                                |</article>
            """.stripMargin)
  }



  def page(uri:String)= UserAction{
    implicit request=>
      val pg = IRI("http://"+request.domain)
      val page: IRI = if(uri.contains(":")) IRI(uri) else pg / uri

      val text = ?("text")
      val title = ?("title")
      //val authors = ?("authors")
      val pageHtml: Html = this.articleTemplate(text.toString(),page)

      this.pj(pageHtml)(request)
  }

  def myStyles() = UserAction{
    implicit request=>
      import scalacss.Defaults._
      val css:String = styles.MyStyles.render
      Ok(css).as(MimeTypes.CSS)
  }
}