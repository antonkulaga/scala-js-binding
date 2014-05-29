package controllers

import org.denigma.binding.models._
import shared._

import play.api.mvc.{AnyContent, Action, Controller}
import org.scalax.semweb.rdf.{Res, IRI}
import play.api.libs.json.JsValue
import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sparql._
import play.api.libs.json.JsValue
import org.scalajs.spickling.PicklerRegistry
import org.denigma.binding.models._
import models._

import org.scalax.semweb.rdf.vocabulary.WI
import scala.concurrent.Future
import play.api.mvc.{AnyContent, Action}
import play.twirl.api.Html

/**
 *
 */
object MenuController  extends Controller  {
  val dom =  IRI(s"http://domain")

  var items:List[MenuItem] =  List(
    "slides/bind"->"About ScalaJS Binding",

    "slides/into"->"About benefits of ScalaJS"

  ) map{ case (url,title)=> MenuItem(dom / url,title)}




//  def add(dom:IRI,path:String,title:String):Unit = {
//
//    this.items = items + MenuItem(dom / path,title)
//  }

  def remove(res:Res) = {

    this.items = this.items.filterNot(i=>i.id==res)

  }




  def all(): Action[AnyContent] =  UserAction{
    implicit request=>
      RegisterPicklers.registerPicklers()
      //val domain: String = request.domain
      val menu =  Menu(dom / "menu", "Main menu", items)
      val pickle: JsValue = PicklerRegistry.pickle(menu)
      Ok(pickle).as("application/json")
  }


}


trait ModelController extends Controller {

  type ModelType<: Model

  var items:Seq[ModelType]

  def add() = UserAction{implicit  request=>
    RegisterPicklers.registerPicklers()
    ???


  }

  def remove(res:Res) = {

    this.items = this.items.filterNot(i=>i.id==res)



  }


}