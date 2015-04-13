package org.denigma.binding

import play.api.mvc.{Controller, Handler}
import play.twirl.api.Html
import org.denigma.endpoints.UserAction
import org.openqa.selenium.By.ById
import org.openqa.selenium.chrome.ChromeDriver
import play.api.test.{FakeApplication, WithServer}
import play.twirl.api.Html

trait GeneralRouters  {
  self:Controller=>


  lazy val routes : PartialFunction[(String,String), Handler] = {

    case ("GET", "/general") => UserAction{implicit request =>
      val html:Html = twirl.html.general(request)
      Ok(twirl.html.test(html)(request))
    }

    case ("GET", "/collection") => UserAction{implicit request =>
      val html:Html = twirl.html.collection(request)
      Ok(twirl.html.test(html)(request))
    }


    case ("GET", "/editor") => UserAction{implicit request =>
      //val html:Html = twirl.html.collection(request)
      val html = twirl.html.editor("str")(request)
      Ok(twirl.html.test(html)(request))
    }

    case ("POST","/test/explore")=> Endpoint.exploreEndpoint()

    case ("POST","/test/crud")=> Endpoint.modelEndpoint()

    case ("POST","/test/shape")=> Endpoint.shapeEndpoint()

    case ("GET",str) if str.startsWith("/assets/")  =>  controllers.Assets.at(path="/public", str.replace("/assets/",""))

    case ("GET",str) if str.startsWith("/public/")  =>  controllers.Assets.at(path="/public", str.replace("/public/",""))

    case ("GET",str) if str.startsWith("/webjars/")  =>  controllers.WebJarAssets.at(str.replace("/webjars/",""))

    //  GET           /webjars/*file             controllers.WebJarAssets.at(file)


    // case other =>Action{implicit request=> BadRequest(s"test router does not have ${other._2}")}
  }
}
