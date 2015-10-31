package org.denigma.preview

import akka.event.LoggingAdapter
import akka.http.extensions.pjax.PJax
import akka.http.scaladsl.model.{HttpResponse, _}
import akka.http.scaladsl.server.{Directives, Route}
import org.denigma.preview.templates.{MyStyles, Twirl}
import play.twirl.api.Html

import scalacss.Defaults._


/**
 * Trait that countains routes and handlers
 */
trait Routes extends Directives with PJax with TextFilesDirectives
{

  def log: LoggingAdapter

  lazy val webjarsPrefix = "lib"

  lazy val resourcePrefix = "resources"

  lazy val sourcesPath = "js/src/main/scala/"

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

      case "collection"=> page(binding.html.collection("It can bind to collections"))(ctx)
      case "controls"=> page(binding.html.controls("There are many controls you can try"))(ctx)
      case "start"=> page(html.start())(ctx)
      case "compbio" | "charts" => page(experimental.html.compbio())(ctx)
      case "bind"=> page(binding.html.bind("Simple binding example"))(ctx)
      case "code"=> page(binding.html.code("The code will tell you"))(ctx)
      case "rdf"=> page(semantic.html.rdf("It can bind views to rdf models"))(ctx)

      /*      case "test"=>page(pages.html.test("Test page"))(ctx)*/

      /*case "bind"=> pages.html.random()
        views.html.pages.bind("It can bind")(request)
      case "collection"=>views.html.pages.collection("It can bind to collections")(request)
      case "editing"=>views.html.pages.editing("It provides some views for better text editing")
      case "remotes"=>views.html.pages.rdf("It can bind rdf shapes")(request)
      case "parse"=>views.html.pages.parse("It can parse")(request)
      case "code"=>views.html.pages.code("The code will tell you")(request)
      case "scalajs"=>views.html.pages.scalajs("Benefits of scalajs")(request)
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

  def loadSources = (pathPrefix("sources"~Slash) | pathPrefix("source"~Slash)){
    extractUnmatchedPath { place ⇒
      parameters("from","to"){
        case (from,to)=>
          extractLog { case log=>
            filePath(sourcesPath,place,log,'/') match {
              case ""   ⇒
                reject()
              case resourceName ⇒
                this.linesFromResource(resourceName,from,to) { case lines=>
                  complete(HttpResponse(entity = HttpEntity(MediaTypes.`text/css`,  lines.reduce(_+"\n"+_)  )  ))
                }
            }
          }
      }
    }
  }
  def webjars =pathPrefix(webjarsPrefix ~ Slash)  {  getFromResourceDirectory(webjarsPrefix)  }

  def routes = index ~  webjars ~ mystyles ~ menu ~ new WebSockets(SuggesterProvider.openChannel).routes ~ loadResources ~ loadSources
}