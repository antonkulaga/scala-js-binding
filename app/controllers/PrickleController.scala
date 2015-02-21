package controllers
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


  def unpickleWith[T](unpickle:(String=>Try[T]))(implicit failMessage:String = "cannot unpickle string data"): BodyParser[T] = parse.tolerantText.validate[T]{
    case value =>
      unpickle(value)  match {
        case Failure(res)=> Left(BadRequest(failMessage+s"because of ${res.toString}"))
        case Success(data)=> Right(data)

      }
  }

/*  val TRUE = Ok(Pickle.intoString(true)).as("application/json")

  val FALSE = Ok(Pickle.intoString(false)).as("application/json")*/
}