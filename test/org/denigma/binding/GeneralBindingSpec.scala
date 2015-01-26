package org.denigma.binding


import org.openqa.selenium.firefox.FirefoxDriver
import org.specs2.mutable.Specification
import com.markatta.scalenium._
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.specs2.specification.AfterExample



class GeneralBindingSpec extends Specification {

  var httpServer: Option[HttpTestServer] = None

  step {
    httpServer = Some(new HttpTestServer(8080, "/test", someSimpleHtml))
  }

  import Specs2Integration.specs2FailureHandler

  "markup search" should {

    "find elements from css selector" in {
      withBrowser { browser =>
        browser.find("h1") should haveSize(1)
        browser.find("ul") should haveSize(1)
        browser.find("li") should haveSize(2)
        browser.find("li") should haveSize(2)
        browser.all("li") should haveSize(2)
        browser.select("li") should haveSize(2)
        browser.first("li") should beSome
      }
    }

    "handle missing elements gracefully" in {
      withBrowser { browser =>
        browser.find("a") should beEmpty
        browser.first("a") should beNone
      }
    }
  }

  def withBrowser[T](testBlock: Browser => T): T = {
    val browser = new Browser(new FirefoxDriver())
    browser.goTo("http://localhost:8080/test")
    testBlock(browser)
  }

  step {
    httpServer.foreach(_.stop())
  }

  def someSimpleHtml =
    """
      |<html><head><title>the page</title></head>
      |<body>
      |  <h1>The header</h1>
      |  <ul>
      |   <li id="firstLi">first</li>
      |   <li>second</li>
      |  </ul>
      |</body>
    """.stripMargin
}