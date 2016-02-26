package org.denigma.preview

import akka.actor.ActorSystem
import akka.http.scaladsl.server.RouteResult
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.{ActorMaterializer, ActorFlowMaterializer}

/**
 * For running as kernel
 */
/*
object Main extends App
{
  implicit val system = ActorSystem()
  sys.addShutdownHook(system.shutdown())
  var main: ActorRef = system.actorOf(Props[MainActor])
  main ! AppMessages.Start(5553)
}
*/

object Main extends App with Routes {

  val host = "0.0.0.0"
  val port = 5553

  implicit val system = ActorSystem("my-system")
  //implicit val materializer: ActorFlowMaterializer = ActorFlowMaterializer()
  implicit val materializerStream: ActorMaterializer = ActorMaterializer()

  implicit val ec = system.dispatcher

  //val server: HttpExt = Http(system)
  val bindingFuture = Http().bindAndHandle(routes, host, port)
  println(s"starting server at $host:$port")
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ ⇒ system.terminate()) // and shutdown when done
}

