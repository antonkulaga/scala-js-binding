package org.denigma.preview

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import java.io.{File => JFile}

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import better.files._
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

import scala.Seq
import scala.collection.immutable._

object Main extends App  {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val server: HttpExt = Http(system)
  val config = system.settings.config

  val (host, port) = (config.getString("app.host"), config.getInt("app.port"))
  val filePath: String = config.as[Option[String]]("app.files").getOrElse("files/")
  val root = File(filePath)
  root.createIfNotExists(asDirectory = true)
  val router = new Router(File(filePath))
  val bindingFuture = server.bindAndHandle(router.routes, host, port)(materializer)
  system.log.info(s"starting server at $host:$port")

}

