package org.denigma.binding

import org.denigma.endpoints.UserAction
import org.openqa.selenium.By.ById
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import play.api.GlobalSettings
import play.api.mvc.{Handler, _}
import play.api.test.{FakeApplication, PlaySpecification, WithServer}
import play.twirl.api.Html
import com.markatta.scalenium._
import JqueryStyle._
import scala.concurrent.duration._

class TestGlobal(val routes:PartialFunction[(String,String), Handler]) extends GlobalSettings{
  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    if(routes.isDefinedAt((request.method,request.path)))
      Some(routes(request.method->request.path))
    else
      super.onRouteRequest(request)
  }
}
