package org.denigma.preview

import akka.actor.ActorSystem
import akka.http.extensions.pjax.PJax
import akka.http.scaladsl.model.{HttpResponse, _}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import better.files.File
import org.denigma.preview.communication.WebSocketManager
import org.denigma.preview.pages.{Head, Pages}
import org.denigma.preview.templates.{MyStyles, Twirl}
import play.twirl.api.Html

import scalacss.Defaults._

class Router(files: File)(implicit fm: Materializer, system: ActorSystem) extends Directives with TextFilesDirectives
{

  lazy val sourcesPath = "js/src/main/scala/"

  val transport = new WebSocketManager(system, new FileManager(files))

  def loadFiles: Route = pathPrefix("files" ~ Slash) {
    getFromDirectory(files.path.toString)
  }

  def loadSources: Route = (pathPrefix("sources" ~ Slash) | pathPrefix("source" ~ Slash)){
    extractUnmatchedPath { place ⇒
      parameters("from", "to"){
        case (from, to) =>
          extractLog { case log=>
            filePath(sourcesPath,place,log,'/') match {
              case ""   ⇒
                reject()
              case resourceName ⇒
                this.linesFromResource(resourceName, from, to) { case lines =>
                  complete(HttpResponse(entity = HttpEntity(MediaTypes.`text/css`.withCharset(HttpCharsets.`UTF-8`),  lines.reduce(_+"\n"+_)  )  ))
                }
            }
          }
      }
    }
  }


  def routes: Route = new Head().routes ~ new Pages().routes ~ loadFiles  ~ new WebSockets("test", transport.openChannel).routes ~  loadSources
}