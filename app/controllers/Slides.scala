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

object Slides extends PJaxPlatformWith("index") {


  def whatMenu() =  UserAction{
    implicit request=>
      RegisterPicklers.registerPicklers()

      val domain: String =  request.domain
      val dom =  IRI(s"http://$domain")

      val items: List[MenuItem] = List(
        "slides/bind"->"It can bind",
        "slides/roll"->"It can play",
        "slides/do"->"It can do",
        "slides/parse"->"It can parse",
        "slides/share"->"It can share"
      ) map{ case (url,title)=> MenuItem(dom / url,title)}

      val menu =  Menu(dom / "menu", "Right menu", items)
      val pickle: JsValue = PicklerRegistry.pickle(menu)
      Ok(pickle).as("application/json")
  }
  def howMenu() =  UserAction{
    implicit request=>
      RegisterPicklers.registerPicklers()

      val domain: String =  request.domain
      val dom =  IRI(s"http://$domain")

      val items: List[MenuItem] = List(
        "slides/code"->"share code between projects",
        "slides/code"->"Deal with JS libs",
        "slides/code"->"Work form JavaScript",
        "slides/code"->"Deal with DOM",
        "slides/code"->"Write cross Scala/ScalaJS libs",
        "slides/thankyou"->"Thank you for your attention!"


    ) map{ case (url,title)=> MenuItem(dom / url,title)}

      val menu =  Menu(dom / "menu", "Right menu", items)
      val pickle: JsValue = PicklerRegistry.pickle(menu)
      Ok(pickle).as("application/json")
  }


  def slide(slide:String) = UserAction {implicit request=>
    val res = slide match {
      case "bind"=>views.html.slides.bind("It can bind")(request)
      case "roll"=>views.html.slides.roll("It can play")(request)
      case "do"=>views.html.slides.todo("It can do")(request)
      case "parse"=>views.html.slides.parse("It can parse")(request)
      case "share"=>views.html.slides.share("It can share")(request)
      case "code"=>views.html.slides.code("The code will tell you")(request)
      case "thankyou"=>views.html.slides.thankyou("Thank you for your attention!")(request)

      case _=>views.html.slides.code("It can bind")(request)

    }
    this.pj(res)(request)
  }

}
