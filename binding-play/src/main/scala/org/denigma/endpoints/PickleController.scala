package org.denigma.endpoints

import play.api.mvc.Controller
import play.api.libs.json.{Json, JsValue}

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.denigma.binding.picklers.rp
import org.scalajs.spickling.playjson._


trait PickleController {
  self:Controller=>
  /**
   * Generates body parser for required type
   * @param failMessage
   * @tparam T
   * @return
   */
  def unpickle[T](implicit failMessage:String = "cannot unpickle json data")  = parse.tolerantJson.validate[T]{
    case value: JsValue =>
      rp.unpickle(value)  match {
        case null=>Left(BadRequest(Json.obj("status" ->"KO","message"->failMessage)).as("application/json"))
        case data:T=>Right(data) //TODO fix with typetags
        case _=>Left(BadRequest(Json.obj("status" ->"KO","message"->"some UFO data")).as("application/json"))

      }
  }

  val TRUE = Ok(rp.pickle(true)).as("application/json")

  val FALSE = Ok(rp.pickle(false)).as("application/json")
}