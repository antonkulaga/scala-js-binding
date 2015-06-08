package org.denigma.binding

import play.api.GlobalSettings
import play.api.mvc.{Handler, _}
import play.api.test.PlaySpecification

import scala.util.{Failure, Success, Try}

/**
 * Basic binding spec that is used to test with webdriver
 */
trait BindingSpec extends PlaySpecification with Controller  {

  implicit var browser:Browser = null

  val routes : PartialFunction[(String,String), Handler]

  lazy val testPort = 3333

  lazy val duration = 5

  def initSettings(routes:PartialFunction[(String,String), Handler]) = {
    new  GlobalSettings{
      override def onRouteRequest(request: RequestHeader): Option[Handler] = {
        if(routes.isDefinedAt((request.method,request.path)))
          Some(routes(request.method->request.path))
        else
          super.onRouteRequest(request)
      }
    }
  }

  lazy val TestGlobal: GlobalSettings = initSettings(routes)

  def safe(something: =>Boolean) =  Try(something) match {
    case Success(res)=> res
    case Failure(th)=>
      println("ERROR: "+th.getMessage)
      false

  }


}
