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


object CloseCode extends Enumeration {
  val CLOSE_NORMAL = Value(1000)
  val CLOSE_GOING_AWAY = Value(1001)
  val CLOSE_PROTOCOL_ERROR = Value(1002)
  val CLOSE_UNSUPPORTED = Value(1003)
  val CLOSE_NO_STATUS = Value(1005)
  val CLOSE_ABNORMAL = Value(1006)
  val CLOSE_TOO_LARGE = Value(1009)
}

/*
*
*
* 1000	CLOSE_NORMAL	Normal closure; the connection successfully completed whatever purpose for which it was created.
1001	CLOSE_GOING_AWAY	The endpoint is going away, either because of a server failure or because the browser is navigating away from the page that opened the connection.
1002	CLOSE_PROTOCOL_ERROR	The endpoint is terminating the connection due to a protocol error.
1003	CLOSE_UNSUPPORTED	The connection is being terminated because the endpoint received data of a type it cannot accept (for example, a text-only endpoint received binary data).
1004	 	Reserved. A meaning might be defined in the future.
1005	CLOSE_NO_STATUS	Reserved.  Indicates that no status code was provided even though one was expected.
1006	CLOSE_ABNORMAL	Reserved. Used to indicate that a connection was closed abnormally (that is, with no close frame being sent) when a status code is expected.
1007	 	The endpoint is terminating the connection because a message was received that contained inconsistent data (e.g., non-UTF-8 data within a text message).
1008	 	The endpoint is terminating the connection because it received a message that violates its policy. This is a generic status code, used when codes 1003 and 1009 are not suitable.
1009	CLOSE_TOO_LARGE	The endpoint is terminating the connection because a data frame was received that is too large.
1010	 	The client is terminating the connection because it expected the server to negotiate one or more extension, but the server didn't.
1011	 	The server is terminating the connection because it encountered an unexpected condition that prevented it from fulfilling the request.
1012–1014	 	Reserved for future use by the WebSocket standard.
1015	 	Reserved. Indicates that the connection was closed due to a failure to perform a TLS handshake (e.g., the server certificate can't be verified).
1016–1999	 	Reserved for future use by the WebSocket standard.
2000–2999	 	Reserved for use by WebSocket extensions.
3000–3999	 	Available for use by libraries and frameworks. May not be used by applications.
4000–4999	 	Available for use by applications.
*
* */

object WebSocketStorage  extends WebSocketStorage
class WebSocketStorage
{
  var sockets: Map[String, WebSocket] = Map.empty[String, WebSocket]

  def apply(url: String): WebSocket = sockets.getOrElse(url, add(url))

  def init(url: String)(fun: (WebSocket => WebSocket)): WebSocket = sockets.getOrElse(url, fun(add(url)))

  def add(url: String): WebSocket = {
    val w: WebSocket = new WebSocket(url)
    this.sockets = this.sockets.+(url -> w)
    w
  }

  def remove(url: String): this.type = sockets.get(url) match {
    case Some(w) =>
      w.close(CloseCode.CLOSE_NORMAL.id,"logout")
      this
    case None =>
      dom.console.log(s"nothing to remove with url $url")
      this
  }
}