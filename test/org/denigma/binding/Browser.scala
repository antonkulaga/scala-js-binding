package org.denigma.binding

import com.markatta.scalenium._
import org.openqa.selenium.WebDriver

object Browser {
  def apply(driver: WebDriver): Browser = new Browser(driver)
}

class Browser(val driver: WebDriver)
  extends HasDriver
  with HasSearchContext
  with PageProperties
  with ScreenShots
  with Navigation
  with Scripts
  with MarkupSearch
  with Await
  with Forms
{

  def searchContext = driver

  /** close the current window, quit of no windows left */
  def close(): Unit =  {
    driver.close()
  }

  /** shut down the selenium browser */
  def quit(): Unit =  {
    driver.quit()
  }

}