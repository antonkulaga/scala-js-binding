package controllers

import org.denigma.binding.models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import org.scalax.semweb.rdf.{StringLiteral, Res, IRI}
import play.api.libs.json.{Json, JsValue}
import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sparql._
import org.scalajs.spickling.PicklerRegistry
import org.denigma.binding.models._
import controllers.ItemsController
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.rdf.vocabulary.WI


object ModelsController  extends Controller with ItemsController{

  type ModelType =  PropertyModel

  override implicit def register: () => Unit = rp.registerPicklers



  def menu(uri:IRI,title:String) =
    PropertyModel(
      properties =
        Map(
          WI.pl("hasPage")->Set(uri),
          WI.pl("hasTitle")->Set(StringLiteral(title))
        )
    )

  def menu(page:String,title:String)(implicit dom:IRI): PropertyModel = this.menu(dom / page,title)

  implicit val dom = IRI("http://localhost")

  override var items: List[ModelType] = List(
    menu("slides/bind","Basic binding example"),
    menu("slides/collection","Collection binding"),
    menu("slides/remotes","Remove views"),
    menu("slides/rdf","RDF views")
  )


}

trait ItemWithIdController extends ItemsController{
  self:Controller=>
  def deleteById() = UserAction(this.pickle[Model]()){implicit request=>
    val id = request.body.id
    this.items = this.items.filterNot(i=>i.id==id)
    Ok(PicklerRegistry.pickle(true)).as("application/json")
  }
  override type ModelType<:Model
}


trait ItemsController {
  self: Controller=>

  implicit def register:()=>Unit

  type ModelType //<:Model

  var items:List[ModelType]

  def all(): Action[AnyContent] =  UserAction{
    implicit request=>
      rp.registerPicklers()
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