package org.denigma.binding

import org.denigma.endpoints.UserAction
import play.api.mvc.{Controller, Handler}
import play.twirl.api.Html

trait GeneralRouters  {
  self:Controller=>

  lazy val generalRoutes : PartialFunction[(String,String), Handler] = {

    case ("GET", "/menus") => UserAction{implicit request =>
      val html:Html = twirl.html.general(request)
      Ok(twirl.html.test(html)(request))
    }

    case ("GET", "/collection") => UserAction{implicit request =>
      val html:Html = twirl.html.collection(request)
      Ok(twirl.html.test(html)(request))
    }

    case ("GET",str) if str.startsWith("/assets/")  =>  controllers.Assets.at(path="/public", str.replace("/assets/",""))

    case ("GET",str) if str.startsWith("/public/")  =>  controllers.Assets.at(path="/public", str.replace("/public/",""))

  }


  lazy val endpointRoutes: PartialFunction[(String,String), Handler] = {
    case ("POST","/test/explore")=> Endpoint.exploreEndpoint()

    case ("POST","/test/crud")=> Endpoint.modelEndpoint()

    case ("POST","/test/shape")=> Endpoint.shapeEndpoint()
  }


  lazy val routes : PartialFunction[(String,String), Handler] = this.generalRoutes.orElse(this.endpointRoutes)
}
