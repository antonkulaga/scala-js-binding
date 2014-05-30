package controllers

import org.denigma.binding.models._
import shared._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import org.scalax.semweb.rdf.{Res, IRI}
import play.api.libs.json.{Json, JsValue}
import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sparql._
import org.scalajs.spickling.PicklerRegistry
import org.denigma.binding.models._
import models._

import org.scalax.semweb.rdf.vocabulary.WI
import scala.concurrent.Future
import play.twirl.api.Html
import play.api.Play
import org.scalax.semweb.rdf.IRI
import scala.Some
import play.api.http.HttpVerbs
import play.api.libs.iteratee.Done
import org.scalax.semweb.rdf.IRI
import scala.Some
import play.api.libs.iteratee.Input.Empty
import java.util.Locale
import org.scalax.semweb.rdf.IRI
import scala.Some
import scala.util.matching.Regex
import org.scalax.semweb.rdf.IRI
import scala.Some
import org.scalax.semweb.rdf.IRI
import scala.Some
import play.api.mvc.AnyContentAsMultipartFormData
import play.api.mvc.AnyContentAsJson
import play.api.mvc.AnyContentAsText
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.mvc.AnyContentAsXml
import play.api.mvc.BodyParsers.parse
import java.io.File

/**
 *
 */
object MenuController  extends Controller  {

  implicit def register = RegisterPicklers.registerPicklers

  val dom =  IRI(s"http://domain")
//
  var items:List[MenuItem] =  List(
    "slides/bind"->"About ScalaJS Binding",

    "slides/into"->"About benefits of ScalaJS"

  ) map{ case (url,title)=> MenuItem(dom / url,title)}




//  def remove(res:Res) = UserAction{implicit  request=>
//    this.items = this.items.filterNot(i=>i.id==res)
//    ???
//  }
//
//
//
//

//
//
  def all(): Action[AnyContent] =  UserAction{
    implicit request=>
      RegisterPicklers.registerPicklers()
      //val domain: String = request.domain
      val menu =  Menu(dom / "menu", "Main menu", items)
      val pickle: JsValue = PicklerRegistry.pickle(menu)
      Ok(pickle).as("application/json")
  }






}


trait PickleController extends Controller {

  implicit def register:()=>Unit

  type ModelType<:Model

  var items:List[ModelType]

  def add = UserAction(this.pickle[ModelType]()){implicit request=>
    val item = request.body
    Ok(Json.obj("status" ->"OK","message"->"addition successful")).as("application/json")
  }

  def remove = UserAction(this.pickle[ModelType]()){implicit request=>
    val item = request.body

    Ok(Json.obj("status" ->"OK","message"->"addition successful")).as("application/json")
  }
  /**
   * Generates body parser for required type
   * @param failMessage
   * @param register
   * @tparam T
   * @return
   */
  def pickle[T](failMessage:String = "cannot unpickle json data")(implicit register: ()=>Unit)  = parse.tolerantJson.validate[T]{
    case value: JsValue =>
      register()
      PicklerRegistry.unpickle(value)
      value match {
        case data:T=>Right(data)
        case null=>Left(BadRequest(Json.obj("status" ->"KO","message"->failMessage)).as("application/json"))
        case _=>Left(BadRequest(Json.obj("status" ->"KO","message"->"some UFO data")).as("application/json"))

      }
  }
}