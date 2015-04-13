package org.denigma.binding
import org.denigma.endpoints.UserAction
import org.openqa.selenium.By.ById
import org.openqa.selenium.chrome.ChromeDriver
import play.api.mvc.Handler
import play.api.test.{FakeApplication, WithServer}
import play.twirl.api.Html

class ShapedCollectionsSpec extends BindingSpec with GeneralRouters
{
self=>

  step {
    browser = new Browser(new ChromeDriver())
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
        titles.size == 2 && desc.size==2
      }
    }.toBecomeTrue()
  }


  step {
    browser.close()
  }

}
