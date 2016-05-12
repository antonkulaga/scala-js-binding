package org.denigma.preview

import akka.http.extensions.security._
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server._
import akka.stream.scaladsl.Flow

import scala.concurrent.Future

class WebSockets(
                channel: String,
                makeChannel: (String, String) => Flow[Message, Message, _]
                ) extends  AuthDirectives with Directives with WithLoginRejections with WithRegistrationRejections
{

  /*
  def routes: Route =
    pathPrefix("connect") {
        parameters("channel", "username"){
          case (channel, username) =>
            println("attempt to connect")
            handleWebSocketMessages(makeChannel(channel, username))
        }
    }
    */
  def routes: Route =
    pathPrefix("channel"){
      pathPrefix(channel){
        parameter("username"){
          username=>
            println(s"username = $username")
            handleWebSocketMessages(makeChannel(channel, username))
        }
      }
    }
}


class WebSocketsWithLogin(
                  loginByName: (String, String) => Future[LoginResult],
                  loginByEmail: (String, String) => Future[LoginResult],
                  makeChannel: (String, String) => Flow[Message, Message, Unit]
                ) extends  AuthDirectives with Directives with WithLoginRejections with WithRegistrationRejections
{
  def routes: Route =
    pathPrefix("channel"){
      pathPrefix("test"){
        parameter("username"){
          username=>
            println(s"username = $username")
            handleWebSocketMessages(makeChannel("notebook", username))
        }
      }
    }
}
