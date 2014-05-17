package controllers
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sparql._
import org.scalax.semweb.rdf.IRI
import models.{RegisterPicklers, MenuItem, Menu}
import play.api.libs.json.JsValue
import org.scalajs.spickling.PicklerRegistry

import play.api.templates.Html
import org.scalax.semweb.rdf.vocabulary.WI
import scala.concurrent.Future
import play.api.mvc.{AnyContent, Action}

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




  def topMenu(): Action[AnyContent] =  UserAction{
    implicit request=>
      RegisterPicklers.registerPicklers()

      val domain: String = request.domain
      val dom =  IRI(s"http://$domain")

      val items: List[MenuItem] = List(
        "slides/bind"->"About ScalaJS Binding",

        "slides/into"->"About benefits of ScalaJS"

      ) map{ case (url,title)=> MenuItem(dom / url,title)}

      val menu =  Menu(dom / "menu", "Main menu", items)
      val pickle: JsValue = PicklerRegistry.pickle(menu)
      Ok(pickle).as("application/json")
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