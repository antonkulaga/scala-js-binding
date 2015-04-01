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
import org.specs2.mutable._

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


}
