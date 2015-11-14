package org.denigma.preview

import java.io.File

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.Directive
import akka.http.scaladsl.server.directives.ContentTypeResolver
import akka.http.scaladsl.server.directives.FileAndResourceDirectives.ResourceFile

import scala.annotation.tailrec
import scala.io.Source

trait TextFilesDirectives
{

  def linesFromResource(resourceName: String, from: String, to: String) = textResource(resourceName).map{
    case lines=>
      lines.dropWhile(!_.contains(from))
        .takeWhile(!_.contains(to)).toList
  }

  //def linesFromResource(resourceName:String,start:Int,end:Int) =textResource(resourceName).map{case lines=> lines.slice(start, start + end).toList }

  protected def filePath(base: String, path: Uri.Path, log: LoggingAdapter, separator: Char = File.separatorChar): String = {
    import java.lang.StringBuilder
    @tailrec def rec(p: Uri.Path, result: StringBuilder = new StringBuilder(base)): String =
      p match {
        case Uri.Path.Empty       ⇒ result.toString
        case Uri.Path.Slash(tail) ⇒ rec(tail, result.append(separator))
        case Uri.Path.Segment(head, tail) ⇒
          if (head.indexOf('/') >= 0 || head == "..") {
            log.warning("File-system path for base [{}] and Uri.Path [{}] contains suspicious path segment [{}], " +
              "GET access was disallowed", base, path, head)
            ""
          } else rec(tail, result.append(head))
      }
    rec(if (path.startsWithSlash) path.tail else path)
  }


  def textResource(resourceName: String) =resource(resourceName).map{case ResourceFile(url,length,lastModified)=>
    Source.fromURL(url).getLines()
  }

  def resource(resourceName: String,
               classLoader: ClassLoader = classOf[ActorSystem].getClassLoader)
              (implicit resolver: ContentTypeResolver) =  Directive[Tuple1[ResourceFile]]{ inner => ctx =>
      if (!resourceName.endsWith("/"))
          Option(classLoader.getResource(resourceName)) flatMap ResourceFile.apply match {
            case Some(resource) ⇒ inner(Tuple1(resource))(ctx)
           case other=> ctx.reject()
        }
    else
        ctx.reject()
  }
}
