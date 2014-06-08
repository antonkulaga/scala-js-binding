package controllers

import play.api.mvc._
import play.api.libs.json.JsValue
import org.scalajs.spickling.playjson._
import org.denigma.binding.models._
import org.denigma.binding.picklers._


trait ItemsController extends PickleController{
  self: Controller=>


  type ModelType //<:Model

  var items:List[ModelType]

  def all(): Action[AnyContent] =  UserAction{
    implicit request=>
      //val domain: String = request.domain
      //val menu =  Menu(dom / "menu", "Main menu", items)
      val pickle: JsValue = rp.pickle(items)
      Ok(pickle).as("application/json")
  }
  def add() = UserAction(this.pickle[ModelType]()){implicit request=>
    val item = request.body
    this.items= items:::item::Nil
    Ok(rp.pickle(true)).as("application/json")
  }




  def delete() = UserAction(this.pickle[ModelType]()){implicit request=>
    val item = request.body
    this.items = this.items filterNot (_ == item)
    Ok(rp.pickle(true)).as("application/json")
  }



}