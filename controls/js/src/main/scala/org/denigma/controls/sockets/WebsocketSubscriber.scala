package org.denigma.controls.sockets

import org.denigma.binding.binders.Events
import org.scalajs.dom
import org.scalajs.dom.Blob
import org.scalajs.dom.raw.{ErrorEvent, Event, MessageEvent, WebSocket}
import rx._
import org.denigma.binding.extensions._
//import rx.Ctx.Owner.voodoo
import rx.Ctx.Owner.Unsafe.Unsafe

import scala.scalajs.js.typedarray.ArrayBuffer


class SimpleSubscriber(val channel: String, val username: String) extends WebSocketSubscriber{

  override def getWebSocketUri(username: String): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}/channel/$channel?username=$username"
  }

  override def initWebSocket(url: String): WebSocket = WebSocketStorage(url)

  urlOpt() = Option(getWebSocketUri(username))
}

object WebSocketSubscriber
{
  def apply(channel: String, username: String): WebSocketSubscriber = new SimpleSubscriber(channel, username)
}


trait WebSocketSubscriber
{

  val channel: String

  val webSocketOpt: Var[Option[WebSocket]] = Var(None)

  val connected: Rx[Boolean] = webSocketOpt.map(_.isDefined)
  val urlOpt: Var[Option[String]] = Var(None)

  protected def onUrlChange(url: Option[String]): Unit = url match{
    case Some(u)=> webSocketOpt() = Option(initWebSocket(u))
    case None =>
      //println("None")
      webSocketOpt() = None
  }

  urlOpt.onChange(this.onUrlChange)

  protected def getWebSocketUri(username: String): String

  protected def initWebSocket(url: String): WebSocket

  def send(message: String): Unit = webSocketOpt.now match {
    case Some(w)=>  w.send(message)
    case None=> dom.console.error("websocket is not initialized")
  }

  def send(message: ArrayBuffer): Unit = webSocketOpt.now match {
    case Some(w)=>
      println("sending arraybuffer message")
      w.send(message)
    case None=> dom.console.error("websocket is not initialized")
  }

  def send(message: Blob): Unit = webSocketOpt.now match {
    case Some(w)=>  w.send(message)
    case None=> dom.console.error("websocket is not initialized")
  }

  protected def subscribe(w: WebSocket) = {
    w.onopen = {(event: Event) ⇒ onOpen() = event}
    w.onerror = {(event: ErrorEvent) ⇒ onError() = event}
    w.onmessage = {(event: MessageEvent) ⇒ onMessage() = event}
    w.onclose = {(event: Event) ⇒  onClose() = event}
  }


  webSocketOpt.onChange{
    case Some(w) => subscribe(w)
    case None => println("webSocketOpt changed to None") // TODO: decide what to do here
  }

  val onOpen: rx.Var[Event] = Var(Events.createEvent())
  val onMessage: rx.Var[dom.MessageEvent] = Var(Events.createMessageEvent())
  lazy val onError: rx.Var[dom.ErrorEvent] = Var(Events.createErrorEvent())
  val onClose: rx.Var[Event] = Var(Events.createEvent())
  val opened: Var[Boolean] = Var(false)

  onOpen.triggerLater{
    println("opened")
    opened() = true
  }
  onClose.triggerLater(opened() = false)

}
