package org.denigma.preview.pages

import akka.http.extensions.pjax.PJax
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Directives, Route}
import org.denigma.controls.Twirl
import play.twirl.api.Html

class Pages extends Directives with PJax{

  def defaultPage: Option[Html] = {
    None
  }

  def index =  pathSingleSlash{ ctx =>
    ctx.complete {
      HttpResponse(  entity = HttpEntity(MediaTypes.`text/html`.withCharset(HttpCharsets.`UTF-8`), html.index(None).body  ))
    }
  }

  val loadPage: Html => Html = h => html.index(Some(h))


  def test: Route = pathPrefix("test" ~ Slash) { ctx=>
      pjax[Twirl](Html(s"<h1>${ctx.unmatchedPath}</h1>"),loadPage){h=>c=>
        val resp = HttpResponse(  entity = HttpEntity(MediaTypes.`text/html`.withCharset(HttpCharsets.`UTF-8`), h.body  ))
        c.complete(resp)
      }(ctx)
    }


  def routes: Route = index ~ test ~ menu




  def page(html: Html): Route = pjax[Twirl](html, loadPage){h=>c=>
    val resp = HttpResponse(  entity = HttpEntity(MediaTypes.`text/html`.withCharset(HttpCharsets.`UTF-8`), h.body  ))
    c.complete(resp)
  }

  def menu = pathPrefix("pages" ~ Slash){ ctx =>
    ctx.unmatchedPath.toString() match {
      case "collection"=> page(binding.html.collection("It can bind to collections"))(ctx)
      case "controls" => page(controls.html.uicontrols("There are many controls you can try"))(ctx)
      case "pdf" => page(controls.html.pdf("PDF viewing"))(ctx)
      case "start" => page(html.start())(ctx)
      case "charts" | "plots" => page(plots.html.charts())(ctx)
      case "bind" => page(binding.html.bind("Simple binding example"))(ctx)
      case "properties" => page(binding.html.properties("Property bindings"))(ctx)
      case "rdf" => page(semantic.html.rdf("It can bind views to rdf models"))(ctx)
      case other => ctx.complete(s"page $other not found!")
    }
  }


}