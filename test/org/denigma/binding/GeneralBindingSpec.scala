package org.denigma.binding

import org.denigma.endpoints.UserAction
import org.openqa.selenium.By.ById
import org.openqa.selenium.chrome.ChromeDriver
import play.api.mvc.Handler
import play.api.test.{FakeApplication, WithServer}
import play.twirl.api.Html



class GeneralBindingSpec extends BindingSpec
{
self=>

  step {
    browser = new Browser(new ChromeDriver())
  }

  lazy val routes : PartialFunction[(String,String), Handler] = {

      case ("GET", "/general") => UserAction{implicit request =>
          val html:Html = twirl.html.general(request)
          Ok(twirl.html.test(html)(request))
        }

        case ("GET", "/collection") => UserAction{implicit request =>
          val html:Html = twirl.html.collection(request)
          Ok(twirl.html.test(html)(request))
        }

      case ("POST","/test/explore")=> Endpoint.exploreEndpoint()

      case ("POST","/test/crud")=> Endpoint.modelEndpoint()

      case ("POST","/test/crud")=> Endpoint.shapeEndpoint()

      case ("GET",str) if str.startsWith("/assets/")  =>  controllers.Assets.at(path="/public", str.replace("/assets/",""))

      case ("GET",str) if str.startsWith("/public/")  =>  controllers.Assets.at(path="/public", str.replace("/public/",""))

      case ("GET",str) if str.startsWith("/webjars/")  =>  controllers.WebJarAssets.at(str.replace("/webjars/",""))

    //  GET           /webjars/*file             controllers.WebJarAssets.at(file)


    // case other =>Action{implicit request=> BadRequest(s"test router does not have ${other._2}")}
  }

  "test binding" in new WithServer(app = FakeApplication(withGlobal = Some(TestGlobal)), port = testPort) {


    browser.goTo(s"http://localhost:$testPort/general")
    val div = browser.first(s"#div1").get
    val input = browser.first(s"#input1").get
    val area = browser.first(s"#area1").get

    val input1 =  browser.driver.findElement(new ById("input1"))
    val area1 =  browser.driver.findElement(new ById("area1"))

    val edit = ""
    val basic = "string1works!"
    browser.waitAtMost(duration).secondsFor{
      div.text ==basic  && input.value == basic && area.value == basic
    }.toBecomeTrue
    val additional = "this string is changing"

    input1.sendKeys(additional)
    browser.waitAtMost(duration).secondsFor{
      div.text == basic+additional && input.value == basic+additional && area.value == basic+additional
    }.toBecomeTrue()

    area1.clear()
    area1.sendKeys(basic)

    browser.waitAtMost(duration).secondsFor{
      div.text ==basic  && input.value == basic && area.value == basic
    }.toBecomeTrue

    input1.sendKeys(additional)
    val ns =  basic+additional
    browser.waitAtMost(duration).secondsFor{
      div.text == ns && input.value == ns && area.value == ns
    }.toBecomeTrue

    println("\nbinding collection test\n")
  }

  "binding to collection" in  new WithServer(app = FakeApplication(withGlobal = Some(TestGlobal)), port = testPort)
  {
    import com.markatta.scalenium._
    import JqueryStyle._

    val hasTitle = "http://denigma.org/resource/title"

    browser.goTo(s"http://localhost:$testPort/collection")
    browser.waitAtMost(duration * 3).secondsFor{
      val titles = browser.find("td").filter(p=>p.webElement.getAttribute("property")==hasTitle)
      val hasDesc ="has_description"
      val desc = browser.find("td").filter(p=>p.webElement.getAttribute("property")==hasDesc)
      titles.size==2 && desc.size==2
    }.toBecomeTrue()
  }

  step {
    //browser.close()
  }

}
