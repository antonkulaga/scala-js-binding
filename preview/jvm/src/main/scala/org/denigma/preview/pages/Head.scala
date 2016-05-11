package org.denigma.preview.pages

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Directives, Route}
import org.denigma.preview.templates.MyStyles

import scalacss.Defaults._

class Head extends Directives
{

  lazy val webjarsPrefix = "lib"
  lazy val resourcePrefix = "resources"

  def mystyles =    path("styles" / "mystyles.css"){
    complete  {
      HttpResponse(  entity = HttpEntity(MediaTypes.`text/css`.withCharset(HttpCharsets.`UTF-8`),  MyStyles.render   ))   }
  }

  def loadResources = pathPrefix(resourcePrefix ~ Slash) {
    getFromResourceDirectory("")
  }


  def webjars =pathPrefix(webjarsPrefix ~ Slash)  {  getFromResourceDirectory(webjarsPrefix)  }

  def routes: Route = mystyles ~ webjars ~ loadResources
}
