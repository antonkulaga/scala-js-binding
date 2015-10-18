package org.denigma.binding.extensions
import org.scalajs.dom.raw._
import org.scalajs.dom

import scala.scalajs.js

@js.native
object Functions extends js.GlobalScope{

  def encodeURIComponent(text:String):String = js.native
}

trait Functions {

  def saveAs(filename:String, text:String) = {
    val pom = dom.document.createElement("a")
    pom.setAttribute("id","pom")
    pom.setAttribute("href","data:text/plain;charset=utf-8," + Functions.encodeURIComponent(text))
    pom.setAttribute("download", filename)
    pom.dyn.click()
    if(pom.parentNode==dom.document) dom.document.removeChild(pom)
  }
}
