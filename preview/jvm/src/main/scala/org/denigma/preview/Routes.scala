package org.denigma.preview

import akka.http.extensions.pjax.PJax
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Route, Directives}
import akka.http.scaladsl.server.Directives._
import org.denigma.preview.templates.{Twirl, MyStyles}
import play.twirl.api.Html

import scalacss.Defaults._


/**
 * Trait that countains routes and handlers
 */
trait Routes extends Directives with PJax
{

  lazy val webjarsPrefix = "lib"

  lazy val resourcePrefix = "resources"

  def defaultPage: Option[Html] = None


  def index =  pathSingleSlash{ctx=>
    ctx.complete {
      HttpResponse(  entity = HttpEntity(MediaTypes.`text/html`, html.index(None).body  ))
    }
  }

  lazy val loadPage:Html=>Html = h=>html.index(Some(h))


  def page(html:Html): Route = pjax[Twirl](html,loadPage){h=>c=>
        val resp = HttpResponse(  entity = HttpEntity(MediaTypes.`text/html`, h.body  ))
        c.complete(resp)
      }


  def menu = pathPrefix("pages"~ Slash){ctx=>
    ctx.unmatchedPath.toString() match {

      case "collection"=>    page(pages.html.collection("It can bind to collections"))(ctx)
      case "bind"=> page(pages.html.bind("It can bind"))(ctx)
      case "code"=>page(pages.html.code("The code will tell you"))(ctx)

      /*case "bind"=> pages.html.random()
        views.html.pages.bind("It can bind")(request)
      case "collection"=>views.html.pages.collection("It can bind to collections")(request)
      case "editing"=>views.html.pages.editing("It provides some views for better text editing")
      case "remotes"=>views.html.pages.rdf("It can bind rdf shapes")(request)
      case "parse"=>views.html.pages.parse("It can parse")(request)
      case "code"=>views.html.pages.code("The code will tell you")(request)
      case "scalajs"=>views.html.pages.scalajs("Benefits of scalajs")(request)
      case "rdf"=>views.html.pages.rdf("It can bind views to rdf models")(request)
      case "data"=>  views.html.pages.data("Set data schema")(request)
      case "feed"=>   views.html.papers.reports(request)
      //views.html.pages.data("Provides some controls for working with data")(request)
      case "sparql"=>views.html.pages.sparql("It can do sparql parsing")(request)
      case "globe"=>views.html.pages.globe("It can do sparql parsing")(request)*/
      case other=> ctx.complete("other")
    }
  }

  def mystyles =    path("styles" / "mystyles.css"){
    complete  {
      HttpResponse(  entity = HttpEntity(MediaTypes.`text/css`,  MyStyles.render   ))   }
  }

  def loadResources = pathPrefix(resourcePrefix~Slash) {
    getFromResourceDirectory("")
  }

  def webjars =pathPrefix(webjarsPrefix ~ Slash)  {  getFromResourceDirectory(webjarsPrefix)  }


  def routes = index ~  webjars ~ mystyles ~ menu ~ loadResources
}