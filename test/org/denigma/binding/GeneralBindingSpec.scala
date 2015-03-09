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

/*

class GeneralBindingSpec extends PlaySpecification with Controller
{
self=>

  implicit var browser:Browser = null

  step{
    browser = new Browser(new ChromeDriver())
  }

  val routes : PartialFunction[(String,String), Handler] = {

      case ("GET", "/general") => UserAction{implicit request =>
          val html:Html = twirl.html.general(request)
          Ok(twirl.html.test(html)(request))
        }

        case ("GET", "/collection") => UserAction{implicit request =>
          val html:Html = twirl.html.collection(request)
          Ok(twirl.html.test(html)(request))
        }

   // case other =>Action{implicit request=> BadRequest(s"test router does not have ${other._2}")}
  }

  object TestGlobal extends GlobalSettings{
    override def onRouteRequest(request: RequestHeader): Option[Handler] = {
      if(routes.isDefinedAt((request.method,request.path)))
        Some(routes(request.method->request.path))
      else
        super.onRouteRequest(request)
    }
  }

  val testPort = 3333

  "test general binding" in new WithServer(app = FakeApplication(withGlobal = Some(TestGlobal))
    , port = testPort) {

    browser.goTo(s"http://localhost:$testPort/general")
    val div = browser.first(s"#div1")
    val input = browser.first(s"#input1")
    val basic = "string1works!"
    browser.waitAtMost(10).secondsFor{
      div.forall{  case d=>d.text ==basic}      &&
      input.forall{  case i=>i.value == basic}
    }.toBecomeTrue
    val additional = "this string is changing"
    browser.waitAtMost(5).secondsFor{
      browser.driver.findElement(new ById("input1")).sendKeys(additional)
      div.forall{  case d=>d.text ==basic + additional} &&       input.forall{  case i=>i.value == basic+additional}
    }.toBecomeTrue
  }


  step{
    browser.close()
  }
}
*/