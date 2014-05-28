package controllers

import org.scalajs.spickling.playjson._
import org.scalax.semweb.sparql._
import org.scalax.semweb.rdf.IRI
import org.denigma.binding.models._
import models._
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sparql._
import org.scalax.semweb.rdf.IRI
import play.api.libs.json.JsValue
import org.scalajs.spickling.PicklerRegistry
import org.denigma.binding.models._
import models._

import org.scalax.semweb.rdf.vocabulary.WI
import scala.concurrent.Future
import play.api.mvc.{AnyContent, Action}
import play.twirl.api.Html

import play.api.libs.json.JsValue
import org.scalajs.spickling.PicklerRegistry

import play.api.templates.Html
import org.scalax.semweb.rdf.vocabulary.WI
import scala.concurrent.Future
import shared._

object Slides extends PJaxPlatformWith("index") {


  def whatMenu() =  UserAction{
    implicit request=>
      RegisterPicklers.registerPicklers()

      val domain: String =  request.domain
      val dom =  IRI(s"http://$domain")

      val items: List[MenuItem] = List(
        "slides/bind"->"Basic binding example",
        "slides/collection"->"Collection binding",
        "slides/remotes"->"Remove views"
        //"slides/parse"->"Parsing example"
        ) map{ case (url,title)=> MenuItem(dom / url,title)}

      val menu =  Menu(dom / "menu", "Right menu", items)
      val pickle: JsValue = PicklerRegistry.pickle(menu)
      Ok(pickle).as("application/json")
  }
//  def howMenu() =  UserAction{
//    implicit request=>
//      RegisterPicklers.registerPicklers()
//
//      val domain: String =  request.domain
//      val dom =  IRI(s"http://$domain")
//
//      val items: List[MenuItem] = List(
//        "slides/code"->"share code between projects",
//        "slides/code"->"Deal with JS libs",
//        "slides/code"->"Work form JavaScript",
//        "slides/code"->"Deal with DOM",
//        "slides/code"->"Write cross Scala/ScalaJS libs",
//        "slides/thankyou"->"Thank you for your attention!"
//
//
//    ) map{ case (url,title)=> MenuItem(dom / url,title)}
//
//      val menu =  Menu(dom / "menu", "Right menu", items)
//      val pickle: JsValue = PicklerRegistry.pickle(menu)
//      Ok(pickle).as("application/json")
//  }


  def slide(slide:String) = UserAction {implicit request=>
    val res = slide match {
      case "bind"=>views.html.slides.bind("It can bind")(request)
      case "collection"=>views.html.slides.collection("It can bind to collections")(request)
      case "remotes"=>views.html.slides.remote("It can make remote requests")(request)
      case "parse"=>views.html.slides.parse("It can parse")(request)
      case "code"=>views.html.slides.code("The code will tell you")(request)
      case _=>views.html.slides.code("The code will tell you")(request)

    }
    this.pj(res)(request)
  }

}
