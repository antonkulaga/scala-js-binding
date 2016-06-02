package org.denigma.preview

import java.nio.ByteBuffer

import boopickle.DefaultBasic._
import org.denigma.controls.sockets._
import org.denigma.preview.messages.WebMessages
import org.denigma.preview.messages.WebMessages.Message

import scala.concurrent.Future

//import org.denigma.kappa.notebook.storage.WebSocketStorage
import org.scalajs.dom
import org.scalajs.dom.raw.WebSocket
import rx.Ctx.Owner.Unsafe.Unsafe
import rx.Var
import org.denigma.binding.extensions._

class WebMessagesTransport(val channel: String, username: String) extends WebSocketTransport1
{

  type Input = WebMessages.Message


  override val connected = Var(false)

  input.triggerLater{
    onInput(input.now)
  }

   protected def onInput(inp: Input) = inp match {
    case WebMessages.Connected(uname, ch, list) if uname==username //&& ch == channel
    =>
      println(s"connection of user $username to $channel established")
      connected() = true
    case WebMessages.Disconnected(uname, ch, list) if uname==username
      //&& ch == channel
    =>
      println(s"user $username diconnected from $channel")
      connected() = false

    case _=> //do nothing
  }

 override def send(message: Output): Unit = if(connected.now) {
    val mes = bytes2message(pickle(message))
    send(mes)
  } else {
   connected.triggerOnce{
     case true =>
       send(message)
     case false =>
     }
  }


  override protected def closeHandler() = {
    println("websocket closed")
    connected() = false
    opened() = false
  }

  override def getWebSocketUri(username: String): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}/channel/$channel?username=$username"
  }

  def open(): Unit = {
    urlOpt() = Option(getWebSocketUri(username))
  }

  override def initWebSocket(url: String): WebSocket = WebSocketStorage(url)

  override def emptyInput: Message = WebMessages.EmptyMessage

  override protected def pickle(message: Output): ByteBuffer = {
    Pickle.intoBytes(message)
  }

  override protected def unpickle(bytes: ByteBuffer): Message = {
    Unpickle[Input].fromBytes(bytes)
  }
}