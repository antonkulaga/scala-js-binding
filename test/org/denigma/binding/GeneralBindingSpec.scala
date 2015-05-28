package org.denigma.binding

import org.denigma.endpoints.UserAction
import org.openqa.selenium.By.ById
import org.openqa.selenium.chrome.ChromeDriver
import play.api.mvc.Handler
import play.api.test.{FakeApplication, WithServer}
import play.twirl.api.Html



class GeneralBindingSpec extends BindingSpec with GeneralRouters
{
self=>

  step {
    browser = new Browser(new ChromeDriver())
  }

  "test binding" in new WithServer(app = FakeApplication(withGlobal = Some(TestGlobal)), port = testPort) {


    browser.goTo(s"http://localhost:$testPort/menus")
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
      safe {
        val tds = browser.find("td")
        val titles: Seq[Element] = tds.filter(p => p.webElement.getAttribute("property") == hasTitle)
        val hasDesc = "has_description"
        val desc = browser.find("td").filter(p=>p.webElement.getAttribute("property")==hasDesc)
        println("something works")
        titles.size == 2 //&& desc.size==2
      }
    }.toBecomeTrue()
  }


  step {
    browser.close()
  }

}
