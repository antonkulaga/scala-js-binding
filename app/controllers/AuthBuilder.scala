package controllers

import play.api.mvc._
import scala.concurrent.Future


import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.USERS
import play.api.libs.concurrent.Execution.Implicits._


trait UserRequestHeader extends RequestHeader{

  def username: Option[IRI]
  def pjax: Option[String]
}

object UserAction extends ActionBuilder[AuthRequest]
{

  override def invokeBlock[A](request: Request[A], block: (AuthRequest[A]) => Future[Result]): Future[Result] =
  {
    val user: Option[IRI] = request.session.get("user").map(name=>if(name.contains(":")) IRI(name) else IRI(USERS.user / name))
    val req = AuthRequest(user,request)
    //TODO: make more safe
    block(req).map { result =>
      request.headers.get("Origin") match {
        case Some(o) => result.withHeaders("Access-Control-Allow-Origin" -> o)
        case None => result
      }
    }
  }
}

/**
 * Auth Action wrapper
 * @param action
 * @tparam A
 */
case class WithUser[A](action: Action[A]) extends Action[A] {



  def apply(request: Request[A]): Future[Result] = {
    val user = request.session.get("user").map(name=>if(name.contains(":")) IRI(name) else IRI(USERS.user / "name"))
    val req = AuthRequest(user,request)
    action(req)
  }

  lazy val parser = action.parser
}

/**
 * Extends request with information about user
 * @param username Username
 * @param request initial request
 * @tparam A
 */
case class AuthRequest[A](username: Option[IRI], request: Request[A]) extends WrappedRequest[A](request) with UserRequestHeader
{
  def isGuest = username.isEmpty
  def isSigned = username.isDefined

  def pjax: Option[String] = request.headers.get("X-PJAX")

}


