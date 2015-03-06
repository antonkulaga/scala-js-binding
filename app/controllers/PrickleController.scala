package controllers

import org.denigma.binding.messages.Suggestion

import scala.concurrent.Future
import scala.util.Success
import play.api.mvc.{BodyParser, Controller}
import prickle._
import scala.concurrent.ExecutionContext.global
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.util.{Failure, Try}


trait PrickleController {
  self:Controller=>
  /**
   * Generates body parser for required type
   * @param failMessage
   * @tparam T
   * @return
   */


  def unpickleWith[T](unpickle:(String=>Try[T]))(implicit failMessage:String = "cannot unpickle string data"): BodyParser[T] =
    parse.tolerantText.validate[T]
    {
      case value =>
        unpickle(value)  match {
          case Failure(res)=>
            val mes = failMessage+s"because of ${res.toString}"
            play.api.Logger.error(mes)
            Left(BadRequest(mes))
          case Success(data)=> Right(data)

        }
  }
  
  lazy val pTRUE = pack(Pickle.intoString(true))
  lazy val pFALSE  = pack(Pickle.intoString(false))

  /**
   * Packs as result*
   * @param str string to pack into
   * @return
   */
  def pack(str:String) = Ok(str).as("text/plain")

/*  val TRUE = Ok(Pickle.intoString(true)).as("application/json")

  val FALSE = Ok(Pickle.intoString(false)).as("application/json")*/
}