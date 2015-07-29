package org.denigma.preview

import akka.actor._
import akka.http.scaladsl.{Http, _}
import akka.stream.ActorMaterializer

/**
 * Main actor that encapsulates main application logic and starts the server
 */
class MainActor  extends Actor with ActorLogging with Routes
{
  implicit val system = context.system
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val server: HttpExt = Http(context.system)

  override def receive: Receive = {
    case AppMessages.Start(port)=>
      val host = "localhost"
      server.bindAndHandle(routes, host, port)
      log.info(s"starting server at $host:$port")


    case AppMessages.Stop=>  onStop()
  }

  def onStop() = {
    log.info("Main actor has been stoped...")
  }

  override def postStop() = {
    onStop()
  }


}
