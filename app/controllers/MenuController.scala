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

import org.scalax.semweb.rdf.IRI


object TopMenu  extends Controller  with ItemsController{

  implicit def register = RegisterPicklers.registerPicklers

  type ModelType = MenuItem


  val dom =  IRI(s"http://domain")

  var items:List[ModelType] =  List(
    "slides/bind"->"About ScalaJS Binding",

    "slides/into"->"About benefits of ScalaJS"

  ) map{ case (url,title)=> MenuItem(dom / url,title)}


}


trait ItemsController {
  self: Controller=>

  implicit def register:()=>Unit

  type ModelType<:Model

  var items:List[ModelType]

  def all(): Action[AnyContent] =  UserAction{
    implicit request=>
      RegisterPicklers.registerPicklers()
      //val domain: String = request.domain
      //val menu =  Menu(dom / "menu", "Main menu", items)
      val pickle: JsValue = PicklerRegistry.pickle(items)
      Ok(pickle).as("application/json")
  }
  def add() = UserAction(this.pickle[ModelType]()){implicit request=>
    val item = request.body
    this.items= items:::item::Nil
    Ok(PicklerRegistry.pickle(true)).as("application/json")
  }

  def deleteById() = UserAction(this.pickle[Model]()){implicit request=>
    val id = request.body.id
    this.items = this.items.filterNot(i=>i.id==id)
    Ok(PicklerRegistry.pickle(true)).as("application/json")
  }


  def delete() = UserAction(this.pickle[ModelType]()){implicit request=>
    val item = request.body
    this.items = this.items filterNot (_ == item)
    Ok(PicklerRegistry.pickle(true)).as("application/json")
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