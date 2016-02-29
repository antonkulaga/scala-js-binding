package org.denigma.preview

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer


object Main extends App with Routes {

  val host = "0.0.0.0"
  val port = 5553

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val server: HttpExt = Http(system)
  val bindingFuture = server.bindAndHandle(routes, host, port)(materializer)
  println(s"starting server at $host:$port")
}

